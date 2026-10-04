package com.msb.mall.order.demo.dao;

import com.msb.mall.order.entity.OrderEntity;
import org.apache.ibatis.annotations.*;
import java.util.Map;

public interface DemoInventoryDao {
    @Select("SELECT sku_id AS skuId, spu_id AS spuId, sku_title AS title, sku_default_img AS image, price FROM pms_sku_info WHERE sku_id=#{skuId}")
    Map<String,Object> product(@Param("skuId") Long skuId);

    // Same conditional SQL as mall-ware/mapper/ware/WareSkuDao.xml; same demo DB transaction as the order.
    @Update("UPDATE wms_ware_sku SET stock_locked=stock_locked+#{count} WHERE sku_id=#{skuId} AND ware_id=#{wareId} AND stock-stock_locked>=#{count}")
    int lock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("count") Integer count);

    @Update("UPDATE wms_ware_sku SET stock_locked=stock_locked-#{count} WHERE sku_id=#{skuId} AND ware_id=#{wareId} AND stock_locked>=#{count}")
    int release(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("count") Integer count);

    @Select("SELECT * FROM oms_order WHERE order_sn=#{sn} AND member_id=#{memberId} AND note='LYD_DEMO_D4B' FOR UPDATE")
    OrderEntity ownedForUpdate(@Param("sn") String sn, @Param("memberId") Long memberId);
}
