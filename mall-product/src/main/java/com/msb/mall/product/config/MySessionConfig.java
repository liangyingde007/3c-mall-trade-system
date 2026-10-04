package com.msb.mall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class MySessionConfig {
    @Value("${mall.demo.enabled:false}")
    private boolean demoEnabled;

    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        if (!demoEnabled) {
            cookieSerializer.setDomainName("msb.com");
        }
        cookieSerializer.setCookiePath("/");
        cookieSerializer.setSameSite("Lax");
        cookieSerializer.setUseHttpOnlyCookie(true);
        cookieSerializer.setCookieName("msbsession");
        return cookieSerializer;
    }

    @Bean
    public RedisSerializer<Object> redisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }
}
