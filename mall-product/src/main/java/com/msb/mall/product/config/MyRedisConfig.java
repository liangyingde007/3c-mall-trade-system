package com.msb.mall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRedisConfig {

    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties){
        Config config = new Config();
        // 配置连接的信息
        SingleServerConfig server = config.useSingleServer()
                .setAddress((redisProperties.isSsl() ? "rediss://" : "redis://")
                        + redisProperties.getHost() + ":" + redisProperties.getPort())
                .setDatabase(redisProperties.getDatabase());
        if (redisProperties.getPassword() != null && !redisProperties.getPassword().isEmpty()) {
            server.setPassword(redisProperties.getPassword());
        }
        RedissonClient redissonClient = Redisson.create(config);
        return  redissonClient;
    }
}
