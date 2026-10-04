package com.msb.mall.controller;

import com.msb.mall.Interceptor.AuthInterceptor;
import com.msb.mall.feign.ProductFeignService;
import com.msb.mall.service.ICartService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.*;

/** Session-scoped demo entry; price and totals always come from original services. */
@RestController
@RequestMapping("/demo/api")
@ConditionalOnProperty(name = "mall.demo.enabled", havingValue = "true")
public class DemoCartController {
    private static final Set<Long> DEMO_SKUS = new HashSet<>(Arrays.asList(921001L,921002L,921003L));
    private final ICartService carts;
    private final ProductFeignService products;

    public DemoCartController(ICartService carts, ProductFeignService products) {
        this.carts = carts; this.products = products;
    }

    @GetMapping("/catalog")
    public Object catalog() { return products.demoProducts(); }

    @GetMapping("/products/{skuId}")
    public Object detail(@PathVariable Long skuId) { requireSku(skuId); return products.demoProduct(skuId); }

    @GetMapping("/cart")
    public Map<String,Object> cart() {
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("dataMode", "sample");
        result.put("visitorId", AuthInterceptor.threadLocal.get().getId().toString());
        result.put("cart", carts.getCartList());
        return result;
    }

    @PostMapping(value="/cart", consumes="application/json")
    public Map<String,Object> add(@Valid @RequestBody AddItem request) throws Exception {
        requireSku(request.skuId);
        carts.addCart(request.skuId, request.quantity);
        return cart();
    }

    @PatchMapping(value="/cart/{skuId}", consumes="application/json")
    public Map<String,Object> update(@PathVariable Long skuId, @Valid @RequestBody ChangeItem request) {
        requireSku(skuId);
        if (request.quantity == null && request.checked == null) {
            throw new IllegalArgumentException("请指定数量或勾选状态");
        }
        if (carts.updateCartItem(skuId, request.quantity, request.checked) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "购物车中没有这件商品");
        }
        return cart();
    }

    @DeleteMapping("/cart/{skuId}")
    public Map<String,Object> remove(@PathVariable Long skuId) {
        requireSku(skuId);
        if (!carts.removeCartItem(skuId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "购物车中没有这件商品");
        }
        return cart();
    }

    private void requireSku(Long skuId) {
        if (!DEMO_SKUS.contains(skuId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "演示商品不存在");
        }
    }

    public static class AddItem {
        @NotNull public Long skuId;
        @NotNull @Min(1) @Max(20) public Integer quantity;
    }

    public static class ChangeItem {
        @Min(1) @Max(20) public Integer quantity;
        public Boolean checked;
    }
}
