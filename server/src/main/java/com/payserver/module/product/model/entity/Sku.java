package com.payserver.module.product.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品 SKU 实体
 * 对应数据库表 sku
 */
@Data
public class Sku {

    /** 主键ID */
    private Long id;
    /** 商品ID */
    private Long productId;
    /** 规格名称 */
    private String skuName;
    /** 售价 */
    private BigDecimal price;
    /** 可用库存 */
    private Integer stock;
    /** 预占库存 */
    private Integer lockedStock;
    /** 创建时间 */
    private LocalDateTime createTime;
}
