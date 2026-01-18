package com.wtu.mapper;

import com.wtu.DTO.EntityCheckNoDTO;
import com.wtu.entity.Entity;
import com.wtu.entity.EntityDTO;
import com.wtu.entity.Node;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;


/**
 * <p>
 *  实体Mapper 接口
 * </p>
 *
 * @since 2024-11-21
 */
@Mapper
public interface EntityMapper {

    // 获取根结点
    List<Node> getRootNode();

    // 获取子节点
    List<Node> getNode(Long id);

    // 查询所有已审核通过的节点（用于优化树形结构构建）
    List<Node> getAllApprovedNodes();

    // ID查询实体所有信息
    Entity findById(Long id);

    // 查询总数
    int count(@Param("name") String name,
              @Param("status") Integer status);

    // 分页查询
    List<Entity> pageQuery(@Param("offset") int offset,
                           @Param("limit") int limit,
                           @Param("name") String name,
                           @Param("status") Integer status);

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

    // 审核实体
    void checkEntityOk(Long id);

    // 根据实体名查询实体
    Entity findByName(String name);

    // 审核未通过
    void checkEntityNo(EntityCheckNoDTO entityCheckNoDTO);
}
