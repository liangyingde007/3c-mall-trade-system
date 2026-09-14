package com.msb.mall.product.web;

import com.msb.mall.product.service.SkuInfoService;
import com.msb.mall.product.vo.SpuItemVO;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

/**
 * 商品详情的控制器
 */
@Controller
public class ItemController {

    @Autowired
    SkuInfoService skuInfoService;

    /**
     * 根据前端传递的SkuId我们需要查询出对有的商品信息
     * @param skuId
     * @return
     */
    @Trace
    @GetMapping("/{skuId}.html")
    public String item(@PathVariable Long skuId, Model model) throws ExecutionException, InterruptedException {
        SpuItemVO itemVO = skuInfoService.item(skuId);
        model.addAttribute("item",itemVO);
        return "item";
    }
}
