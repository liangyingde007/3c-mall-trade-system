package com.msb.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.msb.common.utils.PageUtils;
import com.msb.mall.member.dao.MemberLevelDao;
import com.msb.mall.member.entity.MemberLevelEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 会员等级
 *
 * @author dpb
 * @email dengpbs@163.com
 * @date 2021-11-24 19:47:00
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {



    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询默认的会员等级
     *
     * @return
     */
     MemberLevelEntity queryMemberLevelDefault() ;
}

