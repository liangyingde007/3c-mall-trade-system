package com.msb.mall.third.controller;

import com.msb.common.utils.R;
import com.msb.mall.third.utils.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SMSController {

    @Autowired
    private SmsComponent smsComponent;

    /**
     * 调用短信服务商提供的短信API发送短信
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/sms/sendcode")
    public R sendSmsCode(@RequestParam("phone") String phone,@RequestParam("code") String code){
        smsComponent.sendSmsCode(phone,code);
        return R.ok();
    }
}
