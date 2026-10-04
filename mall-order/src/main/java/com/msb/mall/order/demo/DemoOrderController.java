package com.msb.mall.order.demo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.msb.common.constant.AuthConstant;
import com.msb.common.vo.MemberVO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotBlank;
import java.util.*;

@ConditionalOnProperty(name="mall.demo.enabled",havingValue="true")
@RestController
@RequestMapping("/demo/orders")
public class DemoOrderController {
    private final DemoOrderService service;
    public DemoOrderController(DemoOrderService service) { this.service=service; }

    @PostMapping(value="/confirmation",consumes="application/json")
    public DemoOrderService.Quote confirm(HttpServletRequest request) { return service.confirm(member(request)); }
    @PostMapping(consumes="application/json")
    public Map<String,Object> create(@Valid @RequestBody Submission submission,HttpServletRequest request) {
        return service.create(member(request),submission.token);
    }
    @GetMapping public List<Map<String,Object>> list(HttpServletRequest request) { return service.list(member(request)); }
    @GetMapping("/{sn}") public Map<String,Object> detail(@PathVariable String sn,HttpServletRequest request) {
        return service.detail(member(request),sn);
    }
    @PostMapping(value="/{sn}/cancel",consumes="application/json")
    public Map<String,Object> cancel(@PathVariable String sn,HttpServletRequest request) { return service.cancel(member(request),sn); }

    private MemberVO member(HttpServletRequest request) {
        HttpSession session=request.getSession(false);
        Object value=session==null?null:session.getAttribute(AuthConstant.AUTH_SESSION_REDIS);
        if (!(value instanceof MemberVO) || !"demo-visitor".equals(((MemberVO)value).getUsername())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"演示会话已失效，请刷新页面");
        }
        return (MemberVO)value;
    }
    public static class Submission {
        @NotBlank @Pattern(regexp="[a-f0-9]{32}") public String token;
    }
}
