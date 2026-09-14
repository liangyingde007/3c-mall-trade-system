package com.msb.mall.third.utils;

import lombok.Data;
import org.apache.http.HttpResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信组件
 */
@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
@Data
@Component
public class SmsComponent {

    private String host;
    private String path;
    private String method = "POST";
    private String appCode;

    /**
     * 发送短信验证码
     * @param phone 发送的手机号
     * @param code 发送的短信验证码
     */
    public void sendSmsCode(String phone,String code){
        Map<String, String> headers = new HashMap<String, String>();
        // Authorization header format: APPCODE <your-app-code>
        headers.put("Authorization", "APPCODE " + appCode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("content", "code:"+code);
        bodys.put("phone_number", phone);
        bodys.put("template_id", "TPL_0000");


        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
