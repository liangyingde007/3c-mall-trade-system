package com.msb.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.msb.mall.product.entity.CategoryEntity;
import com.msb.mall.product.service.CategoryService;
import com.msb.common.utils.PageUtils;
import com.msb.common.utils.R;



/**
 * 商品三级分类
 *
 * @author dpb
 * @email dengpbs@163.com
 * @date 2021-11-24 15:09:45
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:category:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryService.queryPage(params);
        // PageUtils page = categoryService.queryPageWithTree(params);
        return R.ok().put("page", page);
    }


    @GetMapping("/listTree")
    public R listTree(@RequestParam Map<String, Object> params){
        List<CategoryEntity> list = categoryService.queryPageWithTree(params);
        return R.ok().put("data", list);
    }




    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		// categoryService.updateById(category);
        categoryService.updateDetail(category);
        return R.ok();
    }

    /**
     * 批量修改
     */
    @RequestMapping("/updateBatch")
    //@RequiresPermissions("product:category:update")
    public R updateBatch(@RequestBody CategoryEntity[] category){
        //categoryService.updateById(category);
        categoryService.updateBatchById(Arrays.asList(category));
        return R.ok();
    }

    /**
     * 批量删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
		// categoryService.removeByIds(Arrays.asList(catIds));
        categoryService.removeCategoryByIds(Arrays.asList(catIds));
        return R.ok();
    }


}
