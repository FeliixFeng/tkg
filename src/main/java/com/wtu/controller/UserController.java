package com.wtu.controller;

import com.wtu.DTO.UserDTO;
import com.wtu.DTO.UserLoginDTO;
import com.wtu.DTO.UserModifyDTO;
import com.wtu.DTO.UserRegisterDTO;
import com.wtu.entity.User;
import com.wtu.result.Result;
import com.wtu.service.IUserService;
import com.wtu.utils.JwtUtils;
import com.wtu.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  用户管理
 * </p>
 *
 * @since 2024-11-21
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final JwtUtils jwtUtils;

    // 用户登录
    @PostMapping("/login")
    @Operation(summary = "用户登录",description = "通过用户名, 密码登录")
    public Result<UserLoginVO> login(@Valid @RequestBody UserLoginDTO userLoginDTO){
        log.info("用户登录 {}", userLoginDTO);
        
        // 转换为 UserDTO
        UserDTO userDTO = UserDTO.builder()
                .username(userLoginDTO.getUsername())
                .password(userLoginDTO.getPassword())
                .build();
        
        User user = userService.login(userDTO);

        // 转换为 VO，隐藏密码
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .userType(user.getUserType())
                .avatar(user.getAvatar())
                .token(user.getToken())
                .build();

        return Result.success(userLoginVO);
    }

    // 用户注册
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "通过用户名、密码注册新用户")
    public Result<UserLoginVO> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        // 转换为 UserDTO
        UserDTO userDTO = UserDTO.builder()
                .username(userRegisterDTO.getUsername())
                .password(userRegisterDTO.getPassword())
                .userType(userRegisterDTO.getUserType())
                .phone(userRegisterDTO.getPhone())
                .build();

        User user = userService.register(userDTO);

        // 注册成功后生成 Token
        String token = jwtUtils.generateToken(user.getUsername());
        user.setToken(token);

        // 转换为 VO，隐藏密码
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .userType(user.getUserType())
                .avatar(user.getAvatar())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }

    // 用户ID查询用户名
    @GetMapping("/get_name/{id}")
    @Operation(summary = "用户ID查询用户名", description = "通过ID查询用户名")
    public Result<String> getUserNameById(@PathVariable Integer id){
        String name = userService.findNameById(id);
        return Result.success(name);
    }

    // 用户名查询所有信息
    @GetMapping("/get_user/{username}")
    @Operation(summary = "用户名查询所有信息", description = "返回所有用户信息")
    public Result<User> getUser(@PathVariable String username){
        User user = userService.list(username);
        // 清除密码字段
        user.setPassword(null);
        return Result.success(user);
    }

    // 修改用户信息
    @PutMapping("/update")
    @Operation(summary = "修改用户信息", description = "修改用户信息")
    public Result updateUser(@RequestBody UserModifyDTO userModifyDTO){
        userService.update(userModifyDTO);
        return Result.success();
    }

    // 修改密码
    @PutMapping("/modify_password")
    @Operation(summary = "修改密码", description = "修改密码")
    public Result modifyPassword(@RequestBody UserModifyDTO userModifyDTO){
        userService.modifyPassword(userModifyDTO);
        return Result.success();
    }
}
