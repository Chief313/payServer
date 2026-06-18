package com.payserver.module.product.service.impl;

import com.payserver.common.exception.BizException;
import com.payserver.common.result.PageResult;
import com.payserver.module.product.mapper.CategoryMapper;
import com.payserver.module.product.mapper.ProductMapper;
import com.payserver.module.product.mapper.SkuMapper;
import com.payserver.module.product.model.dto.ProductDetailVO;
import com.payserver.module.product.model.entity.Category;
import com.payserver.module.product.model.entity.Product;
import com.payserver.module.product.model.entity.Sku;
import com.payserver.module.product.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品业务服务实现
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;
    private final SkuMapper skuMapper;

    public ProductServiceImpl(CategoryMapper categoryMapper,
                              ProductMapper productMapper,
                              SkuMapper skuMapper) {
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
        this.skuMapper = skuMapper;
    }

    /**
     * 分页查询上架商品
     */
    @Override
    public PageResult<Product> listProducts(Long categoryId, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int offset = (safePage - 1) * safeSize;
        List<Product> list = productMapper.findOnSaleList(categoryId, offset, safeSize);
        long total = productMapper.countOnSale(categoryId);
        return PageResult.of(list, total, safePage, safeSize);
    }

    /**
     * 获取商品详情
     */
    @Override
    public ProductDetailVO getProductDetail(Long productId) {
        Product product = productMapper.findById(productId);
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new BizException("商品不存在或已下架");
        }
        ProductDetailVO vo = new ProductDetailVO();
        vo.setProduct(product);
        vo.setSkus(skuMapper.findByProductId(productId));
        return vo;
    }

    /**
     * 查询全部分类
     */
    @Override
    public List<Category> listCategories() {
        return categoryMapper.findAll();
    }

    /**
     * 新增分类
     */
    @Override
    public Category createCategory(Category category) {
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        categoryMapper.insert(category);
        return categoryMapper.findById(category.getId());
    }

    /**
     * 更新分类
     */
    @Override
    public Category updateCategory(Category category) {
        Category existing = categoryMapper.findById(category.getId());
        if (existing == null) {
            throw new BizException("分类不存在");
        }
        categoryMapper.update(category);
        return categoryMapper.findById(category.getId());
    }

    /**
     * 删除分类
     */
    @Override
    public void deleteCategory(Long id) {
        Category existing = categoryMapper.findById(id);
        if (existing == null) {
            throw new BizException("分类不存在");
        }
        categoryMapper.deleteById(id);
    }

    /**
     * 管理端分页查询商品
     */
    @Override
    public PageResult<Product> adminListProducts(Long categoryId, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int offset = (safePage - 1) * safeSize;
        List<Product> list = productMapper.findAdminList(categoryId, offset, safeSize);
        long total = productMapper.countAdmin(categoryId);
        return PageResult.of(list, total, safePage, safeSize);
    }

    /**
     * 新增商品
     */
    @Override
    public Product createProduct(Product product) {
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        if (categoryMapper.findById(product.getCategoryId()) == null) {
            throw new BizException("分类不存在");
        }
        productMapper.insert(product);
        return productMapper.findById(product.getId());
    }

    /**
     * 更新商品
     */
    @Override
    public Product updateProduct(Product product) {
        Product existing = productMapper.findById(product.getId());
        if (existing == null) {
            throw new BizException("商品不存在");
        }
        if (product.getCategoryId() != null && categoryMapper.findById(product.getCategoryId()) == null) {
            throw new BizException("分类不存在");
        }
        productMapper.update(product);
        return productMapper.findById(product.getId());
    }

    /**
     * 删除商品
     */
    @Override
    public void deleteProduct(Long productId) {
        Product existing = productMapper.findById(productId);
        if (existing == null) {
            throw new BizException("商品不存在");
        }
        skuMapper.deleteByProductId(productId);
        productMapper.deleteById(productId);
    }

    /**
     * 新增 SKU
     */
    @Override
    public Sku createSku(Sku sku) {
        if (productMapper.findById(sku.getProductId()) == null) {
            throw new BizException("商品不存在");
        }
        if (sku.getStock() == null) {
            sku.setStock(0);
        }
        if (sku.getLockedStock() == null) {
            sku.setLockedStock(0);
        }
        skuMapper.insert(sku);
        return skuMapper.findById(sku.getId());
    }

    /**
     * 更新 SKU
     */
    @Override
    public Sku updateSku(Sku sku) {
        Sku existing = skuMapper.findById(sku.getId());
        if (existing == null) {
            throw new BizException("SKU 不存在");
        }
        skuMapper.update(sku);
        return skuMapper.findById(sku.getId());
    }

    /**
     * 删除 SKU
     */
    @Override
    public void deleteSku(Long skuId) {
        Sku existing = skuMapper.findById(skuId);
        if (existing == null) {
            throw new BizException("SKU 不存在");
        }
        skuMapper.deleteById(skuId);
    }
}
