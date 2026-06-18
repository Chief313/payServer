package com.payserver.module.product.model.entity;

import lombok.Data;

/**
 * SKU 详情（含商品信息）
 * 用于下单时获取商品快照
 */
@Data
public class SkuDetail {

    /** SKU ID */
    private Long skuId;
    /** 商品ID */
    private Long productId;
    /** 商品名称 */
    private String productName;
    /** 规格名称 */
    private String skuName;
    /** 单价 */
    private java.math.BigDecimal price;
    /** 可用库存 */
    private Integer stock;
    /** 商品状态：1上架 0下架 */
    private Integer productStatus;
}
