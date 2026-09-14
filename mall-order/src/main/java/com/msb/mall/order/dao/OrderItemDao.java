package com.msb.mall.order.dao;

import com.msb.mall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author dpb
 * @email dengpbs@163.com
 * @date 2021-11-24 19:48:00
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
