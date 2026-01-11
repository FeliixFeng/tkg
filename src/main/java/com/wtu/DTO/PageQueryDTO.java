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
@Schema(description = "分页查询传输对象")
public class PageQueryDTO {

    @Schema(description = "页数")
    private Integer page;

    @Schema(description = "单页数据量")
    private Integer pageSize;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "名称")
    private String name;
}
