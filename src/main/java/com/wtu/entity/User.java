package com.wtu.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

/**
 * 用户实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User{

    // 用户ID
    private Integer id;

    // 用户名
    private String username;

    // 密码
    private String password;

    // 手机号
    private String phone;

    // 用户类型
    private Integer userType;

    // 头像
    private String avatar;

    // 用户的 JWT Token
    private String token;
}
