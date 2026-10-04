package com.msb.mall.controller;

import com.msb.mall.feign.DemoOrderFeignService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/demo/api")
@ConditionalOnProperty(name="mall.demo.enabled",havingValue="true")
public class DemoOrderGateway {
    private final DemoOrderFeignService orders;
    public DemoOrderGateway(DemoOrderFeignService orders) { this.orders=orders; }
    @PostMapping(value="/checkout",consumes="application/json")
    public Map<String,Object> confirm(HttpServletRequest request) { return orders.confirm(cookie(request),Collections.emptyMap()); }
    @PostMapping(value="/orders",consumes="application/json")
    public Map<String,Object> create(@RequestBody Map<String,Object> body,HttpServletRequest request) { return orders.create(cookie(request),body); }
    @GetMapping("/orders") public List<Map<String,Object>> list(HttpServletRequest request) { return orders.list(cookie(request)); }
    @GetMapping("/orders/{sn}") public Map<String,Object> detail(@PathVariable String sn,HttpServletRequest request) { return orders.detail(cookie(request),sn); }
    @PostMapping(value="/orders/{sn}/cancel",consumes="application/json")
    public Map<String,Object> cancel(@PathVariable String sn,HttpServletRequest request) { return orders.cancel(cookie(request),sn,Collections.emptyMap()); }
    private String cookie(HttpServletRequest request) { return request.getHeader("Cookie"); }
}
