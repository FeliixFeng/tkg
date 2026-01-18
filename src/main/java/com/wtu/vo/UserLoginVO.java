package com.wtu.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录/注册返回 VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginVO {

    // 用户ID
    private Integer id;

    // 用户名
    private String username;

    // 手机号
    private String phone;

    // 用户类型
    private Integer userType;

    // 头像
    private String avatar;

    // JWT Token
    private String token;
}
