package com.wtu.service.impl;

import com.wtu.DTO.EntityCheckNoDTO;
import com.wtu.DTO.PageQueryDTO;
import com.wtu.entity.Entity;
import com.wtu.entity.EntityDTO;
import com.wtu.entity.Node;
import com.wtu.mapper.EntityMapper;
import com.wtu.mapper.UserMapper;
import com.wtu.result.PageResult;
import com.wtu.service.IEntityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 *  实体服务实现类
 * </p>
 *
 * @since 2024-11-21
 */
@Service
@Slf4j
public class EntityServiceImpl  implements IEntityService {

    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private UserMapper userMapper;

    // 查询实体树形结构
    public List<Node> getEntityTree() {
        // 获取根结点
        List<Node> rootNode = entityMapper.getRootNode();
        log.info("根结点, {}", rootNode);
        // 为根结点构建树形结构
        for (Node node : rootNode)
            buildTree(node);
        return rootNode;
    }

    // 根据ID查询实体所有信息
    @Override
    public Entity findById(Long id) {
        return entityMapper.findById(id);
    }

    // 分页查询已审核
    @Override
    public PageResult pageQuery(PageQueryDTO pageQueryDTO) {
        // 计算分页查询的偏移量和限制数
        int offset = (pageQueryDTO.getPage() - 1) * pageQueryDTO.getPageSize();
        int limit = pageQueryDTO.getPageSize();

        // 查询总数
        int total = entityMapper.count(
                pageQueryDTO.getName(),
                pageQueryDTO.getStatus());

        // 分页查询
        List<Entity> entityList = entityMapper.pageQuery(offset, limit,
                pageQueryDTO.getName(),
                pageQueryDTO.getStatus());

        return new PageResult(total, entityList);
    }


    // 根据名称删除实体
    @Override
    public void deleteByName(String name) {
        entityMapper.deleteByName(name);
    }

    // 根据用户ID查询实体
    @Override
    public List<Entity> findByUserId(Long id) {
        return entityMapper.findByUserId(id);
    }

    // 根据实体名查询实体ID
    @Override
    public Long findIdByName(String name) {
        return entityMapper.findIdByName(name);
    }

    // 添加实体
    @Override
    public void addEntity(EntityDTO entityDTO) {
        entityMapper.addEntity(entityDTO);
    }

    // 更新实体
    @Override
    public void updateEntity(EntityDTO entityDTO) {
        entityMapper.updateEntity(entityDTO);
    }

    // 审核通过
    @Override
    public void checkEntityOk(Long id) {
        entityMapper.checkEntityOk(id);
    }

    // 审核未通过
    @Override
    public void checkEntityNo(EntityCheckNoDTO entityCheckNoDTO) {
        entityMapper.checkEntityNo(entityCheckNoDTO);
    }

    // 根据实体名查询实体
    @Override
    public Entity findByName(String name) {
        return entityMapper.findByName(name);
    }


    // 构建树形结构
    private void buildTree(Node node){
        // 获取当前节点的子节点
        List<Node> childrenNodes = entityMapper.getNode(node.getId());
        log.info("当前节点的子节点, {}", childrenNodes);
        // 如果当前节点有子节点，递归地为每个子节点设置树形结构
        if (childrenNodes != null && !childrenNodes.isEmpty()) {
            node.setChildren(new ArrayList<>()); // 初始化children列表
            for (Node child : childrenNodes) {
                // 为子节点递归构建树形结构
                buildTree(child);
                // 将子节点添加到当前节点的children中
                node.getChildren().add(child);
            }
        }
    }

}
