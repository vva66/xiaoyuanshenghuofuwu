package com.campus.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("freshman_guide")
public class FreshmanGuide {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String category;

    private String content;

    private String images;

    private String videoUrl;

    private Integer sortOrder;

    private Integer isImportant;

    private Integer status;

    private LocalDateTime publishTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
