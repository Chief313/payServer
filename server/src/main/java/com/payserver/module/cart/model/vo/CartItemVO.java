package com.payserver.module.cart.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车列表展示对象
 * 包含商品与 SKU 快照信息
 */
@Data
public class CartItemVO {

    /** 购物车条目ID */
    private Long id;
    /** SKU ID */
    private Long skuId;
    /** 商品ID */
    private Long productId;
    /** 商品名称 */
    private String productName;
    /** 规格名称 */
    private String skuName;
    /** 封面图URL */
    private String coverUrl;
    /** 单价 */
    private BigDecimal price;
    /** 购买数量 */
    private Integer quantity;
    /** 可用库存 */
    private Integer stock;
    /** 是否选中 */
    private Integer selected;
    /** 小计金额 */
    private BigDecimal subtotal;
}
