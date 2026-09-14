package com.msb.mall.member;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//@SpringBootTest
class MallMemberApplicationTests {

    @Test
    void contextLoads() {
        /*String s = Md5Crypt.md5Crypt("123456".getBytes());
        System.out.println("s = " + s);*/
        // d4541250b586296fcce5dea4463ae17f
        // d4541250b586296fcce5dea4463ae17f
        String s = DigestUtils.md2Hex("123456");
        System.out.println(s);
        // 加盐处理 $1$yTOjAK3L$l/bxeu5ntKmRxAWnPDT0f1
        //         $1$dT.BkcTo$36AblSK/wOMNou5u0ouVZ.
        String s1 = Md5Crypt.md5Crypt("123456".getBytes());
        System.out.println(s1);
        // $1$66666666$lfLA4px5dxc7jWjFIJPJh/
        // $1$66666666$lfLA4px5dxc7jWjFIJPJh/
        String s2 = Md5Crypt.md5Crypt("123456".getBytes(),"$1$abc2@134#");
        System.out.println(s2);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode1 = encoder.encode("123456");
        String encode2 = encoder.encode("123456");
        String encode3 = encoder.encode("123456");
        System.out.println("encode1 = " + encode1);
        System.out.println("encode2 = " + encode2);
        System.out.println("encode3 = " + encode3);

        System.out.println("encoder.matches(\"123456\",encode1) = " + encoder.matches("123456", encode1));
        System.out.println("encoder.matches(\"123456\",encode2) = " + encoder.matches("123456", encode2));
        System.out.println("encoder.matches(\"123456\",encode3) = " + encoder.matches("123456", encode3));

    }

}
