package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("sys_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String studentNo;

    private String username;

    private String password;

    private String realName;

    private Integer gender;

    private String phone;

    private String email;

    private String avatar;

    private String college;

    private String major;

    private String className;

    private Integer enrollmentYear;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
