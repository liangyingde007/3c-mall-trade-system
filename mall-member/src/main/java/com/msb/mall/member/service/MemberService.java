package com.msb.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.msb.common.utils.PageUtils;
import com.msb.mall.member.entity.MemberEntity;
import com.msb.mall.member.exception.PhoneExsitExecption;
import com.msb.mall.member.exception.UsernameExsitException;
import com.msb.mall.member.vo.MemberLoginVO;
import com.msb.mall.member.vo.MemberReigerVO;
import com.msb.mall.member.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author dpb
 * @email dengpbs@163.com
 * @date 2021-11-24 19:47:00
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberReigerVO vo) throws PhoneExsitExecption, UsernameExsitException;

    MemberEntity login(MemberLoginVO vo);

    /**
     * 社交登录
     * @param vo
     * @return
     */
    MemberEntity login(SocialUser vo);
}

