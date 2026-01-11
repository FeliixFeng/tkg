package com.wtu.service;

import com.wtu.DTO.UserDTO;
import com.wtu.DTO.UserModifyDTO;
import com.wtu.entity.User;

/**
 * <p>
 *  用户服务类
 * </p>
 *
 * @since 2024-11-21
 */
public interface IUserService {

    // 用户登录
    User login(UserDTO userDTO);

    // 用户注册
    User register(UserDTO userDTO);

    // 根据ID查询用户名
    String findNameById(Integer id);

    // 根据用户名查询用户
    User list(String username);

    // 更新用户信息
    void update(UserModifyDTO userModifyDTO);

    // 修改密码
    void modifyPassword(UserModifyDTO userModifyDTO);

}
