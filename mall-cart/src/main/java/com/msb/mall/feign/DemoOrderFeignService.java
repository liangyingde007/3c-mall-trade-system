package com.msb.mall.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@FeignClient(name="mall-order-demo",url="${mall.service.order-url:http://127.0.0.1:9012}")
public interface DemoOrderFeignService {
    @PostMapping(value="/demo/orders/confirmation",consumes="application/json")
    Map<String,Object> confirm(@RequestHeader("Cookie") String cookie,@RequestBody Map<String,Object> body);
    @PostMapping(value="/demo/orders",consumes="application/json")
    Map<String,Object> create(@RequestHeader("Cookie") String cookie,@RequestBody Map<String,Object> body);
    @GetMapping("/demo/orders") List<Map<String,Object>> list(@RequestHeader("Cookie") String cookie);
    @GetMapping("/demo/orders/{sn}") Map<String,Object> detail(@RequestHeader("Cookie") String cookie,@PathVariable("sn") String sn);
    @PostMapping(value="/demo/orders/{sn}/cancel",consumes="application/json")
    Map<String,Object> cancel(@RequestHeader("Cookie") String cookie,@PathVariable("sn") String sn,@RequestBody Map<String,Object> body);
}
