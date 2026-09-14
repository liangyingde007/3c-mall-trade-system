package com.msb.mall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.msb.mall.product.vo.AttrResponseVo;
import com.msb.mall.product.vo.AttrVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.msb.mall.product.entity.AttrEntity;
import com.msb.mall.product.service.AttrService;
import com.msb.common.utils.PageUtils;
import com.msb.common.utils.R;



/**
 * 商品属性
 *
 * @author dpb
 * @email dengpbs@163.com
 * @date 2021-11-24 15:09:45
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    // attr/base/list/0?t=1640759871949&page=1&limit=10&key=aaa
    // attr/sale/list/
    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseList(@RequestParam Map<String, Object> params
            ,@PathVariable("catelogId") Long catelogId
            ,@PathVariable("attrType") String attrType
    ){
        PageUtils page =attrService.queryBasePage(params,catelogId,attrType);
        return R.ok().put("page",page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     * /product/attr/info/${this.dataForm.attrId}
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		//AttrEntity attr = attrService.getById(attrId);
        AttrResponseVo responseVo = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", responseVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVO vo){
		//attrService.save(attr);
        attrService.saveAttr(vo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVO attr){
		// attrService.updateById(attr);
        attrService.updateBaseAttr(attr);
        return R.ok();
    }

    /**
     * 删除
     *    如果是删除基本属性那么还需要将关联的属性组的信息也要删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
        //
		// attrService.removeByIds(Arrays.asList(attrIds));
        attrService.removeByIdsDetails(attrIds);

        return R.ok();
    }

}
