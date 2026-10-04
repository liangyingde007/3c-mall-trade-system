package com.msb.mall.config;

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

    /**
     * 自定义Cookie的配置
     * @return
     */
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

    /**
     * 对存储在Redis中的数据指定序列化的方式
     * @return
     */
    @Bean
    public RedisSerializer<Object> redisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }
}
