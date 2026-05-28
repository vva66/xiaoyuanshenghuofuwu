package com.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品实体
 */
@Data
@TableName("canteen_dish")
public class CanteenDish implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long canteenId;

    private String windowName;

    private String dishName;

    private String image;

    private BigDecimal price;

    private String description;

    private BigDecimal rating;

    private Integer reviewCount;

    private Integer salesCount;

    private Integer isRecommend;

    private Integer status;

    private Integer sortOrder;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
