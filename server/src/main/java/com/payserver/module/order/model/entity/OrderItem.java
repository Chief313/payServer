package com.payserver.module.order.model.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单明细实体
 * 对应数据库表 order_item
 */
@Data
public class OrderItem {

    /** 主键ID */
    private Long id;
    /** 订单ID */
    private Long orderId;
    /** 商品ID */
    private Long productId;
    /** SKU ID */
    private Long skuId;
    /** 商品名称快照 */
    private String productName;
    /** 规格名称快照 */
    private String skuName;
    /** 成交单价 */
    private BigDecimal price;
    /** 购买数量 */
    private Integer quantity;
}
