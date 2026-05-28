package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.entity.ClubTask;
import com.campus.mapper.ClubTaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubTaskService {

    private final ClubTaskMapper clubTaskMapper;

    public void createTask(ClubTask task) {
        task.setStatus(0);
        clubTaskMapper.insert(task);
    }

    public List<ClubTask> getClubTasks(Long clubId) {
        LambdaQueryWrapper<ClubTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClubTask::getClubId, clubId)
               .orderByDesc(ClubTask::getCreateTime);
        return clubTaskMapper.selectList(wrapper);
    }

    @Transactional
    public void completeTask(Long taskId, Long userId) {
        ClubTask task = clubTaskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        if (!userId.equals(task.getAssigneeId()) && !userId.equals(task.getCreatedBy())) {
            throw new RuntimeException("无权操作此任务");
        }
        task.setStatus(1);
        clubTaskMapper.updateById(task);
    }

    public void deleteTask(Long taskId) {
        clubTaskMapper.deleteById(taskId);
    }
}
