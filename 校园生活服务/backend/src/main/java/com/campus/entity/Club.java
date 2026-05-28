package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 社团实体
 */
@Data
@TableName("club")
public class Club implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String logo;

    private String category;

    private String description;

    private java.time.LocalDate establishedDate;

    private Long presidentId;

    private String presidentName;

    private Integer memberCount;

    private String contactInfo;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
