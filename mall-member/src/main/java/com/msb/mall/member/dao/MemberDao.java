package com.msb.mall.member.dao;

import com.msb.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author dpb
 * @email dengpbs@163.com
 * @date 2021-11-24 19:47:00
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
