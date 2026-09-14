package com.msb.common.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@ToString
@Data
public class MemberVO implements Serializable  {
    private static final long serialVersionUID = 1L;


    private Long id;

    private Long levelId;

    private String username;

    private String password;

    private String nickname;

    private String mobile;

    private String email;

    private String header;

    private Integer gender;

    private Date birth;

    private String city;

    private String job;

    private String sign;

    private Integer sourceType;

    private Integer integration;

    private Integer growth;

    private Integer status;

    private Date createTime;

    private String socialUid;
    private String accessToken;
    private long expiresIn;

}
