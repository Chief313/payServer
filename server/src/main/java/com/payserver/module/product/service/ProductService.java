package com.payserver.module.product.service;

import com.payserver.common.result.PageResult;
import com.payserver.module.product.model.dto.ProductDetailVO;
import com.payserver.module.product.model.entity.Category;
import com.payserver.module.product.model.entity.Product;
import com.payserver.module.product.model.entity.Sku;

import java.util.List;

/**
 * 商品业务服务接口
 */
public interface ProductService {

    /**
     * 分页查询上架商品（用户端）
     *
     * @param categoryId 分类ID，可为空
     * @param page       页码，从 1 开始
     * @param size       每页条数
     * @return 分页结果
     */
    PageResult<Product> listProducts(Long categoryId, int page, int size);

    /**
     * 获取商品详情（含 SKU）
     *
     * @param productId 商品ID
     * @return 商品详情
     */
    ProductDetailVO getProductDetail(Long productId);

    /**
     * 查询全部分类
     *
     * @return 分类列表
     */
    List<Category> listCategories();

    /**
     * 新增分类
     *
     * @param category 分类实体
     * @return 新增后的分类
     */
    Category createCategory(Category category);

    /**
     * 更新分类
     *
     * @param category 分类实体
     * @return 更新后的分类
     */
    Category updateCategory(Category category);

    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    void deleteCategory(Long id);

    /**
     * 分页查询全部商品（管理端）
     *
     * @param categoryId 分类ID，可为空
     * @param page       页码
     * @param size       每页条数
     * @return 分页结果
     */
    PageResult<Product> adminListProducts(Long categoryId, int page, int size);

    /**
     * 新增商品
     *
     * @param product 商品实体
     * @return 新增后的商品
     */
    Product createProduct(Product product);

    /**
     * 更新商品
     *
     * @param product 商品实体
     * @return 更新后的商品
     */
    Product updateProduct(Product product);

    /**
     * 删除商品
     *
     * @param productId 商品ID
     */
    void deleteProduct(Long productId);

    /**
     * 新增 SKU
     *
     * @param sku SKU 实体
     * @return 新增后的 SKU
     */
    Sku createSku(Sku sku);

    /**
     * 更新 SKU
     *
     * @param sku SKU 实体
     * @return 更新后的 SKU
     */
    Sku updateSku(Sku sku);

    /**
     * 删除 SKU
     *
     * @param skuId SKU ID
     */
    void deleteSku(Long skuId);
}
