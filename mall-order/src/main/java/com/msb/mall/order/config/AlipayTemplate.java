package com.msb.mall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.msb.mall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付宝的配置文件
 */
//@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    // 商户appid 沙箱账号: tklalf8880@sandbox.com
    public static String APPID = "2021000121601310";
    // 私钥 pkcs8格式的
    public static String RSA_PRIVATE_KEY = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCM/F/ekJJQdWwQfqA+IKHj2PLPFZt63jbuPqI29b7PzG6Amk1oljMVq9GUyONGsiHVfRR24LjuWRu4VgfPRLZbKAHKeFh6c/X3oW/w4KTjLsy42KBKfU41EEm921oXBAJchXnw9HFNybyek2DmcgRc/ARveCDz2XnbeU7cdzaUysj0lCS99yl7abeVLcZSZgxbYZ/vBB4Pov3CoSm8VH2nsEhNCORc2rX2Cuj5xC1VxwsP90cd0g2iTSHkp27nNazkORcz37WXifhSM2e/J6sMbsCZZZYszstr3wBja7iXKUKIChV0T4f7yF913WLYW0xuBEGKw3ZXPEPF0kaFuYGBAgMBAAECggEBAIw2qrnUmk8+mJzG8mMXXSoXJ1RgruLBgWvsCrQSLxTGwddQnk+3oVKaMLOCHta+bfu+i822rPUO1gy8MsQmGB4MacuNaKFQFn2SppuvLw1qC9yCRNgQSulnK4+QSca+DAMsFBxZXx331oj3FdXZ6xD26wpZNJyP2ys1OAL25OeQRGjafA34IO1WwYlOxKQg7ZCS+S6BjG9qltdl+glRhdR9wshP6ubJElxRscXC4UjsVm1WQU0AW6MxQ5eQPdN1naKv6ctLCaCUcxMklDXlEmXHbdADSM9YXHhIu9dtF8UPkV+OgmlMgO4BEUyRSPL+x5fhSTUOkvwC9rsIYgSQdgECgYEA/McygN+nWU+u0wjr4AXZDyFqhpMJOgu4jn1Yj6WRazJtEwMczcuVUl2XdKk+IW6iMSLkKlForNNzX9/MpjC1O6hK83z87T/Kr2DXRFduVvSd497od1i0AldaRgBLqqsaqQcLaHBitr6Og36UKKqdwASxGZAyMY2a8cBst+bFHnECgYEAjshniTSSQjG3lQsC2i61sHMYdD+aej8KF0g/7oeNPggvE90bN0JI9XEEQLbokqPC/7xGL5Vs0vz5W2yN9+bKh78cCWrnsktLy8i9rnW1mpeNqyu+Z0zSbhSdqSDUaPEwcGRgxjthdB5+4zHCaiKGIlRQ5buVaIkJClMjKLeAPBECgYEAs18C1nJehUDG9Neq0XA048i5l7801+zDTNFji6NYenHw9oHJ6briPe1N8Sm7fevMygNRVzonhiQSBeGAjhmYMTot3XQ+4nfW8vZMKyHDmY7Bj5Z47V+TdOnZlDzXdMcwBuuJy9WcJsM3y9WBew3/HOf3aAVrqMAsSApc2261R/ECgYEAhMCVav8jAUKCYqPDOiJ1sSHB2cuNEB3ufukg7+C+FK+9PaW0+TK67ODwSE8He9egpO9i/jja3mxwV21dreC5625nXeY0mBPj3Au/OCae+6XvBPf95sOJXunyEF7CvvOIyua5B3YMTM9RwGDyVoXU2rQ0JvS6nF/UtPHJXcEHDFECgYAP6LPk4FE0k5agMME+Kgif+dDzWqJhuFJha7L+6Yk/Z8hP8nPbwvuQ26JZE3NP+Kp/9Pa+QGpFrEkIc0QMv/XIreTvW4gmMD84+gQIyjQ0KKUmTdjWZSKtoPUQ2G1gGPbJ5SNzQhSDwxzekj5Ked9RvG7wJDtm4VAFBveC5I4j/A==";
    // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://order.msb.com/payed/notify";
    // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
    public static String return_url = "http://order.msb.com/orderPay/returnUrl";
    // 请求网关地址
    public static String URL = "https://openapi.alipaydev.com/gateway.do";
    // 编码
    public static String CHARSET = "UTF-8";
    // 返回格式
    public static String FORMAT = "json";
    // 支付宝公钥
    public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuNB+I2NfHghJTp7L6sZo9MI195nCAnLOMBqLyFniEV0V1omrt10bpePnvJ66KLyoXoVa787lIqNR13SGZ0vRI7dScREYXeXij6/kqZ7EVQuY0h8Uxc0Qhu9qr3O70xsbt9juu3kLZrEZHbYi4HsYAOVYiNrtTnKOpelVY+XSehqkVWju+feJoxrHUxRmJTHoKlTHLWuGDGINqPw++XFLFQ31VaXnAd/J+/RBq0rVUN8Gxixazl2ehjizxxA2pZSyxxtu8OCaXmMIcw2AP/uGtssnaKfjd83Ros3x9GaBwqgsJ2NDqhgDKhqf4d5qUmiQag/joSQmHnontJep2TR4/QIDAQAB";
    // 日志记录目录
    public static String log_path = "/log";
    // RSA2
    public static String SIGNTYPE = "RSA2";

    public String pay(PayVo payVo){
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        //调用RSA签名方式
        AlipayClient client = new DefaultAlipayClient(URL,
                APPID,
                RSA_PRIVATE_KEY,
                FORMAT,
                CHARSET,
                ALIPAY_PUBLIC_KEY,
                SIGNTYPE);
        AlipayTradeWapPayRequest alipay_request=new AlipayTradeWapPayRequest();

        // 封装请求支付信息
        AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
        model.setOutTradeNo(payVo.getOut_trader_no());
        model.setSubject(payVo.getSubject());
        model.setTotalAmount(payVo.getTotal_amount());
        model.setBody(payVo.getBody());
        model.setTimeoutExpress("5000");
        model.setProductCode("11111");
        alipay_request.setBizModel(model);
        // 设置异步通知地址
        alipay_request.setNotifyUrl(notify_url);
        // 设置同步地址
        alipay_request.setReturnUrl(return_url);

        // form表单生产
        String form = "";
        try {
            // 调用SDK生成表单
            form = client.pageExecute(alipay_request).getBody();
            return form;
        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  null;
    }
}
