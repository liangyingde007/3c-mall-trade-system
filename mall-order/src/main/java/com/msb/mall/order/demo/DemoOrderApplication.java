package com.msb.mall.order.demo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/** Minimal demo entry: original order entities/mappers, without payment or MQ consumers. */
@ConditionalOnProperty(name="mall.demo.enabled",havingValue="true")
@SpringBootApplication(scanBasePackages = "com.msb.mall.order.demo")
@MapperScan({"com.msb.mall.order.dao", "com.msb.mall.order.demo.dao"})
@EnableRedisHttpSession
@EnableScheduling
public class DemoOrderApplication {
    public static void main(String[] args) { SpringApplication.run(DemoOrderApplication.class, args); }
}
