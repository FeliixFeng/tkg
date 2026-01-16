package com.wtu.service.impl;

import com.wtu.DTO.UserDTO;
import com.wtu.DTO.UserModifyDTO;
import com.wtu.entity.User;
import com.wtu.mapper.UserMapper;
import com.wtu.service.IUserService;

import com.wtu.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  用户服务实现类
 * </p>
 *
 * @since 2024-11-21
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    // 用户登录
    @Override
    public User login(UserDTO userDTO) {
        // 获取用户名和密码
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();

        // 调用 Mapper 查询用户
        User user = userMapper.login(username, password);
        // 如果用户不存在，返回 null
        if (user == null) {
            return null;
        }

        // 用户存在, 生成JWT Token
        String token = jwtUtils.generateToken(username);

        // 设置 Token 和用户信息到UserDTO
        user.setToken(token);

        return user;
    }

    // 用户注册
    @Override
    public User register(UserDTO userDTO) {
        // 获取用户名、密码、用户类型、电话
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        Integer userType = userDTO.getUserType();
        String phone = userDTO.getPhone();
        // 查询用户是否存在
        User user = userMapper.selectByUsername(username);

        // 如果用户存在，返回 null
        if(user != null){
            return null;
        }
        else{
            // 创建新用户
            User newUser = User.builder()
                    .username(username)
                    .password(password)
                    .userType(userType)
                    .phone(phone)
                    .avatar("default.png")
                    .build();
            // 调用 Mapper 注册用户并获取ID
            int id = userMapper.register(newUser);
            newUser.setId(id);
            return newUser;
        }
    }

    // 根据ID查询用户名
    @Override
    public String findNameById(Integer id) {
        return userMapper.findNameById(id);
    }

    // 根据用户名查询用户
    @Override
    public User list(String username) {
        return userMapper.list(username);
    }

    // 更新用户信息
    @Override
    public void update(UserModifyDTO userModifyDTO) {
        userMapper.update(userModifyDTO);
    }

    // 修改密码
    @Override
    public void modifyPassword(UserModifyDTO userModifyDTO) {
        userMapper.modifyPassword(userModifyDTO);
    }


}
