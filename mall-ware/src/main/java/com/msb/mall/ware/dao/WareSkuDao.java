package com.msb.mall.ware.dao;

import com.msb.mall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author dpb
 * @email dengpbs@163.com
 * @date 2021-11-24 19:46:02
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    Long getSkuStock(Long skuId);

    List<WareSkuEntity> listHashStock(@Param("skuId") Long skuId);

    Integer lockSkuStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("count") Integer count);

}
