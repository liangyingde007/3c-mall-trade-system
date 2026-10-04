package com.msb.mall.feign;

import com.msb.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(value = "mall-product", url = "${mall.service.product-url:}")
public interface ProductFeignService {

    @GetMapping("/demo/products")
    R demoProducts();

    @GetMapping("/demo/products/{skuId}")
    R demoProduct(@PathVariable("skuId") Long skuId);

    @RequestMapping("/product/skuinfo/info/{skuId}")
    //@RequiresPermissions("product:skuinfo:info")
    public R info(@PathVariable("skuId") Long skuId);


    @GetMapping("/product/skuinfo/saleAttrs/{skuId}")
    public List<String> getSkuSaleAttrs(@PathVariable("skuId") Long skuId);
}
