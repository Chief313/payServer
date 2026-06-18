package com.payserver.module.product.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体，对应 product 表
 */
@Data
public class Product {

    /** 商品ID */
    private Long id;
    /** 分类ID */
    private Long categoryId;
    /** 商品名称 */
    private String name;
    /** 商品描述 */
    private String description;
    /** 封面图 URL */
    private String coverUrl;
    /** 售价 */
    private BigDecimal price;
    /** 状态：1 上架，0 下架 */
    private Integer status;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
