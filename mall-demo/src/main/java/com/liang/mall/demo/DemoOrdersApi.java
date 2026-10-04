package com.liang.mall.demo;

import com.msb.mall.Interceptor.AuthInterceptor;
import com.msb.mall.order.demo.DemoOrderService;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.*;

@RestController
@RequestMapping("/demo/api")
public class DemoOrdersApi {
    private final DemoOrderService orders;
    public DemoOrdersApi(DemoOrderService orders) { this.orders=orders; }
    @PostMapping(value="/checkout",consumes="application/json")
    public DemoOrderService.Quote confirm() { return orders.confirm(AuthInterceptor.threadLocal.get()); }
    @PostMapping(value="/orders",consumes="application/json")
    public Map<String,Object> create(@Valid @RequestBody Submission body) { return orders.create(AuthInterceptor.threadLocal.get(),body.token); }
    @GetMapping("/orders") public List<Map<String,Object>> list() { return orders.list(AuthInterceptor.threadLocal.get()); }
    @GetMapping("/orders/{sn}") public Map<String,Object> detail(@PathVariable String sn) { return orders.detail(AuthInterceptor.threadLocal.get(),sn); }
    @PostMapping(value="/orders/{sn}/cancel",consumes="application/json")
    public Map<String,Object> cancel(@PathVariable String sn) { return orders.cancel(AuthInterceptor.threadLocal.get(),sn); }
    public static class Submission { @NotBlank @Pattern(regexp="[a-f0-9]{32}") public String token; }
}
