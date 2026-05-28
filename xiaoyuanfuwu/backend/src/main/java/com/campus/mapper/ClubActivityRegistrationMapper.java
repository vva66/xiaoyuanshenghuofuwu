package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.ClubActivityRegistration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 社团活动报名Mapper
 */
@Mapper
public interface ClubActivityRegistrationMapper extends BaseMapper<ClubActivityRegistration> {

    @Select("SELECT COUNT(*) FROM club_activity_registration WHERE activity_id = #{activityId} AND user_id = #{userId}")
    int countByActivityIdAndUserId(@Param("activityId") Long activityId, @Param("userId") Long userId);
}