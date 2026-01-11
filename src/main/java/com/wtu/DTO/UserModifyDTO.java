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
@Schema(description = "用户修改传输对象")
public class UserModifyDTO {

    @Schema(description = "用户ID")
    private Integer id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "用户类型")
    private String phone;

    @Schema(description = "头像")
    private String avatar;
}
