package com.payserver.module.product.controller;

import com.payserver.common.result.PageResult;
import com.payserver.common.result.Result;
import com.payserver.module.product.model.dto.ProductDetailVO;
import com.payserver.module.product.model.entity.Product;
import com.payserver.module.product.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端商品控制器
 * 提供商品浏览接口（无需登录）
 */
@RestController
@RequestMapping("/api/v1/user/products")
public class UserProductController {

    private final ProductService productService;

    public UserProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 分页查询上架商品
     *
     * @param categoryId 分类ID，可选
     * @param page       页码，默认 1
     * @param size       每页条数，默认 10
     * @return 分页商品列表
     */
    @GetMapping
    public Result<PageResult<Product>> listProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(productService.listProducts(categoryId, page, size));
    }

    /**
     * 获取商品详情
     *
     * @param id 商品ID
     * @return 商品详情（含 SKU）
     */
    @GetMapping("/{id}")
    public Result<ProductDetailVO> getProductDetail(@PathVariable Long id) {
        return Result.ok(productService.getProductDetail(id));
    }
}
