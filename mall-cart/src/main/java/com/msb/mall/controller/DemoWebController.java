package com.msb.mall.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@ConditionalOnProperty(name = "mall.demo.enabled", havingValue = "true")
public class DemoWebController {
    @GetMapping({"/", "/demo"})
    public String home() { return "forward:/demo/index.html"; }
}
