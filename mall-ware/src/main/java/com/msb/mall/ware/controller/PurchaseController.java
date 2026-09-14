package com.msb.mall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.msb.mall.ware.vo.MergeVO;
import com.msb.mall.ware.vo.PurchaseDoneVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.msb.mall.ware.entity.PurchaseEntity;
import com.msb.mall.ware.service.PurchaseService;
import com.msb.common.utils.PageUtils;
import com.msb.common.utils.R;



/**
 * 采购信息
 *
 * @author dpb
 * @email dengpbs@163.com
 * @date 2021-11-24 19:46:02
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;


    /**
     * 完成采购
     * {
     *     id:1,// 采购单
     *     items:[
     *      {itemId:1,status:4,reason:""}
     *      ,{itemId:2,status:4,reason:""}
     *     ]//采购项
     * }
     */
    @PostMapping("/done")
    public R done(@RequestBody PurchaseDoneVO vo){
        purchaseService.done(vo);
        return R.ok();
    }

    // /ware/purchase/receive

    /**
     * 领取采购单
     * [2,3,4]
     * @return
     */
    @PostMapping("/receive")
    public R receive(@RequestBody List<Long> ids){
        purchaseService.received(ids);
        return R.ok();
    }

    // /ware/purchase/merge
    @RequestMapping("/merge")
    //@RequiresPermissions("ware:purchase:list")
    public R merge(@RequestBody MergeVO mergeVO){
        Integer flag = purchaseService.merge(mergeVO);
        if(flag == -1){
            return R.error("合并失败...该采购单不能被合并!");
        }
        return R.ok();
    }
    /**
     * 列表
     */
    @RequestMapping("/unreceive/list")
    //@RequiresPermissions("ware:purchase:list")
    public R listUnreceive(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnreceive(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
