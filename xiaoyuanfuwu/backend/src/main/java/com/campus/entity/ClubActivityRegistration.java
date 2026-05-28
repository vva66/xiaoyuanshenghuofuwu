package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 社团活动报名实体
 */
@Data
@TableName("club_activity_registration")
public class ClubActivityRegistration implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long activityId;

    private Long userId;

    private String userName;

    private String studentNo;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}