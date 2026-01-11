package com.wtu.mapper;

import com.wtu.DTO.UserDTO;
import com.wtu.DTO.UserModifyDTO;
import com.wtu.entity.User;
import org.apache.ibatis.annotations.*;

/**
 * <p>
 *  用户Mapper 接口
 * </p>
 *
 * @since 2024-11-21
 */

@Mapper
public interface UserMapper {

    // 根据用户名密码登录
    User login(String username, String password);

    // 根据用户名查询用户
    User selectByUsername(String username);

    // 注册用户
    // 自动生成主键并回填到对象的 id 属性
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int register(User newUser);

    // 根据用户ID获取用户名
    String getUsernameById(Integer userId);

    // 根据用户ID获取用户名
    String findNameById(Integer id);

    // 根据用户名查询用户
    User list(String username);

    // 更新用户信息
    void update(UserModifyDTO userModifyDTO);

    // 修改密码
    void modifyPassword(UserModifyDTO userModifyDTO);
}
