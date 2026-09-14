package com.msb.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.msb.common.utils.PageUtils;
import com.msb.mall.product.entity.AttrAttrgroupRelationEntity;
import com.msb.mall.product.vo.AttrGroupRelationVO;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author dpb
 * @email dengpbs@163.com
 * @date 2021-11-24 14:46:05
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);


    void saveBatch(List<AttrGroupRelationVO> vos);
}

