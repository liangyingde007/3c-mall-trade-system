package com.msb.mall.feign;

import com.msb.common.utils.R;
import com.msb.mall.vo.LoginVo;
import com.msb.mall.vo.SocialUser;
import com.msb.mall.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 会员服务
 */
@FeignClient("mall-memeber")
public interface MemberFeginService {
    @PostMapping("/member/member/register")
    public R register(@RequestBody UserRegisterVo vo);


    @RequestMapping("/member/member/login")
    public R login(@RequestBody LoginVo vo);

    @RequestMapping("/member/member/oauth2/login")
    public R socialLogin(@RequestBody SocialUser vo);
}
