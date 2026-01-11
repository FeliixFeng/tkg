package com.wtu.DTO;

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
public class EntityCheckNoDTO {

    @Schema(description = "实体ID")
    private Long id;

    @Schema(description = "未审核通过原因")
    private String rejectReason;
}
