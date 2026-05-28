package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.CourseSchedule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课表Mapper
 */
@Mapper
public interface CourseScheduleMapper extends BaseMapper<CourseSchedule> {
}
