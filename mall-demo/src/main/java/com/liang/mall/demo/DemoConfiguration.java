package com.liang.mall.demo;

import com.msb.mall.Interceptor.AuthInterceptor;
import org.springframework.context.annotation.*;
import org.springframework.session.web.http.*;
import org.springframework.web.servlet.config.annotation.*;
import java.util.concurrent.*;

@Configuration
public class DemoConfiguration implements WebMvcConfigurer {
    @Override public void addInterceptors(InterceptorRegistry registry) {
        // Static assets and health probes don't create a visitor or write Redis sessions.
        registry.addInterceptor(new AuthInterceptor(true)).addPathPatterns("/demo/api/**");
    }
    @Bean public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookie=new DefaultCookieSerializer();
        cookie.setCookieName("msbsession"); cookie.setCookiePath("/");
        cookie.setUseHttpOnlyCookie(true); cookie.setSameSite("Lax");
        return cookie;
    }
    @Bean(destroyMethod="shutdown") public ThreadPoolExecutor executor() {
        return new ThreadPoolExecutor(2,4,30,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(32),new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
