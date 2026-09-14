package com.msb.mall.order.controller;

import java.util.Arrays;
import java.util.Map;

import com.msb.mall.order.fegin.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import com.msb.mall.order.entity.OrderEntity;
import com.msb.mall.order.service.OrderService;
import com.msb.common.utils.PageUtils;
import com.msb.common.utils.R;



/**
 * 订单
 *
 * @author dpb
 * @email dengpbs@163.com
 * @date 2021-11-24 19:48:00
 */
@RefreshScope
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    ProductService productService;

    @GetMapping("/products")
    public R queryProduct(){
        // OpenFegin 远程调用服务
        return R.ok().put("products",productService.queryAllBrand());
    }
    @Value("${user.userName}")
    private String userName;

    @Value("${user.age}")
    private Integer age;

    @GetMapping("/users")
    public R queryUser(){
        return R.ok().put("userName",userName).put("age",age);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("order:order:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("order:order:info")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("order:order:save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("order:order:update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("order:order:delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }
}
