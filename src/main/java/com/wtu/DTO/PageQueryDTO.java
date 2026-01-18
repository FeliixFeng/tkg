package com.wtu.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "页数不能为空")
    @Min(value = 1, message = "页数必须大于0")
    private Integer page;

    @Schema(description = "单页数据量")
    @NotNull(message = "单页数据量不能为空")
    @Min(value = 1, message = "单页数据量必须大于0")
    private Integer pageSize;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "名称")
    private String name;
}
