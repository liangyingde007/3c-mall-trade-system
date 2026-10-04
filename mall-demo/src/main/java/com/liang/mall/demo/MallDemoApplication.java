package com.liang.mall.demo;

import com.msb.mall.controller.DemoCartController;
import com.msb.mall.controller.DemoWebController;
import com.msb.mall.service.impl.CartServiceImpl;
import com.msb.mall.order.demo.DemoOrderService;
import com.msb.mall.order.demo.DemoOrderExpiry;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

// The original application classes and management controllers are never scanned.
@SpringBootApplication
@EnableScheduling
@EnableRedisHttpSession(redisNamespace="lyd:public-demo:session")
@MapperScan({"com.msb.mall.order.dao", "com.msb.mall.order.demo.dao", "com.msb.mall.product.dao", "com.liang.mall.demo.dao"})
@Import({DemoCartController.class, DemoWebController.class, CartServiceImpl.class, DemoOrderService.class, DemoOrderExpiry.class})
public class MallDemoApplication {
    public static void main(String[] args) { SpringApplication.run(MallDemoApplication.class,args); }
}
