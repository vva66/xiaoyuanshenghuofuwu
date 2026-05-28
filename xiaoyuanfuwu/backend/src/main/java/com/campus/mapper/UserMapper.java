package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 判断用户是否为管理员
     * @param userId 用户ID
     * @return true-是管理员，false-不是管理员
     */
    @Select("SELECT COUNT(1) > 0 FROM sys_user_role ur JOIN sys_role r ON ur.role_id = r.id WHERE ur.user_id = #{userId} AND r.role_code = 'ADMIN'")
    boolean isAdmin(Long userId);
}
