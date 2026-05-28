package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.PageResult;
import com.campus.entity.Club;
import com.campus.entity.ClubActivity;
import com.campus.entity.ClubActivityRegistration;
import com.campus.entity.ClubActivitySignIn;
import com.campus.entity.ClubMember;
import com.campus.mapper.ClubActivityMapper;
import com.campus.mapper.ClubActivityRegistrationMapper;
import com.campus.mapper.ClubActivitySignInMapper;
import com.campus.mapper.ClubMapper;
import com.campus.mapper.ClubMemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 社团活动服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClubActivityService {

    private final ClubActivityMapper clubActivityMapper;
    private final ClubActivityRegistrationMapper registrationMapper;
    private final ClubActivitySignInMapper signInMapper;
    private final ClubMapper clubMapper;
    private final ClubMemberMapper clubMemberMapper;

    public void createActivity(ClubActivity activity, Long userId) {
        LambdaQueryWrapper<ClubMember> memberQuery = new LambdaQueryWrapper<>();
        memberQuery.eq(ClubMember::getClubId, activity.getClubId())
                   .eq(ClubMember::getUserId, userId)
                   .eq(ClubMember::getRole, "社长")
                   .eq(ClubMember::getStatus, 1);
        boolean isPresident = clubMemberMapper.selectCount(memberQuery) > 0;
        if (!isPresident) {
            throw new RuntimeException("仅社长可以发布社团活动");
        }

        activity.setCurrentParticipants(0);
        activity.setStatus(1);
        Club club = clubMapper.selectById(activity.getClubId());
        if (club != null) {
            activity.setClubName(club.getName());
        }
        clubActivityMapper.insert(activity);
    }

    public void updateActivity(ClubActivity activity) {
        clubActivityMapper.updateById(activity);
    }

    public void deleteActivity(Long id) {
        clubActivityMapper.deleteById(id);
    }

    public ClubActivity getActivityById(Long id) {
        return clubActivityMapper.selectById(id);
    }

    public PageResult<ClubActivity> getActivityList(Integer pageNum, Integer pageSize, Long clubId) {
        Page<ClubActivity> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ClubActivity> wrapper = new LambdaQueryWrapper<>();
        
        if (clubId != null) {
            wrapper.eq(ClubActivity::getClubId, clubId);
        }
        
        wrapper.eq(ClubActivity::getStatus, 1)
               .orderByDesc(ClubActivity::getCreateTime);
        
        Page<ClubActivity> result = clubActivityMapper.selectPage(page, wrapper);
        
        PageResult<ClubActivity> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setRecords(result.getRecords());
        return pageResult;
    }

    public List<ClubActivity> getActivitiesByClubId(Long clubId) {
        LambdaQueryWrapper<ClubActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubActivity::getClubId, clubId)
               .eq(ClubActivity::getStatus, 1)
               .orderByDesc(ClubActivity::getCreateTime);
        return clubActivityMapper.selectList(wrapper);
    }

    @Transactional
    public void registerActivity(Long activityId, Long userId, String userName, String studentNo) {
        int count = 0;
        try {
            count = registrationMapper.countByActivityIdAndUserId(activityId, userId);
        } catch (Exception e) {
            // 表不存在，视为未报名
        }
        if (count > 0) {
            throw new RuntimeException("已报名该活动");
        }

        ClubActivity activity = clubActivityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }

        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new RuntimeException("活动已满");
        }

        ClubActivityRegistration registration = new ClubActivityRegistration();
        registration.setActivityId(activityId);
        registration.setUserId(userId);
        registration.setUserName(userName);
        registration.setStudentNo(studentNo);
        registration.setStatus(1);
        registrationMapper.insert(registration);

        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        clubActivityMapper.updateById(activity);
    }

    @Transactional
    public void cancelRegistration(Long activityId, Long userId) {
        try {
            LambdaQueryWrapper<ClubActivityRegistration> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ClubActivityRegistration::getActivityId, activityId)
                   .eq(ClubActivityRegistration::getUserId, userId);

            ClubActivityRegistration registration = registrationMapper.selectOne(wrapper);
            if (registration != null) {
                registrationMapper.deleteById(registration.getId());

                ClubActivity activity = clubActivityMapper.selectById(activityId);
                if (activity != null && activity.getCurrentParticipants() > 0) {
                    activity.setCurrentParticipants(activity.getCurrentParticipants() - 1);
                    clubActivityMapper.updateById(activity);
                }
            }
        } catch (Exception e) {
            // 表不存在，忽略
        }
    }

    /**
     * 活动签到
     */
    @Transactional
    public void signInActivity(Long activityId, Long userId, String userName, String studentNo) {
        int count = signInMapper.countByActivityIdAndUserId(activityId, userId);
        if (count > 0) {
            throw new RuntimeException("已签到");
        }

        ClubActivitySignIn signIn = new ClubActivitySignIn();
        signIn.setActivityId(activityId);
        signIn.setUserId(userId);
        signIn.setUserName(userName);
        signIn.setStudentNo(studentNo);
        signIn.setSignInTime(LocalDateTime.now());
        signInMapper.insert(signIn);
    }

    /**
     * 获取活动签到记录
     */
    public List<ClubActivitySignIn> getActivitySignIns(Long activityId) {
        LambdaQueryWrapper<ClubActivitySignIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubActivitySignIn::getActivityId, activityId)
               .orderByDesc(ClubActivitySignIn::getSignInTime);
        return signInMapper.selectList(wrapper);
    }

    /**
     * 获取活动报名列表
     */
    public List<ClubActivityRegistration> getActivityRegistrations(Long activityId) {
        LambdaQueryWrapper<ClubActivityRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubActivityRegistration::getActivityId, activityId)
               .eq(ClubActivityRegistration::getStatus, 1)
               .orderByDesc(ClubActivityRegistration::getCreateTime);
        return registrationMapper.selectList(wrapper);
    }
}