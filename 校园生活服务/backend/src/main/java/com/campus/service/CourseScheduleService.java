package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.entity.CourseSchedule;
import com.campus.mapper.CourseScheduleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课表服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseScheduleService {

    private final CourseScheduleMapper courseScheduleMapper;

    /**
     * 添加课程
     */
    public void addCourse(CourseSchedule course) {
        course.setStatus(1);
        courseScheduleMapper.insert(course);
    }

    /**
     * 更新课程
     */
    public void updateCourse(CourseSchedule course) {
        courseScheduleMapper.updateById(course);
    }

    /**
     * 删除课程
     */
    public void deleteCourse(Long id) {
        courseScheduleMapper.deleteById(id);
    }

    /**
     * 获取用户的课程列表
     */
    public List<CourseSchedule> getUserCourses(Long userId) {
        LambdaQueryWrapper<CourseSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSchedule::getUserId, userId)
               .eq(CourseSchedule::getStatus, 1)
               .orderByAsc(CourseSchedule::getDayOfWeek)
               .orderByAsc(CourseSchedule::getStartSection);
        return courseScheduleMapper.selectList(wrapper);
    }

    /**
     * 获取用户指定星期的课程
     */
    public List<CourseSchedule> getCoursesByDay(Long userId, Integer dayOfWeek) {
        LambdaQueryWrapper<CourseSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSchedule::getUserId, userId)
               .eq(CourseSchedule::getDayOfWeek, dayOfWeek)
               .eq(CourseSchedule::getStatus, 1)
               .orderByAsc(CourseSchedule::getStartSection);
        return courseScheduleMapper.selectList(wrapper);
    }

    /**
     * 批量导入课程
     */
    public void batchImportCourses(Long userId, List<CourseSchedule> courses) {
        for (CourseSchedule course : courses) {
            course.setUserId(userId);
            course.setStatus(1);
            courseScheduleMapper.insert(course);
        }
    }
}
