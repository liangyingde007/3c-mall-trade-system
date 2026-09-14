package com.msb.mall.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class MyFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("网关-全局异常过滤器:");
        HttpRequest httpRequest = exchange.getRequest();
        MediaType contentType = httpRequest.getHeaders().getContentType();
        Mono<Void> filter = null;
        try {
            filter = chain.filter(exchange);
        }catch (Exception e){
            log.info("网关-全局异常过滤器: 出现了异常{}",e.getMessage());
            e.printStackTrace();
        }

        ServerHttpResponse response = exchange.getResponse();
        log.info("-----------------------------------------------");
        return filter;
    }
}
