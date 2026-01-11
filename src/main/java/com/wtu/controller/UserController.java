package com.wtu.controller;

import com.wtu.DTO.UserDTO;
import com.wtu.DTO.UserModifyDTO;
import com.wtu.entity.User;
import com.wtu.result.Result;
import com.wtu.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserController {

    @Autowired
    private IUserService userService;

    // 用户登录
    @PostMapping("/login")
    @Operation(summary = "用户登录",description = "通过用户名, 密码登录")
    public Result<User> login(@RequestBody UserDTO userDTO){
        log.info("用户登录 {}", userDTO);
        User user = userService.login(userDTO);

        if(user != null){
            return Result.success(user);

        }else{
            return Result.error("用户名或密码错误!");
        }
    }

    // 用户注册
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "通过用户名、密码注册新用户")
    public Result<User> register(@RequestBody UserDTO userDTO) {
        User user = userService.register(userDTO);
        if(user != null){
            return Result.success(user);  // 返回注册成功的用户信息
        }else{
            return Result.error("用户名已存在!");
        }
    }

    // 用户ID查询用户名
    @GetMapping("/get_name/{id}")
    @Operation(summary = "用户ID查询用户名", description = "通过ID查询用户名")
    public Result getUserNameById(@PathVariable Integer id){
        String name = userService.findNameById(id);
        if(name == null){
            return Result.error("用户不存在!");
        }else{
            return Result.success(name);
        }
    }

    // 用户名查询所有信息
    @GetMapping("/get_user/{username}")
    @Operation(summary = "用户名查询所有信息", description = "返回所有用户信息")
    public Result getUser(@PathVariable String username){
        return Result.success(userService.list(username));
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
