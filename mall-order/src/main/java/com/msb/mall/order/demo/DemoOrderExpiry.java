package com.msb.mall.order.demo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name="mall.demo.enabled",havingValue="true")
@Component
public class DemoOrderExpiry {
    private final DemoOrderService service;
    public DemoOrderExpiry(DemoOrderService service) { this.service=service; }
    @Scheduled(fixedDelayString="${mall.demo.expiry-scan-ms:60000}")
    public void expire() { service.expirePending(); }
}
