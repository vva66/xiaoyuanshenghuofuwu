package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.PageResult;
import com.campus.entity.Activity;
import com.campus.mapper.ActivityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 活动服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityMapper activityMapper;

    /**
     * 创建活动
     */
    public void createActivity(Activity activity) {
        activity.setCurrentParticipants(0);
        activity.setStatus(0); // 报名中
        activityMapper.insert(activity);
    }

    /**
     * 更新活动
     */
    public void updateActivity(Activity activity) {
        activityMapper.updateById(activity);
    }

    /**
     * 删除活动
     */
    public void deleteActivity(Long id) {
        activityMapper.deleteById(id);
    }

    /**
     * 获取活动详情
     */
    public Activity getActivityById(Long id) {
        return activityMapper.selectById(id);
    }

    /**
     * 分页查询活动列表
     */
    public PageResult<Activity> getActivityList(Integer pageNum, Integer pageSize, 
                                                 String category, String keyword, Integer status) {
        Page<Activity> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        
        // 分类筛选
        if (StringUtils.hasText(category)) {
            wrapper.eq(Activity::getCategory, category);
        }
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Activity::getTitle, keyword);
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(Activity::getStatus, status);
        }
        
        wrapper.orderByDesc(Activity::getCreateTime);
        Page<Activity> result = activityMapper.selectPage(page, wrapper);
        
        PageResult<Activity> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setRecords(result.getRecords());
        return pageResult;
    }

    /**
     * 查询推荐活动
     */
    public List<Activity> getRecommendActivities() {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getIsRecommend, 1)
               .eq(Activity::getStatus, 0) // 报名中
               .orderByDesc(Activity::getCreateTime)
               .last("LIMIT 10");
        return activityMapper.selectList(wrapper);
    }

    /**
     * 查询正在进行的活动
     */
    public List<Activity> getOngoingActivities() {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(Activity::getStartTime, now)
               .ge(Activity::getEndTime, now)
               .eq(Activity::getStatus, 1) // 进行中
               .orderByAsc(Activity::getStartTime);
        return activityMapper.selectList(wrapper);
    }

    /**
     * 更新活动状态
     */
    public void updateActivityStatus(Long id, Integer status) {
        Activity activity = new Activity();
        activity.setId(id);
        activity.setStatus(status);
        activityMapper.updateById(activity);
    }

    /**
     * 获取活动总数
     */
    public long getActivityCount() {
        return activityMapper.selectCount(null);
    }

    /**
     * 按分类统计活动数量
     */
    public Map<String, Long> getActivityCountByCategory() {
        Map<String, Long> result = new HashMap<>();
        List<Activity> all = activityMapper.selectList(null);
        for (Activity a : all) {
            String cat = a.getCategory() != null ? a.getCategory() : "其他";
            result.put(cat, result.getOrDefault(cat, 0L) + 1);
        }
        return result;
    }
}
