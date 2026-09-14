package com.msb.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.msb.common.constant.ProductConstant;
import com.msb.mall.product.dao.AttrAttrgroupRelationDao;
import com.msb.mall.product.dao.AttrGroupDao;
import com.msb.mall.product.entity.*;
import com.msb.mall.product.service.*;
import com.msb.mall.product.vo.AttrGroupRelationVO;
import com.msb.mall.product.vo.AttrResponseVo;
import com.msb.mall.product.vo.AttrVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.msb.common.utils.PageUtils;
import com.msb.common.utils.Query;

import com.msb.mall.product.dao.AttrDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    CategoryService categoryService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVO vo) {
        // 1.保存规格参数的正常信息
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(vo,attrEntity);
        this.save(attrEntity);
        // 2.保存规格参数和属性组的对应信息
        if(vo.getAttrGroupId() != null && vo.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            // 设置相关的属性
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(vo.getAttrGroupId());
            // 将关联的数据存储到对应的表结构中
            attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
        }

    }

    @Override
    public PageUtils queryBasePage(Map<String, Object> params, Long catelogId, String attrType) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_type","base".equalsIgnoreCase(attrType)?1:0);
        // 1.根据类别编号查询
        if(catelogId != 0 ){
            wrapper.eq("catelog_id",catelogId);
        }
        // 2.根据key 模糊查询
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
           wrapper.and((w)->{
               w.eq("attr_id",key).or().like("attr_name",key);
           });
        }
        // 3.分页查询
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        // 4. 关联的我们需要查询出类别名称和属性组的名称
        List<AttrEntity> records = page.getRecords();
        List<AttrResponseVo> list = records.stream().map((attrEntity) -> {
            AttrResponseVo responseVo = new AttrResponseVo();
            BeanUtils.copyProperties(attrEntity, responseVo);
            // 查询每一条结果对应的 类别名称和属性组的名称
            CategoryEntity categoryEntity = categoryService.getById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                responseVo.setCatelogName(categoryEntity.getName());
            }
            if("base".equalsIgnoreCase(attrType)){
                // 设置属性组的名称
                AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
                entity.setAttrId(attrEntity.getAttrId());
                // 去关联表中找到对应的属性组ID
                //attrAttrgroupRelationService.query(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attrEntity.getAttrId()));
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao
                        .selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (attrAttrgroupRelationEntity != null && attrAttrgroupRelationEntity.getAttrGroupId() != null) {
                    // 获取到属性组的ID，然后根据属性组的ID我们来查询属性组的名称
                    AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrAttrgroupRelationEntity.getAttrGroupId());
                    responseVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

            return responseVo;
        }).collect(Collectors.toList());
        pageUtils.setList(list);
        return pageUtils;
    }

    /**
     * 根据规格参数ID查询对应的详细信息
     * 1.规格参数的具体信息
     * 2.关联的属性组信息
     * 3.关联的类别信息
     * @param attrId
     * @return
     */
    @Override
    public AttrResponseVo getAttrInfo(Long attrId) {
        // 声明返回的对象
        AttrResponseVo responseVo = new AttrResponseVo();
        // 1.根据ID查询规格参数的基本信息
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity,responseVo);
        // 2.查询关联的属性组信息 中间表
        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if(relationEntity != null){
                AttrGroupEntity attrGroupEntity = attrGroupService.getById(relationEntity.getAttrGroupId());
                responseVo.setAttrGroupId(attrGroupEntity.getAttrGroupId());
                if(attrGroupEntity != null){
                    responseVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }

        // 3.查询关联的类别信息
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        responseVo.setCatelogPath(catelogPath);

        CategoryEntity categoryEntity = categoryService.getById(catelogId);
        if(categoryEntity!=null){
            responseVo.setCatelogName(categoryEntity.getName());
        }
        return responseVo;
    }

    @Transactional
    @Override
    public void updateBaseAttr(AttrVO attr) {
        AttrEntity entity = new AttrEntity();
        BeanUtils.copyProperties(attr,entity);
        // 1.更新基本数据
        this.updateById(entity);
        if(entity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            // 2.修改分组关联的关系
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(entity.getAttrId());
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            // 判断是否存在对应的数据
            Integer count = attrAttrgroupRelationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId()));
            if(count > 0){
                // 说明有记录，直接更新
                attrAttrgroupRelationDao.update(relationEntity,new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId()));
            }else{
                // 说明没有记录，直接插入
                attrAttrgroupRelationDao.insert(relationEntity);
            }
        }
    }

    @Transactional
    @Override
    public void removeByIdsDetails(Long[] attrIds) {
        for (Long attrId : attrIds) {
            AttrEntity byId = getById(attrId);
            if(byId != null && byId.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
                // 还需要删除关联的属性中的中间表的信息
                attrAttrgroupRelationDao.delete(new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attrId));
            }
        }
        // 1.删除属性表中的数据
        this.removeByIds(Arrays.asList(attrIds));
    }

    /**
     * 根据属性组编号查询对应的基本信息
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        // 1. 根据属性组编号从 属性组和基本信息的关联表中查询出对应的属性信息
        List<AttrAttrgroupRelationEntity> list = attrAttrgroupRelationDao
                .selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        // 2.根据属性id数组获取对应的详情信息
        List<AttrEntity> attrEntities = list.stream()
                .map((entity) -> this.getById(entity.getAttrId()))
                .filter((entity)-> entity != null)
                .collect(Collectors.toList());
        return attrEntities;
    }

    /**
     * 解除属性组和基本属性的关联关系
     *    也就是删除 属性组和属性关联表中的记录
     * @param vos
     */
    @Override
    public void deleteRelation(AttrGroupRelationVO[] vos) {
        // 将我们接收数据的对象转为了一个 entity 实体对象
        List<AttrAttrgroupRelationEntity> list = Arrays.asList(vos).stream().map((item) -> {
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, entity);
            return entity;
        }).collect(Collectors.toList());
        // 批量的删除关联表中的数据
        attrAttrgroupRelationDao.removeBatchRelation(list);
    }

    /**
     * 根据属性组的ID查询出未被关联的属性信息
     * 1.查询出所有的属性信息
     *   我们找到的属性信息必须是和属性组的类别相同的属性
     * 2.查询出当前属性组关联的属性信息
     * 3.1中排除掉2中的属性，剩下的就是我们要查找的属性信息
     * @param params
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils getNoAttrRelation(Map<String, Object> params, Long attrgroupId) {
        // 1.查询当前属性组所在的类别编号
        AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrgroupId);
        // 获取到对应的分类id
        Long catelogId = attrGroupEntity.getCatelogId();
        // 2.当前分组只能关联自己所属的类别下其他的分组没有关联的属性信息。
        // 先找到这个类别下的所有的分组信息
        List<AttrGroupEntity> group = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        // 获取属性组的编号集合
        List<Long> groupIds = group.stream().map((g) -> g.getAttrGroupId()).collect(Collectors.toList());
        // 然后查询出类别信息下所有的属性组已经分配的属性信息
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", groupIds));
        List<Long> attrIds = relationEntities.stream().map((m) -> m.getAttrId()).collect(Collectors.toList());
        // 根据类别编号查询所有的属性信息并排除掉上面的属性信息即可
        // 这其实就是需要查询出最终返回给调用者的信息了  分页  带条件查询
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id",catelogId)
                // 查询的是基本属性信息，不需要查询销售属性信息
                .eq("attr_type",ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        // 然后添加排除的条件
        if(attrIds != null && attrIds.size() > 0){
            wrapper.notIn("attr_id",attrIds);
        }
        // 还有根据key的查询操作
        String key = (String)params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        // 查询对应的相关信息
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<Long> selectSearchAttrIds(List<Long> attrIds) {
        List<AttrEntity> list = this.list(new QueryWrapper<AttrEntity>().in("attr_id", attrIds).eq("search_type", 1));
        return list.stream().map(item->{
            return item.getAttrId();
        }).collect(Collectors.toList());
    }


}