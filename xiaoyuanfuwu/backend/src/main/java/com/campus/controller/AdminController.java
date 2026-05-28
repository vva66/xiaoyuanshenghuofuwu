package com.campus.controller;

import com.campus.common.PageResult;
import com.campus.common.Result;
import com.campus.entity.Activity;
import com.campus.entity.Canteen;
import com.campus.entity.CanteenDish;
import com.campus.entity.Club;
import com.campus.entity.HelpPost;
import com.campus.entity.User;
import com.campus.service.ActivityService;
import com.campus.service.CanteenService;
import com.campus.service.ClubService;
import com.campus.service.HelpPostService;
import com.campus.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理后台控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ActivityService activityService;
    private final HelpPostService helpPostService;
    private final ClubService clubService;
    private final CanteenService canteenService;

    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userService.getUserCount());
        stats.put("activityCount", activityService.getActivityCount());
        stats.put("postCount", helpPostService.getPostCount());
        stats.put("clubCount", clubService.getClubCount());
        stats.put("canteenCount", canteenService.getCanteenCount());
        stats.put("dishCount", canteenService.getDishCount());
        stats.put("activityCategoryStats", activityService.getActivityCountByCategory());
        stats.put("postCategoryStats", helpPostService.getPostCountByCategory());
        stats.put("collegeStats", helpPostService.getUserCountByCollege());
        return Result.success(stats);
    }

    /**
     * 获取用户列表（管理端）
     */
    @GetMapping("/users")
    public Result<PageResult<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) Integer status) {
        PageResult<User> result = userService.getAdminUserList(pageNum, pageSize, username, studentNo, status);
        return Result.success(result);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/users")
    public Result<Void> updateUser(@RequestBody User user) {
        try {
            userService.updateUserInfo(user);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> data) {
        try {
            Integer status = Integer.parseInt(data.get("status").toString());
            userService.updateUserStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取活动列表（管理端）
     */
    @GetMapping("/activities")
    public Result<PageResult<Activity>> getActivityList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        PageResult<Activity> result = activityService.getActivityList(pageNum, pageSize, category, keyword, status);
        return Result.success(result);
    }

    /**
     * 删除活动
     */
    @DeleteMapping("/activities/{id}")
    public Result<Void> deleteActivity(@PathVariable Long id) {
        try {
            activityService.deleteActivity(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取帖子列表（管理端）
     */
    @GetMapping("/posts")
    public Result<PageResult<HelpPost>> getPostList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        PageResult<HelpPost> result = helpPostService.getAdminPostList(pageNum, pageSize, category, keyword, status);
        return Result.success(result);
    }

    /**
     * 审核通过帖子
     */
    @PutMapping("/posts/{id}/approve")
    public Result<Void> approvePost(@PathVariable Long id) {
        try {
            helpPostService.approvePost(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 拒绝帖子
     */
    @PutMapping("/posts/{id}/reject")
    public Result<Void> rejectPost(@PathVariable Long id) {
        try {
            helpPostService.rejectPost(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取社团列表（管理端）
     */
    @GetMapping("/clubs")
    public Result<PageResult<Club>> getClubList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        PageResult<Club> result = clubService.getAllClubs(pageNum, pageSize, category, status);
        return Result.success(result);
    }

    /**
     * 删除社团
     */
    @DeleteMapping("/clubs/{id}")
    public Result<Void> deleteClub(@PathVariable Long id) {
        try {
            clubService.deleteClub(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取食堂列表（管理端）
     */
    @GetMapping("/canteens")
    public Result<PageResult<Canteen>> getCanteenList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Canteen> result = canteenService.getCanteenList(pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 新增食堂
     */
    @PostMapping("/canteens")
    public Result<Void> addCanteen(@RequestBody Canteen canteen) {
        try {
            canteenService.addCanteen(canteen);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新食堂
     */
    @PutMapping("/canteens")
    public Result<Void> updateCanteen(@RequestBody Canteen canteen) {
        try {
            canteenService.updateCanteen(canteen);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除食堂
     */
    @DeleteMapping("/canteens/{id}")
    public Result<Void> deleteCanteen(@PathVariable Long id) {
        try {
            canteenService.deleteCanteen(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取菜品列表
     */
    @GetMapping("/dishes")
    public Result<?> getDishList(@RequestParam(required = false) Long canteenId) {
        if (canteenId != null) {
            return Result.success(canteenService.getDishesByCanteenId(canteenId));
        }
        return Result.success(canteenService.getAllDishes());
    }

    /**
     * 新增菜品
     */
    @PostMapping("/dishes")
    public Result<Void> addDish(@RequestBody CanteenDish dish) {
        try {
            canteenService.addDish(dish);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新菜品
     */
    @PutMapping("/dishes")
    public Result<Void> updateDish(@RequestBody CanteenDish dish) {
        try {
            canteenService.updateDish(dish);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除菜品
     */
    @DeleteMapping("/dishes/{id}")
    public Result<Void> deleteDish(@PathVariable Long id) {
        try {
            canteenService.deleteDish(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 保存系统设置
     */
    @PostMapping("/settings")
    public Result<Void> saveSettings(@RequestBody Map<String, Object> settings) {
        log.info("保存系统设置: {}", settings);
        return Result.success();
    }
}
