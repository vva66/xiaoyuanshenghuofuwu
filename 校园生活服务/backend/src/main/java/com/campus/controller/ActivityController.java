package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.Activity;
import com.campus.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 活动控制器
 */
@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    /**
     * 创建活动
     */
    @PostMapping("/create")
    public Result<Void> createActivity(@RequestBody Activity activity) {
        try {
            activityService.createActivity(activity);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新活动
     */
    @PutMapping("/update")
    public Result<Void> updateActivity(@RequestBody Activity activity) {
        try {
            activityService.updateActivity(activity);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除活动
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteActivity(@PathVariable Long id) {
        try {
            activityService.deleteActivity(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取活动详情
     */
    @GetMapping("/detail/{id}")
    public Result<Activity> getActivityDetail(@PathVariable Long id) {
        Activity activity = activityService.getActivityById(id);
        return Result.success(activity);
    }

    /**
     * 分页查询活动列表
     */
    @GetMapping("/list")
    public Result<?> getActivityList(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false) String category,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) Integer status) {
        try {
            return Result.success(activityService.getActivityList(pageNum, pageSize, category, keyword, status));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取推荐活动
     */
    @GetMapping("/recommend")
    public Result<?> getRecommendActivities() {
        try {
            return Result.success(activityService.getRecommendActivities());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
