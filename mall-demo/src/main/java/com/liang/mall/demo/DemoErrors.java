package com.liang.mall.demo;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
@RestControllerAdvice
public class DemoErrors {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String,String>> conflict(ResponseStatusException e) { return ResponseEntity.status(e.getStatus()).body(Collections.singletonMap("message",e.getReason())); }
    @ExceptionHandler({MethodArgumentNotValidException.class,HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String,String>> invalid(Exception e) { return ResponseEntity.badRequest().body(Collections.singletonMap("message","请检查提交信息，商品数量应为 1 到 20 之间的整数")); }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,String>> invalidArgument(IllegalArgumentException e) { return ResponseEntity.badRequest().body(Collections.singletonMap("message",e.getMessage())); }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> unavailable(Exception e) {
        org.slf4j.LoggerFactory.getLogger(DemoErrors.class).error("Demo operation failed ({})",e.getClass().getSimpleName());
        return ResponseEntity.status(503).body(Collections.singletonMap("message","演示服务暂时不可用，请稍后重试"));
    }
}
