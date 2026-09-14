package com.msb.mall.product.dao;

import com.msb.mall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author dpb
 * @email dengpbs@163.com
 * @date 2021-11-24 14:46:05
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    void updateSpuStatusUp(@Param("spuId") Long spuId,@Param("code") int code);
}
