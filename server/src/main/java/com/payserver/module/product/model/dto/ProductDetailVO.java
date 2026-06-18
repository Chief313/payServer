package com.payserver.module.product.model.dto;

import com.payserver.module.product.model.entity.Product;
import com.payserver.module.product.model.entity.Sku;
import lombok.Data;

import java.util.List;

/**
 * 商品详情响应数据（含 SKU 列表）
 */
@Data
public class ProductDetailVO {

    /** 商品信息 */
    private Product product;
    /** SKU 列表 */
    private List<Sku> skus;
}
