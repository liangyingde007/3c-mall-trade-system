package com.msb.mall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.msb.common.dto.SkuHasStockDto;
import com.msb.common.exception.BizCodeEnume;
import com.msb.common.exception.NoStockExecption;
import com.msb.mall.ware.vo.LockStockResult;
import com.msb.mall.ware.vo.WareSkuLockVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.msb.mall.ware.entity.WareSkuEntity;
import com.msb.mall.ware.service.WareSkuService;
import com.msb.common.utils.PageUtils;
import com.msb.common.utils.R;



/**
 * 商品库存
 *
 * @author dpb
 * @email dengpbs@163.com
 * @date 2021-11-24 19:46:02
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;


    @PostMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVO vo){
        try {
            Boolean flag = wareSkuService.orderLockStock(vo);
        }catch (NoStockExecption e){
            // 表示锁定库存失败
            return R.error(BizCodeEnume.NO_STOCK_EXCEPTION.getCode(),BizCodeEnume.NO_STOCK_EXCEPTION.getMsg());
        }

        return R.ok();
    }

    /**
     * 查询对应的skuId是否有库存
     * @param skuIds
     * @return
     */
    @PostMapping("/hasStock")
    public List<SkuHasStockDto> getSkusHasStock(@RequestBody List<Long> skuIds){
        List<SkuHasStockDto> list = wareSkuService.getSkusHasStock(skuIds);
        return list;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
