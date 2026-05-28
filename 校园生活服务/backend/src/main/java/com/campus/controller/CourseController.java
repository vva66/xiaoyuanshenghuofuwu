package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.CourseSchedule;
import com.campus.service.CourseScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 课表控制器
 */
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseScheduleService courseScheduleService;

    /**
     * 添加课程
     */
    @PostMapping("/add")
    public Result<Void> addCourse(@RequestBody CourseSchedule course) {
        try {
            courseScheduleService.addCourse(course);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新课程
     */
    @PutMapping("/update")
    public Result<Void> updateCourse(@RequestBody CourseSchedule course) {
        try {
            courseScheduleService.updateCourse(course);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        try {
            courseScheduleService.deleteCourse(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户课程列表
     */
    @GetMapping("/list/{userId}")
    public Result<List<CourseSchedule>> getUserCourses(@PathVariable Long userId) {
        List<CourseSchedule> courses = courseScheduleService.getUserCourses(userId);
        return Result.success(courses);
    }

    /**
     * 获取指定星期的课程
     */
    @GetMapping("/day/{userId}/{dayOfWeek}")
    public Result<List<CourseSchedule>> getCoursesByDay(@PathVariable Long userId, 
                                                         @PathVariable Integer dayOfWeek) {
        List<CourseSchedule> courses = courseScheduleService.getCoursesByDay(userId, dayOfWeek);
        return Result.success(courses);
    }

    /**
     * 批量导入课程
     */
    @PostMapping("/batchImport")
    public Result<Void> batchImport(@RequestBody Map<String, Object> data) {
        try {
            Long userId = Long.parseLong(data.get("userId").toString());
            @SuppressWarnings("unchecked")
            List<CourseSchedule> courses = (List<CourseSchedule>) data.get("courses");
            courseScheduleService.batchImportCourses(userId, courses);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
