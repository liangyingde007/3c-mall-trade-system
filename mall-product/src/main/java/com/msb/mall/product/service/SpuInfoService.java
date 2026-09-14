package com.msb.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.msb.common.utils.PageUtils;
import com.msb.mall.product.entity.SpuInfoEntity;
import com.msb.mall.product.vo.OrderItemSpuInfoVO;
import com.msb.mall.product.vo.SpuInfoVO;

import java.util.List;
import java.util.Map;

/**
 * spu信息
 *
 * @author dpb
 * @email dengpbs@163.com
 * @date 2021-11-24 14:46:05
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuInfoVO spuInfoVo);

    PageUtils queryPageByCondition(Map<String, Object> params);

    void up(Long spuId);

    List<OrderItemSpuInfoVO> getOrderItemSpuInfoBySpuId(Long[] spuIds);
}

