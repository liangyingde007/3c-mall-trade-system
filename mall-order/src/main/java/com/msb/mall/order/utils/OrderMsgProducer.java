package com.msb.mall.order.utils;

import com.msb.common.constant.OrderConstant;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class OrderMsgProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public  void sendOrderMessage(String orderSN){
        rocketMQTemplate.syncSend(OrderConstant.ROCKETMQ_ORDER_TOPIC, MessageBuilder.withPayload(orderSN).build(),5000,4);
    }
}
