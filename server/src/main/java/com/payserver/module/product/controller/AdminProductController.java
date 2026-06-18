package com.payserver.module.product.controller;

import com.payserver.common.result.PageResult;
import com.payserver.common.result.Result;
import com.payserver.module.product.model.entity.Category;
import com.payserver.module.product.model.entity.Product;
import com.payserver.module.product.model.entity.Sku;
import com.payserver.module.product.service.ProductService;
import com.payserver.security.RequireRole;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端商品控制器
 * 提供分类与商品的 CRUD 接口
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 查询全部分类
     *
     * @return 分类列表
     */
    @GetMapping("/categories")
    @RequireRole("ADMIN")
    public Result<List<Category>> listCategories() {
        return Result.ok(productService.listCategories());
    }

    /**
     * 新增分类
     *
     * @param category 分类信息
     * @return 新增后的分类
     */
    @PostMapping("/categories")
    @RequireRole("ADMIN")
    public Result<Category> createCategory(@RequestBody Category category) {
        return Result.ok(productService.createCategory(category));
    }

    /**
     * 更新分类
     *
     * @param id       分类ID
     * @param category 分类信息
     * @return 更新后的分类
     */
    @PutMapping("/categories/{id}")
    @RequireRole("ADMIN")
    public Result<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        return Result.ok(productService.updateCategory(category));
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 操作结果
     */
    @DeleteMapping("/categories/{id}")
    @RequireRole("ADMIN")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        productService.deleteCategory(id);
        return Result.ok();
    }

    /**
     * 分页查询全部商品
     *
     * @param categoryId 分类ID，可选
     * @param page       页码
     * @param size       每页条数
     * @return 分页商品列表
     */
    @GetMapping("/products")
    @RequireRole("ADMIN")
    public Result<PageResult<Product>> listProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(productService.adminListProducts(categoryId, page, size));
    }

    /**
     * 新增商品
     *
     * @param product 商品信息
     * @return 新增后的商品
     */
    @PostMapping("/products")
    @RequireRole("ADMIN")
    public Result<Product> createProduct(@RequestBody Product product) {
        return Result.ok(productService.createProduct(product));
    }

    /**
     * 更新商品
     *
     * @param id      商品ID
     * @param product 商品信息
     * @return 更新后的商品
     */
    @PutMapping("/products/{id}")
    @RequireRole("ADMIN")
    public Result<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        return Result.ok(productService.updateProduct(product));
    }

    /**
     * 删除商品
     *
     * @param id 商品ID
     * @return 操作结果
     */
    @DeleteMapping("/products/{id}")
    @RequireRole("ADMIN")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.ok();
    }

    /**
     * 新增 SKU
     *
     * @param productId 商品ID
     * @param sku       SKU 信息
     * @return 新增后的 SKU
     */
    @PostMapping("/products/{productId}/skus")
    @RequireRole("ADMIN")
    public Result<Sku> createSku(@PathVariable Long productId, @RequestBody Sku sku) {
        sku.setProductId(productId);
        return Result.ok(productService.createSku(sku));
    }

    /**
     * 更新 SKU
     *
     * @param id  SKU ID
     * @param sku SKU 信息
     * @return 更新后的 SKU
     */
    @PutMapping("/skus/{id}")
    @RequireRole("ADMIN")
    public Result<Sku> updateSku(@PathVariable Long id, @RequestBody Sku sku) {
        sku.setId(id);
        return Result.ok(productService.updateSku(sku));
    }

    /**
     * 删除 SKU
     *
     * @param id SKU ID
     * @return 操作结果
     */
    @DeleteMapping("/skus/{id}")
    @RequireRole("ADMIN")
    public Result<Void> deleteSku(@PathVariable Long id) {
        productService.deleteSku(id);
        return Result.ok();
    }
}
