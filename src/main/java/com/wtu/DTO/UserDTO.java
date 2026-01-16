package com.wtu.DTO;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Schema(description = "用户传输对象")
public class UserDTO {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "用户类型")
    private Integer userType;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "用户的 JWT Token")
    private String token;

}
