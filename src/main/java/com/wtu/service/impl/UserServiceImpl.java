package com.wtu.service.impl;

import com.wtu.DTO.UserDTO;
import com.wtu.DTO.UserModifyDTO;
import com.wtu.constant.MessageConstant;
import com.wtu.entity.User;
import com.wtu.exception.BusinessException;
import com.wtu.mapper.UserMapper;
import com.wtu.service.IUserService;
import com.wtu.utils.JwtUtils;
import com.wtu.utils.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  用户服务实现类
 * </p>
 *
 * @since 2024-11-21
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    // 用户登录
    @Override
    public User login(UserDTO userDTO) {
        // 获取用户名和密码
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();

        // 根据用户名查询用户
        User user = userMapper.selectByUsername(username);
        
        // 如果用户不存在或密码不匹配，抛出异常
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(MessageConstant.USER_LOGIN_FAILED);
        }

        // 用户存在且密码正确, 生成JWT Token
        String token = jwtUtils.generateToken(username);
        user.setToken(token);

        return user;
    }

    // 用户注册
    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(UserDTO userDTO) {
        // 获取用户名、密码、用户类型、电话
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        Integer userType = userDTO.getUserType();
        String phone = userDTO.getPhone();

        // 查询用户是否存在
        User user = userMapper.selectByUsername(username);

        // 如果用户存在，抛出异常
        if(user != null){
            throw new BusinessException(MessageConstant.USER_ALREADY_EXISTS);
        }

        // 加密密码
        String encodedPassword = passwordEncoder.encode(password);

        // 创建新用户
        User newUser = User.builder()
                .username(username)
                .password(encodedPassword)
                .userType(userType)
                .phone(phone)
                .avatar("default.png")
                .build();
        // 调用 Mapper 注册用户并获取ID
        int id = userMapper.register(newUser);
        newUser.setId(id);

        // 注册成功后生成 JWT Token
        String token = jwtUtils.generateToken(username);
        newUser.setToken(token);

        return newUser;
    }

    // 根据ID查询用户名
    @Override
    public String findNameById(Integer id) {
        String name = userMapper.findNameById(id);
        if (name == null) {
            throw new BusinessException(MessageConstant.USER_NOT_FOUND);
        }
        return name;
    }

    // 根据用户名查询用户
    @Override
    public User list(String username) {
        User user = userMapper.list(username);
        if (user == null) {
            throw new BusinessException(MessageConstant.USER_NOT_FOUND);
        }
        return user;
    }

    // 更新用户信息
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserModifyDTO userModifyDTO) {
        userMapper.update(userModifyDTO);
    }

    // 修改密码
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyPassword(UserModifyDTO userModifyDTO) {
        // 加密新密码
        String encodedPassword = passwordEncoder.encode(userModifyDTO.getPassword());
        userModifyDTO.setPassword(encodedPassword);
        userMapper.modifyPassword(userModifyDTO);
    }
}
