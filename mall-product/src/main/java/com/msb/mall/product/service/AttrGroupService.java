package com.msb.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.msb.common.utils.PageUtils;
import com.msb.mall.product.entity.AttrGroupEntity;
import com.msb.mall.product.vo.AttrGroupWithAttrsVo;
import com.msb.mall.product.vo.SpuItemGroupAttrVo;
import com.msb.mall.product.vo.SpuItemVO;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author dpb
 * @email dengpbs@163.com
 * @date 2021-11-24 14:46:05
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);


    List<AttrGroupWithAttrsVo> getAttrgroupWithAttrsByCatelogId(Long catelogId);

    List<SpuItemGroupAttrVo> getAttrgroupWithSpuId(Long spuId
            , Long catalogId);
}

