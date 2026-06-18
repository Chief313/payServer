package com.payserver.module.product.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类实体，对应 category 表
 */
@Data
public class Category {

    /** 分类ID */
    private Long id;
    /** 分类名称 */
    private String name;
    /** 父分类ID，0 表示顶级 */
    private Long parentId;
    /** 排序值 */
    private Integer sort;
    /** 状态：1 启用，0 禁用 */
    private Integer status;
    /** 创建时间 */
    private LocalDateTime createTime;
}
