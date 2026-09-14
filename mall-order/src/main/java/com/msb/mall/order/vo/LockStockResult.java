package com.msb.mall.order.vo;

import lombok.Data;

@Data
public class LockStockResult {

    private Long skuId;
    private Integer count;
    private Boolean locked;
}
