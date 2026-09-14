package com.msb.mall.product.vo;

import lombok.Data;

/**
 * VO:View Object
 * DTO
 * DO
 * PO
 */
@Data
public class AttrVO {
    private Long attrId;
    private String attrName;
    private Integer searchType;
    private String icon;
    private String valueSelect;
    private Integer attrType;
    private Long enable;
    private Long catelogId;
    private Integer showDesc;
    /**
     * 记录规格参数对应的属性组
     */
    private Long attrGroupId;

}
