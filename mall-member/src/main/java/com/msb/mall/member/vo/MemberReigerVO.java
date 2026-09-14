package com.msb.mall.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class MemberReigerVO {

    private String userName; // 账号

    private String password; // 密码

    private String phone;  // 手机号
}
