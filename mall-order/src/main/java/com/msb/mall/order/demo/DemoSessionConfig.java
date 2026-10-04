package com.msb.mall.order.demo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@ConditionalOnProperty(name="mall.demo.enabled",havingValue="true")
@Configuration
public class DemoSessionConfig {
    @Bean public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookie = new DefaultCookieSerializer();
        cookie.setCookieName("msbsession"); cookie.setCookiePath("/");
        cookie.setUseHttpOnlyCookie(true); cookie.setSameSite("Lax");
        return cookie;
    }
}
