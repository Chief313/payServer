package com.payserver.module.cart.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 购物车条目实体
 * 对应数据库表 cart_item
 */
@Data
public class CartItem {

    /** 主键ID */
    private Long id;
    /** 用户ID */
    private Long userId;
    /** SKU ID */
    private Long skuId;
    /** 购买数量 */
    private Integer quantity;
    /** 是否选中：1选中 0未选中 */
    private Integer selected;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
