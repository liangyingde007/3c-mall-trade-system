package com.msb.mall.order.dao;

import com.msb.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author dpb
 * @email dengpbs@163.com
 * @date 2021-11-24 19:48:00
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    OrderEntity getOrderByOrderSn(@Param("orderSn") String orderSn);

    void updateOrderStatus(@Param("orderSn") String orderSn, @Param("status") Integer status);

    /**
     * Compare-And-Set style order state transition.
     * Only changes the order when it is still in the expected state.
     */
    int updateStatusIfCurrent(@Param("orderSn") String orderSn,
                              @Param("expectedStatus") Integer expectedStatus,
                              @Param("targetStatus") Integer targetStatus);
}
