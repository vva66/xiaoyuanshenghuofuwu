package com.campus.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.Club;
import com.campus.entity.ClubMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 社团成员Mapper
 */
@Mapper
public interface ClubMemberMapper extends BaseMapper<ClubMember> {

    @Select("SELECT c.* FROM club c " +
            "JOIN club_member cm ON c.id = cm.club_id " +
            "WHERE cm.user_id = #{userId} AND cm.status = 1 AND c.status = 1")
    List<Club> selectClubsByUserId(@Param("userId") Long userId);

    @Select("SELECT c.* FROM club c " +
            "JOIN club_member cm ON c.id = cm.club_id " +
            "WHERE cm.user_id = #{userId} AND cm.role = '社长' AND cm.status = 1 AND c.status = 1")
    List<Club> selectManagedClubsByUserId(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM club_member WHERE club_id = #{clubId} AND user_id = #{userId} AND status = 1")
    int countByClubIdAndUserId(@Param("clubId") Long clubId, @Param("userId") Long userId);
}