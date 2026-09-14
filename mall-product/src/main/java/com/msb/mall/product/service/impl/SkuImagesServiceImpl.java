package com.msb.mall.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.msb.common.utils.PageUtils;
import com.msb.common.utils.Query;

import com.msb.mall.product.dao.SkuImagesDao;
import com.msb.mall.product.entity.SkuImagesEntity;
import com.msb.mall.product.service.SkuImagesService;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {
   @Autowired
    SkuImagesDao dao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuImagesEntity> page = this.page(
                new Query<SkuImagesEntity>().getPage(params),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 根据 SKUID 获取对应的图片信息
     * @param skuId
     * @return
     */
    @Override
    public List<SkuImagesEntity> getImagesBySkuId(Long skuId) {
        List<SkuImagesEntity> skuImagesEntities = dao.selectList(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
        return skuImagesEntities;
    }

}