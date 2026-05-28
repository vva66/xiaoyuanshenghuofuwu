package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 求助帖子实体
 */
@Data
@TableName("help_post")
public class HelpPost implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private String category;

    private String images;

    private Long publisherId;

    private String publisherName;

    private String contactInfo;

    private Integer viewCount;

    private Integer replyCount;

    private Integer isTop;

    private Integer isEssence;

    private Integer status;

    private LocalDateTime expireTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
