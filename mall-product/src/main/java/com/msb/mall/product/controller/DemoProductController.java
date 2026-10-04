package com.msb.mall.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.msb.common.utils.R;
import com.msb.mall.product.entity.SkuInfoEntity;
import com.msb.mall.product.service.SkuInfoService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** Read-only entry to the isolated fictional catalog. */
@RestController
@RequestMapping("/demo/products")
@ConditionalOnProperty(name = "mall.demo.enabled", havingValue = "true")
public class DemoProductController {
    private static final List<Long> DEMO_SKUS = Arrays.asList(921001L, 921002L, 921003L);
    private final SkuInfoService skuInfoService;

    public DemoProductController(SkuInfoService skuInfoService) { this.skuInfoService = skuInfoService; }

    @GetMapping
    public R catalog() {
        return R.ok().put("dataMode", "sample").put("products", skuInfoService.list(
                new QueryWrapper<SkuInfoEntity>().in("sku_id", DEMO_SKUS).orderByAsc("sku_id")));
    }

    @GetMapping("/{skuId}")
    public R detail(@PathVariable Long skuId) throws ExecutionException, InterruptedException {
        if (!DEMO_SKUS.contains(skuId) || skuInfoService.getById(skuId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "演示商品不存在");
        }
        return R.ok().put("dataMode", "sample").put("item", skuInfoService.item(skuId));
    }
}
