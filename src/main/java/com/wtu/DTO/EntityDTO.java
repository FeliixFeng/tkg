package com.wtu.entity;


import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "实体传输对象")
public class EntityDTO {

    @Schema(description = "实体ID")
    private Long id;

    @Schema(description = "实体名")
    private String name;

    @Schema(description = "父节点ID")
    private Integer parentId;

    @Schema(description = "实体描述")
    private String description;

    @Schema(description = "实体特点")
    private String feature;

    @Schema(description = "实体简述")
    private String overview;

    @Schema(description = "实体状态")
    private Integer status;

    @Schema(description = "创建者ID")
    private Integer userId;

    @Schema(description = "实体图片")
    private String imageUrl;

}
