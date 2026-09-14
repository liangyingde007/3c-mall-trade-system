package com.msb.mall.service;

import com.msb.mall.dto.SeckillSkuRedisDto;

import java.util.List;

public interface SeckillService {
    void uploadSeckillSku3Days();

    List<SeckillSkuRedisDto> getCurrentSeckillSkus();

    SeckillSkuRedisDto getSeckillSessionBySkuId(Long skuId);

    String kill(String killId, String code, Integer num);
}
