package com.wtu.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Entity  {

    // 实体ID
    private Long id;

    // 实体名
    private String name;

    // 父节点ID
    private Integer parentId;

    // 实体描述
    private String description;

    // 实体状态
    private Integer status;

    // 图像URL
    private String imageUrl;

    // 创建者ID
    private Integer userId;

    // 创建时间
    private LocalDateTime createdAt;

    // 更新时间
    private LocalDateTime updatedAt;

    // 实体简述
    private String overview;

    // 实体特点
    private String feature;

    // 未审核通过原因
    private String rejectReason;

}
