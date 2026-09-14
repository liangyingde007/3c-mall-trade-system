package com.msb.common.exception;

/**
 * 自定义异常：锁定库存失败的情况下产生的异常信
 */
public class NoStockExecption extends RuntimeException{

    private Long skuId;

    public NoStockExecption(Long skuId){
        super("当前商品["+skuId+"]没有库存了");
        this.skuId = skuId;

    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
