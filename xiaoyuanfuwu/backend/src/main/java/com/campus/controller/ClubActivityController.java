package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.ClubActivity;
import com.campus.entity.ClubActivityRegistration;
import com.campus.entity.ClubActivitySignIn;
import com.campus.service.ClubActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 社团活动控制器
 */
@RestController
@RequestMapping("/club/activity")
@RequiredArgsConstructor
public class ClubActivityController {

    private final ClubActivityService clubActivityService;

    @PostMapping("/create")
    public Result<Void> createActivity(@RequestBody Map<String, Object> params) {
        try {
            ClubActivity activity = new ClubActivity();
            activity.setClubId(Long.parseLong(params.get("clubId").toString()));
            activity.setTitle((String) params.get("title"));
            activity.setDescription((String) params.get("description"));
            activity.setLocation((String) params.get("location"));
            activity.setStartTime(java.time.LocalDateTime.parse(params.get("startTime").toString().substring(0, 19)));
            activity.setEndTime(java.time.LocalDateTime.parse(params.get("endTime").toString().substring(0, 19)));
            activity.setMaxParticipants(Integer.parseInt(params.getOrDefault("maxParticipants", "50").toString()));
            Long userId = Long.parseLong(params.get("userId").toString());
            clubActivityService.createActivity(activity, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<Void> updateActivity(@RequestBody ClubActivity activity) {
        try {
            clubActivityService.updateActivity(activity);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteActivity(@PathVariable Long id) {
        try {
            clubActivityService.deleteActivity(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result<ClubActivity> getActivityDetail(@PathVariable Long id) {
        ClubActivity activity = clubActivityService.getActivityById(id);
        return Result.success(activity);
    }

    @GetMapping("/list")
    public Result<?> getActivityList(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false) Long clubId) {
        try {
            return Result.success(clubActivityService.getActivityList(pageNum, pageSize, clubId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/club/{clubId}")
    public Result<List<ClubActivity>> getActivitiesByClub(@PathVariable Long clubId) {
        List<ClubActivity> activities = clubActivityService.getActivitiesByClubId(clubId);
        return Result.success(activities);
    }

    @PostMapping("/register")
    public Result<Void> registerActivity(@RequestBody Map<String, Object> params) {
        try {
            Long activityId = Long.parseLong(params.get("activityId").toString());
            Long userId = Long.parseLong(params.get("userId").toString());
            String userName = (String) params.get("userName");
            String studentNo = (String) params.get("studentNo");
            clubActivityService.registerActivity(activityId, userId, userName, studentNo);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/cancel")
    public Result<Void> cancelRegistration(@RequestBody Map<String, Object> params) {
        try {
            Long activityId = Long.parseLong(params.get("activityId").toString());
            Long userId = Long.parseLong(params.get("userId").toString());
            clubActivityService.cancelRegistration(activityId, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/signin")
    public Result<Void> signInActivity(@RequestBody Map<String, Object> params) {
        try {
            Long activityId = Long.parseLong(params.get("activityId").toString());
            Long userId = Long.parseLong(params.get("userId").toString());
            String userName = (String) params.get("userName");
            String studentNo = (String) params.get("studentNo");
            clubActivityService.signInActivity(activityId, userId, userName, studentNo);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/signins/{activityId}")
    public Result<List<ClubActivitySignIn>> getActivitySignIns(@PathVariable Long activityId) {
        try {
            List<ClubActivitySignIn> signIns = clubActivityService.getActivitySignIns(activityId);
            return Result.success(signIns);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/registrations/{activityId}")
    public Result<List<ClubActivityRegistration>> getActivityRegistrations(@PathVariable Long activityId) {
        try {
            List<ClubActivityRegistration> registrations = clubActivityService.getActivityRegistrations(activityId);
            return Result.success(registrations);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}