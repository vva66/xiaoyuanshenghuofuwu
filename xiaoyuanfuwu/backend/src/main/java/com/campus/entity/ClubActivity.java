package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 社团活动实体
 */
@Data
@TableName("club_activity")
public class ClubActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long clubId;

    private String clubName;

    private String title;

    private String description;

    private String location;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer maxParticipants;

    private Integer currentParticipants;

    private String coverImage;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}