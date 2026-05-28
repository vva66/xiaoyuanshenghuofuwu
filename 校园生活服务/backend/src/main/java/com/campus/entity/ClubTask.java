package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("club_task")
public class ClubTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long clubId;

    private String title;

    private String description;

    private Long assigneeId;

    private String assigneeName;

    private Long createdBy;

    private String createdByName;

    private Integer status;

    private LocalDateTime deadline;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
