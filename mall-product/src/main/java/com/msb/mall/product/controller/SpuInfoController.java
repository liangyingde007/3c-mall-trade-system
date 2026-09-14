package com.msb.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.msb.mall.product.vo.OrderItemSpuInfoVO;
import com.msb.mall.product.vo.SpuInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.msb.mall.product.entity.SpuInfoEntity;
import com.msb.mall.product.service.SpuInfoService;
import com.msb.common.utils.PageUtils;
import com.msb.common.utils.R;



/**
 * spu信息
 *
 * @author dpb
 * @email dengpbs@163.com
 * @date 2021-11-24 15:09:45
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * app/product/spuinfo/6/up
     * 商品的上架功能
     * 传递过来一个spuID
     * 我们就需要根据SPUID查询出需要存储在ElasticSearch中的数据
     * 然后把数据存储到ELasticSearch中，并修改该SPU的状态为上架
     */
    @PostMapping("/{spuId}/up")
    public R spuUp(@PathVariable("spuId") Long spuId){
        spuInfoService.up(spuId);
        return R.ok();
    }
     // /product/spuinfo/getOrderItemSpuInfoBySpuId/{spuIds}
    @RequestMapping("/getOrderItemSpuInfoBySpuId/{spuIds}")
    public List<OrderItemSpuInfoVO> getOrderItemSpuInfoBySpuId(@PathVariable("spuIds") Long[] spuIds){
        return spuInfoService.getOrderItemSpuInfoBySpuId(spuIds);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:spuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
       // PageUtils page = spuInfoService.queryPage(params);
        PageUtils page = spuInfoService.queryPageByCondition(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:spuinfo:info")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:spuinfo:save")
    public R save(@RequestBody SpuInfoVO spuInfoVo){
    //		spuInfoService.save(spuInfo);
        spuInfoService.saveSpuInfo(spuInfoVo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:spuinfo:update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:spuinfo:delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
