package com.wtu.service.impl;

import com.wtu.DTO.EntityCheckNoDTO;
import com.wtu.DTO.PageQueryDTO;
import com.wtu.constant.MessageConstant;
import com.wtu.entity.Entity;
import com.wtu.entity.EntityDTO;
import com.wtu.entity.Node;
import com.wtu.exception.BusinessException;
import com.wtu.mapper.EntityMapper;
import com.wtu.mapper.UserMapper;
import com.wtu.result.PageResult;
import com.wtu.service.IEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  实体服务实现类
 * </p>
 *
 * @since 2024-11-21
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EntityServiceImpl  implements IEntityService {

    private final EntityMapper entityMapper;
    private final UserMapper userMapper;

    // 查询实体树形结构 - 优化版本，解决 N+1 查询问题
    public List<Node> getEntityTree() {
        // 一次性查询所有已审核通过的节点
        List<Node> allNodes = entityMapper.getAllApprovedNodes();
        log.info("查询到所有节点数量: {}", allNodes.size());
        
        // 使用 Map 存储所有节点，key 为节点 ID
        Map<Long, Node> nodeMap = allNodes.stream()
                .collect(Collectors.toMap(Node::getId, node -> node));
        
        // 用于存储根节点
        List<Node> rootNodes = new ArrayList<>();
        
        // 遍历所有节点，构建父子关系
        for (Node node : allNodes) {
            Integer parentId = node.getParentId();
            if (parentId == null) {
                // 根节点
                rootNodes.add(node);
            } else {
                // 子节点，添加到父节点的 children 列表
                Node parentNode = nodeMap.get(parentId.longValue());
                if (parentNode != null) {
                    if (parentNode.getChildren() == null) {
                        parentNode.setChildren(new ArrayList<>());
                    }
                    parentNode.getChildren().add(node);
                }
            }
        }
        
        return rootNodes;
    }

    // 根据ID查询实体所有信息
    @Override
    public Entity findById(Long id) {
        Entity entity = entityMapper.findById(id);
        if (entity == null) {
            throw new BusinessException(MessageConstant.ENTITY_NOT_FOUND);
        }
        return entity;
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
    @Transactional(rollbackFor = Exception.class)
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
        Long id = entityMapper.findIdByName(name);
        if (id == null) {
            throw new BusinessException(MessageConstant.ENTITY_NOT_FOUND);
        }
        return id;
    }

    // 添加实体
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addEntity(EntityDTO entityDTO) {
        entityMapper.addEntity(entityDTO);
    }

    // 更新实体
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEntity(EntityDTO entityDTO) {
        entityMapper.updateEntity(entityDTO);
    }

    // 审核通过
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkEntityOk(Long id) {
        entityMapper.checkEntityOk(id);
    }

    // 审核未通过
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkEntityNo(EntityCheckNoDTO entityCheckNoDTO) {
        entityMapper.checkEntityNo(entityCheckNoDTO);
    }

    // 根据实体名查询实体
    @Override
    public Entity findByName(String name) {
        Entity entity = entityMapper.findByName(name);
        if (entity == null) {
            throw new BusinessException(MessageConstant.ENTITY_NOT_FOUND);
        }
        return entity;
    }
}
