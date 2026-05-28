package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.User;
import com.campus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        try {
            userService.register(user);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");
            Map<String, Object> result = userService.login(username, password);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info/{userId}")
    public Result<User> getUserInfo(@PathVariable Long userId) {
        User user = userService.getUserInfo(userId);
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<Void> updateUserInfo(@RequestBody User user) {
        try {
            userService.updateUserInfo(user);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/changePassword")
    public Result<Void> changePassword(@RequestBody Map<String, String> data) {
        try {
            Long userId = Long.parseLong(data.get("userId"));
            String oldPassword = data.get("oldPassword");
            String newPassword = data.get("newPassword");
            userService.changePassword(userId, oldPassword, newPassword);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping("/list")
    public Result<?> getUserList(@RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  @RequestParam(required = false) String keyword) {
        try {
            return Result.success(userService.getUserList(pageNum, pageSize, keyword));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
