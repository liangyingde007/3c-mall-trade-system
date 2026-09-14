package com.msb.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.msb.mall.product.entity.SkuInfoEntity;
import com.msb.mall.product.service.SkuInfoService;
import com.msb.common.utils.PageUtils;
import com.msb.common.utils.R;



/**
 * sku信息
 *
 * @author dpb
 * @email dengpbs@163.com
 * @date 2021-11-24 15:09:45
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:skuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        //PageUtils page = skuInfoService.queryPage(params);
        PageUtils page = skuInfoService.queryPageByCondition(params);
        return R.ok().put("page", page);
    }

    @GetMapping("/saleAttrs/{skuId}")
    public List<String> getSkuSaleAttrs(@PathVariable("skuId") Long skuId){
        List<String> list = skuInfoService.getSkuSaleAttrs(skuId);
        return list;
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    //@RequiresPermissions("product:skuinfo:info")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        return R.ok().put("skuInfo", skuInfo).put("skuInfoJSON", JSON.toJSONString(skuInfo));
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:skuinfo:save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:skuinfo:update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:skuinfo:delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

}
