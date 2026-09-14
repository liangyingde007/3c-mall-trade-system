package com.msb.mall.third;

import com.aliyun.oss.OSSClient;
import com.msb.mall.third.utils.HttpUtils;
import com.msb.mall.third.utils.SmsComponent;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class MallThirdPartyApplicationTests {

    @Autowired
    private OSSClient ossClient;

    @Disabled("Requires local OSS credentials and test file")
    @Test
    public void testUploadFile() throws FileNotFoundException {
        // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        InputStream inputStream = new FileInputStream(System.getProperty("user.home") + "/Downloads/example.jpg");
        // 依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir/exampleobject.txt）。Object完整路径中不能包含Bucket名称。
        ossClient.putObject(System.getenv().getOrDefault("ALIYUN_OSS_BUCKET", "example-bucket"), "example.jpg", inputStream);
        // 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("长传图片成功...");
    }

    @Autowired
    private SmsComponent component;
    @Disabled("Requires real SMS credentials")
    @Test
    public void testSendSMS2(){
        component.sendSmsCode("13800000000","9966");
    }

    @Disabled("Requires real SMS credentials")
    @Test
    public void testSendSMS1(){
        String host = "https://dfsns.market.alicloudapi.com";
        String path = "/data/send_sms";
        String method = "POST";
        String appcode = System.getenv().getOrDefault("ALIYUN_SMS_APP_CODE", "");
        Map<String, String> headers = new HashMap<String, String>();
        // Authorization header format: APPCODE <your-app-code>
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("content", "code:1122");
        bodys.put("phone_number", "13800000000");
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
