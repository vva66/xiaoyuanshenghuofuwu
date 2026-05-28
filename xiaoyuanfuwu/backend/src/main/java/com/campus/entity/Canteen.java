package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 食堂实体
 */
@Data
@TableName("canteen")
public class Canteen implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String address;

    private String openingHours;

    private String contactPhone;

    private String image;

    private String description;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private BigDecimal rating;

    private Integer status;

    private Integer sortOrder;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
