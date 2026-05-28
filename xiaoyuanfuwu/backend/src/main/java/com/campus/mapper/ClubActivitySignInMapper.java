package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.ClubActivitySignIn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ClubActivitySignInMapper extends BaseMapper<ClubActivitySignIn> {

    @Select("SELECT COUNT(*) FROM club_activity_sign_in WHERE activity_id = #{activityId} AND user_id = #{userId}")
    int countByActivityIdAndUserId(@Param("activityId") Long activityId, @Param("userId") Long userId);
}
