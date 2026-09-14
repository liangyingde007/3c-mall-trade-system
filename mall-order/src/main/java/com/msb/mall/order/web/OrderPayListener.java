package com.msb.mall.order.web;

import com.msb.mall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Payment asynchronous callback entry.
 *
 * Portfolio note: this class demonstrates callback idempotency through an
 * order-state CAS update. In a production payment integration, signature
 * verification and amount/appId/seller validation must be performed before
 * changing order state.
 */
@RestController
public class OrderPayListener {

    @Autowired
    private OrderService orderService;

    @PostMapping("/payed/notify")
    public String handleAlipayed(@RequestParam("out_trade_no") String orderSn) {
        // TODO production: verify Alipay signature and payment amount first.
        orderService.handleOrderComplete(orderSn);
        // Payment platforms require a deterministic success response to stop retries.
        return "success";
    }
}
