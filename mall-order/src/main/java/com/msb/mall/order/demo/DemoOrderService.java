package com.msb.mall.order.demo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.msb.common.constant.CartConstant;
import com.msb.common.constant.OrderConstant;
import com.msb.common.vo.MemberVO;
import com.msb.mall.order.dao.OrderDao;
import com.msb.mall.order.dao.OrderItemDao;
import com.msb.mall.order.demo.dao.DemoInventoryDao;
import com.msb.mall.order.entity.OrderEntity;
import com.msb.mall.order.entity.OrderItemEntity;
import com.msb.mall.order.vo.OrderItemVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@ConditionalOnProperty(name="mall.demo.enabled",havingValue="true")
@Service
public class DemoOrderService {
    private static final List<Long> SKUS = Arrays.asList(921001L, 921002L, 921003L);
    private static final long WARE = 930001L;
    private static final String MARK = "LYD_DEMO_D4B";
    private final OrderDao orders;
    private final OrderItemDao items;
    private final DemoInventoryDao inventory;
    private final StringRedisTemplate redis;
    @Value("${mall.demo.order-ttl-seconds:900}") private long orderTtl;

    // Atomically validate the cart snapshot, then reuse the original compare-and-delete Token expression.
    private static final String CONSUME =
        "if redis.call('get',KEYS[1])~=ARGV[1] then return 0 end; " +
        "for i=2,4 do if (redis.call('hget',KEYS[2],ARGV[i]) or '')~=ARGV[i+3] then return -1 end end; " +
        "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";

    public DemoOrderService(OrderDao orders, OrderItemDao items, DemoInventoryDao inventory, StringRedisTemplate redis) {
        this.orders=orders; this.items=items; this.inventory=inventory; this.redis=redis;
    }

    public Quote confirm(MemberVO member) {
        Quote quote = snapshot(member.getId());
        if (quote.items.isEmpty()) { throw fail(HttpStatus.CONFLICT, "请先勾选需要演示下单的商品"); }
        quote.token=UUID.randomUUID().toString().replace("-", "");
        quote.expiresInSeconds=600;
        redis.opsForValue().set(quoteKey(member.getId(),quote.token), JSON.toJSONString(quote), 10, TimeUnit.MINUTES);
        redis.opsForValue().set(tokenKey(member.getId()),quote.token,10,TimeUnit.MINUTES);
        return publicQuote(quote);
    }

    @Transactional(rollbackFor=Exception.class)
    public Map<String,Object> create(MemberVO member, String token) {
        Quote quote=JSON.parseObject(redis.opsForValue().get(quoteKey(member.getId(),token)),Quote.class);
        if (quote==null) { throw fail(HttpStatus.CONFLICT,"确认已过期或已提交，请重新确认订单"); }
        // Re-read prices before consumption. Never accept client titles, amounts, inventory or ownership.
        Quote current=snapshot(member.getId());
        if (!JSON.toJSONString(current.items).equals(JSON.toJSONString(quote.items))) {
            throw fail(HttpStatus.CONFLICT,"商品价格或购物车已变化，请重新确认订单");
        }
        Object[] arguments={token,"921001","921002","921003",quote.cartSnapshot.get("921001"),quote.cartSnapshot.get("921002"),quote.cartSnapshot.get("921003")};
        Long consumed=redis.execute(new DefaultRedisScript<Long>(CONSUME,Long.class),
                Arrays.asList(tokenKey(member.getId()),CartConstant.CART_PERFIX+member.getId()),arguments);
        if (consumed==null || consumed==0) { throw fail(HttpStatus.CONFLICT,"订单已提交或确认已过期，请重新确认"); }
        if (consumed<0) { throw fail(HttpStatus.CONFLICT,"购物车已变化，请重新确认订单"); }

        OrderEntity order=new OrderEntity();
        order.setOrderSn("DEMO"+IdWorker.getTimeId()); order.setMemberId(member.getId());
        order.setMemberUsername("demo-visitor"); order.setCreateTime(new Date()); order.setModifyTime(new Date());
        order.setStatus(0); order.setTotalAmount(quote.totalAmount); order.setPayAmount(quote.totalAmount);
        order.setFreightAmount(BigDecimal.ZERO); order.setNote(MARK); order.setDeleteStatus(0);
        order.setReceiverName("演示收件人"); order.setReceiverDetailAddress("示例地址，不寄送商品");
        orders.insert(order);
        for (Line line:quote.items) {
            OrderItemEntity item=new OrderItemEntity();
            item.setOrderId(order.getId()); item.setOrderSn(order.getOrderSn()); item.setSkuId(line.skuId);
            item.setSpuId(line.spuId); item.setSkuName(line.title); item.setSkuPic(line.image);
            item.setSkuQuantity(line.quantity); item.setSkuPrice(line.unitPrice); item.setRealAmount(line.lineTotal);
            items.insert(item);
            if (inventory.lock(line.skuId,WARE,line.quantity)!=1) {
                throw fail(HttpStatus.CONFLICT,"示例库存不足，订单未创建。请减少数量后重新确认");
            }
        }
        // The SQL transaction commits both rows and all SKU locks. No distributed transaction or MQ claim.
        return view(order);
    }

    public List<Map<String,Object>> list(MemberVO member) {
        List<Map<String,Object>> result=new ArrayList<>();
        for (OrderEntity order:orders.selectList(new QueryWrapper<OrderEntity>()
                .eq("member_id",member.getId()).eq("note",MARK).orderByDesc("id").last("LIMIT 20"))) {
            result.add(view(order));
        }
        return result;
    }

    public Map<String,Object> detail(MemberVO member,String sn) {
        OrderEntity order=orders.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn",sn).eq("member_id",member.getId()).eq("note",MARK));
        if (order==null) { throw fail(HttpStatus.NOT_FOUND,"演示订单不存在"); }
        return view(order);
    }

    @Transactional(rollbackFor=Exception.class)
    public Map<String,Object> cancel(MemberVO member,String sn) { return close(sn,member.getId()); }

    @Transactional(rollbackFor=Exception.class)
    public void expirePending() {
        Date cutoff=new Date(System.currentTimeMillis()-orderTtl*1000);
        List<OrderEntity> expired=orders.selectList(new QueryWrapper<OrderEntity>().eq("note",MARK)
                .eq("status",0).le("create_time",cutoff).orderByAsc("id").last("LIMIT 50"));
        for (OrderEntity order:expired) { close(order.getOrderSn(),order.getMemberId()); }
    }

    private Map<String,Object> close(String sn,Long owner) {
        OrderEntity order=inventory.ownedForUpdate(sn,owner);
        if (order==null) { throw fail(HttpStatus.NOT_FOUND,"演示订单不存在"); }
        if (order.getStatus()==4) { return view(order); }
        // Original OrderDao updateStatusIfCurrent; the row lock protects ownership and release-once semantics.
        if (orders.updateStatusIfCurrent(sn,0,4)!=1) { throw fail(HttpStatus.CONFLICT,"当前订单状态无法取消"); }
        for (OrderItemEntity item:orderItems(sn)) {
            if (inventory.release(item.getSkuId(),WARE,item.getSkuQuantity())!=1) {
                throw new IllegalStateException("Demo stock release invariant failed");
            }
        }
        order.setStatus(4); order.setModifyTime(new Date());
        return view(order);
    }

    private Quote snapshot(Long memberId) {
        Quote quote=new Quote();
        List<Object> raw=redis.opsForHash().multiGet(CartConstant.CART_PERFIX+memberId,new ArrayList<Object>(Arrays.asList("921001","921002","921003")));
        for (int index=0;index<SKUS.size();index++) {
            Long sku=SKUS.get(index); String value=raw.get(index)==null?"":raw.get(index).toString();
            quote.cartSnapshot.put(sku.toString(),value);
            if (value.isEmpty()) { continue; }
            OrderItemVo cart=JSON.parseObject(value,OrderItemVo.class);
            if (!cart.isCheck()) { continue; }
            if (cart.getCount()==null || cart.getCount()<1 || cart.getCount()>20) {
                throw fail(HttpStatus.CONFLICT,"请先检查购物车中的商品数量");
            }
            Map<String,Object> product=inventory.product(sku);
            if (product==null) { throw fail(HttpStatus.CONFLICT,"示例商品已不可用"); }
            Line line=new Line(); line.skuId=sku; line.spuId=((Number)product.get("spuId")).longValue();
            line.title=product.get("title").toString(); line.image=product.get("image").toString();
            line.quantity=cart.getCount(); line.unitPrice=new BigDecimal(product.get("price").toString());
            line.lineTotal=line.unitPrice.multiply(BigDecimal.valueOf(line.quantity));
            quote.items.add(line); quote.totalAmount=quote.totalAmount.add(line.lineTotal);
        }
        return quote;
    }

    private List<OrderItemEntity> orderItems(String sn) {
        return items.selectList(new QueryWrapper<OrderItemEntity>().eq("order_sn",sn).orderByAsc("sku_id"));
    }

    private Map<String,Object> view(OrderEntity order) {
        Map<String,Object> view=new LinkedHashMap<>();
        view.put("orderSn",order.getOrderSn()); view.put("status",order.getStatus());
        view.put("statusLabel",order.getStatus()==4?"已关闭（演示）":"待付款（演示）");
        view.put("totalAmount",order.getTotalAmount()); view.put("createdAt",order.getCreateTime().getTime());
        view.put("expiresAt",order.getCreateTime().getTime()+orderTtl*1000);
        List<Line> lines=new ArrayList<>();
        for (OrderItemEntity item:orderItems(order.getOrderSn())) {
            Line line=new Line(); line.skuId=item.getSkuId(); line.spuId=item.getSpuId(); line.title=item.getSkuName();
            line.image=item.getSkuPic(); line.quantity=item.getSkuQuantity(); line.unitPrice=item.getSkuPrice(); line.lineTotal=item.getRealAmount();
            lines.add(line);
        }
        view.put("items",lines); view.put("dataMode","sample"); return view;
    }

    private Quote publicQuote(Quote quote) {
        Quote publicView=new Quote(); publicView.token=quote.token; publicView.items=quote.items;
        publicView.totalAmount=quote.totalAmount; publicView.expiresInSeconds=quote.expiresInSeconds;
        publicView.cartSnapshot=null; return publicView;
    }

    private String tokenKey(Long id) { return "demo:"+OrderConstant.ORDER_TOKEN_PREFIX+":"+id; }
    private String quoteKey(Long id,String token) { return "demo:order:quote:"+id+":"+token; }
    private ResponseStatusException fail(HttpStatus status,String message) { return new ResponseStatusException(status,message); }

    public static class Quote {
        public String token;
        public int expiresInSeconds;
        public List<Line> items=new ArrayList<>();
        public BigDecimal totalAmount=BigDecimal.ZERO;
        public Map<String,String> cartSnapshot=new LinkedHashMap<>();
    }
    public static class Line {
        public Long skuId,spuId;
        public String title,image;
        public Integer quantity;
        public BigDecimal unitPrice,lineTotal;
    }
}
