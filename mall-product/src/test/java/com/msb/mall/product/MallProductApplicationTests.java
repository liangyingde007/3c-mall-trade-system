package com.msb.mall.product;


import com.msb.mall.product.service.AttrGroupService;
import com.msb.mall.product.service.CategoryService;
import com.msb.mall.product.service.SkuSaleAttrValueService;
import com.msb.mall.product.vo.SkuItemSaleAttrVo;
import com.msb.mall.product.vo.SpuItemGroupAttrVo;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;


@SpringBootTest
public class MallProductApplicationTests {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;


    @Test
    public void testRedissonClient(){
        System.out.println("redissonClient:"+redissonClient);
    }

    @Test
    public void testStringRedisTemplate(){
        // 获取操作String类型的Options对象
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        // 插入数据
        ops.set("name","bobo"+ UUID.randomUUID());
        // 获取存储的信息
        System.out.println("刚刚保存的值："+ops.get("name"));
    }

    @Test
    public void test1(){
        Long[] catelogPath = categoryService.findCatelogPath(762l);
        for (Long aLong : catelogPath) {
            System.out.println(aLong);
        }
    }

    @Test
    public void test2(){
        List<SpuItemGroupAttrVo> attrgroupWithSpuId = attrGroupService.getAttrgroupWithSpuId(6l, 225l);
        for (SpuItemGroupAttrVo spuItemGroupAttrVo : attrgroupWithSpuId) {
            System.out.println(spuItemGroupAttrVo);
        }
    }

    @Test
    public void test3(){
        List<SkuItemSaleAttrVo> skuSaleAttrValueBySpuId = skuSaleAttrValueService.getSkuSaleAttrValueBySpuId(6l);
        for (SkuItemSaleAttrVo skuItemSaleAttrVo : skuSaleAttrValueBySpuId) {
            System.out.println(skuItemSaleAttrVo);
        }
    }

}
