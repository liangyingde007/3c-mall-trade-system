package com.msb.mall.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;
import java.util.Map;

@RestControllerAdvice(assignableTypes = {DemoCartController.class,DemoOrderGateway.class})
public class DemoErrorAdvice {
    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class,
            IllegalArgumentException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String,String>> invalid(Exception exception) {
        String message = exception instanceof IllegalArgumentException
                ? exception.getMessage() : "请检查商品信息，数量应为 1 到 20 之间的整数";
        return ResponseEntity.badRequest().body(Collections.singletonMap("message", message));
    }

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<Map<String,String>> missing(org.springframework.web.server.ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatus()).body(Collections.singletonMap("message", exception.getReason()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> unavailable(Exception exception) {
        return ResponseEntity.status(503).body(Collections.singletonMap("message", "商品或购物车服务暂时不可用，请稍后重试"));
    }

    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<Map<String,String>> upstream(feign.FeignException exception) {
        int status=exception.status();
        String message="订单服务暂时不可用，请稍后重试";
        if (status>=400 && status<500) {
            try {
                com.alibaba.fastjson.JSONObject result=com.alibaba.fastjson.JSON.parseObject(exception.contentUTF8());
                if (result.getString("message")!=null) { message=result.getString("message"); }
            } catch (Exception ignored) { }
            return ResponseEntity.status(status).body(Collections.singletonMap("message",message));
        }
        return ResponseEntity.status(503).body(Collections.singletonMap("message",message));
    }
}
