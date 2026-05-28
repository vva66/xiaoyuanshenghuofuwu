package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.PageResult;
import com.campus.config.JwtUtil;
import com.campus.entity.User;
import com.campus.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    public void register(User user) {
        // 检查学号是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStudentNo, user.getStudentNo());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("学号已存在");
        }

        // 检查用户名是否已存在
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 使用明文密码，统一设置为123456
        user.setPassword("123456");
        user.setStatus(1);
        userMapper.insert(user);
    }

    /**
     * 用户登录
     */
    public Map<String, Object> login(String username, String password) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证明文密码
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 判断是否为管理员（根据角色判断）
        boolean isAdmin = userMapper.isAdmin(user.getId());

        // 返回用户信息和Token
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("studentNo", user.getStudentNo());
        result.put("realName", user.getRealName());
        result.put("avatar", user.getAvatar());
        result.put("isAdmin", isAdmin);

        return result;
    }

    /**
     * 获取用户信息
     */
    public User getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return user;
    }

    /**
     * 更新用户信息
     */
    public void updateUserInfo(User user) {
        // 不允许直接修改密码
        user.setPassword(null);
        userMapper.updateById(user);
    }

    /**
     * 修改密码
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证明文旧密码
        if (!oldPassword.equals(user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        // 更新为明文新密码
        user.setPassword(newPassword);
        userMapper.updateById(user);
    }

    /**
     * 分页查询用户列表
     */
    public PageResult<User> getUserList(Integer pageNum, Integer pageSize, String keyword) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword)
                   .or()
                   .like(User::getStudentNo, keyword)
                   .or()
                   .like(User::getRealName, keyword);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userMapper.selectPage(page, wrapper);
        
        // 隐藏密码
        result.getRecords().forEach(user -> user.setPassword(null));
        
        PageResult<User> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setRecords(result.getRecords());
        return pageResult;
    }

    /**
     * 管理端分页查询用户列表（支持用户名、学号、状态筛选）
     */
    public PageResult<User> getAdminUserList(Integer pageNum, Integer pageSize,
                                              String username, String studentNo, Integer status) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }

        if (StringUtils.hasText(studentNo)) {
            wrapper.like(User::getStudentNo, studentNo);
        }

        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }

        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userMapper.selectPage(page, wrapper);

        result.getRecords().forEach(user -> user.setPassword(null));

        PageResult<User> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setRecords(result.getRecords());
        return pageResult;
    }

    /**
     * 获取用户总数
     */
    public long getUserCount() {
        return userMapper.selectCount(null);
    }

    /**
     * 更新用户状态
     */
    public void updateUserStatus(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userMapper.updateById(user);
    }
}
