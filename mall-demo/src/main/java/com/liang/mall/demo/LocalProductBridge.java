package com.liang.mall.demo;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.msb.common.utils.R;
import com.msb.mall.feign.ProductFeignService;
import com.msb.mall.product.dao.SkuInfoDao;
import com.msb.mall.product.entity.SkuInfoEntity;
import com.liang.mall.demo.dao.DemoCatalogDao;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

/** Local adapter of the original cart's product contract; no internal HTTP hop. */
@Component
public class LocalProductBridge implements ProductFeignService {
    private static final List<Long> SKUS=Arrays.asList(921001L,921002L,921003L);
    private final SkuInfoDao products;
    private final DemoCatalogDao details;
    public LocalProductBridge(SkuInfoDao products,DemoCatalogDao details) { this.products=products; this.details=details; }
    private SkuInfoEntity product(Long id) {
        if (!SKUS.contains(id)) { throw new ResponseStatusException(HttpStatus.NOT_FOUND,"演示商品不存在"); }
        SkuInfoEntity product=products.selectById(id);
        if (product==null) { throw new ResponseStatusException(HttpStatus.NOT_FOUND,"演示商品不存在"); }
        return product;
    }
    @Override public R demoProducts() {
        return R.ok().put("dataMode","sample").put("products",products.selectList(new QueryWrapper<SkuInfoEntity>().in("sku_id",SKUS).orderByAsc("sku_id")));
    }
    @Override public R info(Long id) { return R.ok().put("skuInfoJSON",JSON.toJSONString(product(id))); }
    @Override public List<String> getSkuSaleAttrs(Long id) { product(id); return products.getSkuSaleAttrs(id); }
    @Override public R demoProduct(Long id) {
        SkuInfoEntity product=product(id);
        Map<String,Object> item=new LinkedHashMap<>(); item.put("info",product);
        Map<String,List<Map<String,Object>>> grouped=new LinkedHashMap<>();
        for (Map<String,Object> row:details.attributes(product.getSpuId(),product.getCatalogId())) {
            String group=row.get("groupName").toString();
            Map<String,Object> attr=new LinkedHashMap<>(); attr.put("attrName",row.get("attrName")); attr.put("attrValue",row.get("attrValue"));
            grouped.computeIfAbsent(group,k->new ArrayList<>()).add(attr);
        }
        List<Map<String,Object>> groups=new ArrayList<>();
        grouped.forEach((name,attrs)-> { Map<String,Object> group=new LinkedHashMap<>(); group.put("groupName",name); group.put("baseAttrs",attrs); groups.add(group); });
        item.put("baseAttrs",groups); item.put("seckillVO",null);
        return R.ok().put("dataMode","sample").put("item",item);
    }
}
