package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 社团成员实体
 */
@Data
@TableName("club_member")
public class ClubMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long clubId;

    private Long userId;

    private String userName;

    private String studentNo;

    private String role;

    private LocalDate joinDate;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}