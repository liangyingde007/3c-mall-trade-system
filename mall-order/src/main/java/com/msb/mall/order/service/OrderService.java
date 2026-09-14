package com.msb.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.msb.common.dto.SeckillOrderDto;
import com.msb.common.exception.NoStockExecption;
import com.msb.common.utils.PageUtils;
import com.msb.mall.order.entity.OrderEntity;
import com.msb.mall.order.fegin.MemberFeginService;
import com.msb.mall.order.vo.OrderConfirmVo;
import com.msb.mall.order.vo.OrderResponseVO;
import com.msb.mall.order.vo.OrderSubmitVO;
import com.msb.mall.order.vo.PayVo;

import java.util.Map;

/**
 * 订单
 *
 * @author dpb
 * @email dengpbs@163.com
 * @date 2021-11-24 19:48:00
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);


    /**
     * 获取订单确认页中需要获取的相关信息
     * @return
     */
    OrderConfirmVo confirmOrder();

    OrderResponseVO submitOrder(OrderSubmitVO vo) throws NoStockExecption;

    PayVo getOrderPay(String orderSn);

    void updateOrderStatus(String orderSn,Integer status);

    void handleOrderComplete(String orderSn);

    boolean markPaidIfPending(String orderSn);

    void quickCreateOrder(SeckillOrderDto orderDto);
}

