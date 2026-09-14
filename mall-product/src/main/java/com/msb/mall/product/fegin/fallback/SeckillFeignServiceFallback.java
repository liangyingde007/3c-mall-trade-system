package com.msb.mall.product.fegin.fallback;

import com.msb.common.exception.BizCodeEnume;
import com.msb.common.utils.R;
import com.msb.mall.product.fegin.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SeckillFeignServiceFallback implements SeckillFeignService {
    @Override
    public R getSeckillSessionBySkuId(Long skuId) {
        log.error("熔断降级....SeckillFeignService:{}",skuId);
        return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(),BizCodeEnume.UNKNOW_EXCEPTION.getMsg());
    }
}
