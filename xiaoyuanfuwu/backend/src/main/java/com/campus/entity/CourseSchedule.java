package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 课表实体
 */
@Data
@TableName("course_schedule")
public class CourseSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String courseName;

    private String teacher;

    private String classroom;

    private Integer dayOfWeek;

    private Integer startSection;

    private Integer endSection;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer weekStart;

    private Integer weekEnd;

    private String semester;

    private String academicYear;

    private String color;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
