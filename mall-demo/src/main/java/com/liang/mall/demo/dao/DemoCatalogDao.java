package com.liang.mall.demo.dao;
import org.apache.ibatis.annotations.*;
import java.util.*;
public interface DemoCatalogDao {
    @Select("SELECT t1.attr_group_name AS groupName,t3.attr_name AS attrName,t4.attr_value AS attrValue FROM pms_attr_group t1 LEFT JOIN pms_attr_attrgroup_relation t2 ON t1.attr_group_id=t2.attr_group_id LEFT JOIN pms_attr t3 ON t2.attr_id=t3.attr_id LEFT JOIN pms_product_attr_value t4 ON t4.attr_id=t2.attr_id WHERE t1.catelog_id=#{catalogId} AND t4.spu_id=#{spuId} ORDER BY t1.attr_group_id,t2.attr_sort,t3.attr_id")
    List<Map<String,Object>> attributes(@Param("spuId") Long spuId,@Param("catalogId") Long catalogId);
}
