package com.msb.mall.product.service.impl;

import com.msb.mall.product.dao.CategoryBrandRelationDao;
import com.msb.mall.product.entity.CategoryBrandRelationEntity;
import com.msb.mall.product.service.CategoryBrandRelationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.msb.common.utils.PageUtils;
import com.msb.common.utils.Query;

import com.msb.mall.product.dao.BrandDao;
import com.msb.mall.product.entity.BrandEntity;
import com.msb.mall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    CategoryBrandRelationDao relationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 1. 获取key
        String key = (String)params.get("key");
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(key)){
            // 添加对应的查询条件
            wrapper.eq("brand_id",key).or().like("name",key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 更新品牌信息
     * @param brand
     */
    @Transactional
    @Override
    public void updateDetail(BrandEntity brand) {
        // 1.更新原始数据
        this.updateById(brand);
        if(!StringUtils.isEmpty(brand.getName())){
            // 同步更新级联关系中的数据
            categoryBrandRelationService.updateBrandName(brand.getBrandId(),brand.getName());
            // TODO 同步更新其他的品牌名称的冗余数据

        }
    }

}