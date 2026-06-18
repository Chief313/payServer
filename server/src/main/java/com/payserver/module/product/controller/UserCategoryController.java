package com.payserver.module.product.controller;

import com.payserver.common.result.Result;
import com.payserver.module.product.model.entity.Category;
import com.payserver.module.product.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端分类控制器
 * 提供分类浏览接口（无需登录）
 */
@RestController
@RequestMapping("/api/v1/user/categories")
public class UserCategoryController {

    private final ProductService productService;

    public UserCategoryController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 查询上架分类列表
     *
     * @return 分类列表
     */
    @GetMapping
    public Result<List<Category>> listCategories() {
        return Result.ok(productService.listCategories().stream()
                .filter(c -> c.getStatus() == null || c.getStatus() == 1)
                .toList());
    }
}
