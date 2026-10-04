package com.msb.mall.order.demo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.util.*;

@ConditionalOnProperty(name="mall.demo.enabled",havingValue="true")
@RestControllerAdvice(assignableTypes=DemoOrderController.class)
public class DemoOrderErrors {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String,String>> expected(ResponseStatusException error) {
        return ResponseEntity.status(error.getStatus()).body(Collections.singletonMap("message",error.getReason()));
    }
    @ExceptionHandler({MethodArgumentNotValidException.class,HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String,String>> invalid(Exception error) {
        return ResponseEntity.badRequest().body(Collections.singletonMap("message","订单信息不完整，请重新确认"));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> unavailable(Exception error) {
        return ResponseEntity.status(503).body(Collections.singletonMap("message","订单服务暂时不可用，请稍后重新确认"));
    }
}
