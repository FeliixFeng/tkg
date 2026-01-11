package com.wtu.service;

import com.wtu.DTO.EntityCheckNoDTO;
import com.wtu.DTO.PageQueryDTO;
import com.wtu.entity.Entity;
import com.wtu.entity.EntityDTO;
import com.wtu.entity.Node;
import com.wtu.result.PageResult;
import java.util.List;

/**
 * <p>
 *  实体服务类
 * </p>
 *
 * @since 2024-11-21
 */
public interface IEntityService {

    // 查询树形结构
    public List<Node> getEntityTree();

    // 根据ID查询实体所有信息
    Entity findById(Long id);

    // 分页查询
    PageResult pageQuery(PageQueryDTO pageQueryDTO);

    // 根据名称删除实体
    void deleteByName(String name);

    // 根据用户ID查询实体
    List<Entity> findByUserId(Long id);

    // 根据实体名查询实体ID
    Long findIdByName(String name);

    // 添加实体
    void addEntity(EntityDTO entityDTO);

    // 更新实体
    void updateEntity(EntityDTO entityDTO);

    // 实体审核通过
    void checkEntityOk(Long id);

    // 实体审核未通过
    void checkEntityNo(EntityCheckNoDTO entityCheckNoDTO);

    // 根据实体名查询实体
    Entity findByName(String name);

}
