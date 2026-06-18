package com.payserver.module.product.mapper;

import com.payserver.module.product.model.entity.Sku;
import com.payserver.module.product.model.entity.SkuDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商品 SKU MyBatis Mapper
 */
@Mapper
public interface SkuMapper {

    /**
     * 根据ID查询 SKU
     *
     * @param id SKU ID
     * @return SKU 实体
     */
    Sku findById(@Param("id") Long id);

    /**
     * 查询 SKU 详情（含商品信息）
     *
     * @param id SKU ID
     * @return SKU 详情
     */
    SkuDetail findDetailById(@Param("id") Long id);

    /**
     * 根据商品ID查询 SKU 列表
     *
     * @param productId 商品ID
     * @return SKU 列表
     */
    @Select("SELECT id, product_id, sku_name, price, stock, locked_stock, create_time "
            + "FROM sku WHERE product_id = #{productId} ORDER BY id ASC")
    List<Sku> findByProductId(Long productId);

    /**
     * 新增 SKU
     *
     * @param sku SKU 实体
     * @return 影响行数
     */
    @Insert("INSERT INTO sku (product_id, sku_name, price, stock, locked_stock) "
            + "VALUES (#{productId}, #{skuName}, #{price}, #{stock}, #{lockedStock})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Sku sku);

    /**
     * 更新 SKU
     *
     * @param sku SKU 实体
     * @return 影响行数
     */
    @Update("UPDATE sku SET sku_name = #{skuName}, price = #{price}, stock = #{stock}, "
            + "locked_stock = #{lockedStock} WHERE id = #{id}")
    int update(Sku sku);

    /**
     * 删除 SKU
     *
     * @param id SKU ID
     * @return 影响行数
     */
    @Delete("DELETE FROM sku WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 根据商品ID删除全部 SKU
     *
     * @param productId 商品ID
     * @return 影响行数
     */
    @Delete("DELETE FROM sku WHERE product_id = #{productId}")
    int deleteByProductId(Long productId);

    /**
     * 预占库存：可用库存减少，锁定库存增加
     *
     * @param skuId    SKU ID
     * @param quantity 数量
     * @return 影响行数，0 表示库存不足
     */
    int lockStock(@Param("skuId") Long skuId, @Param("quantity") int quantity);

    /**
     * 释放预占库存（订单取消或超时）
     *
     * @param skuId    SKU ID
     * @param quantity 数量
     * @return 影响行数
     */
    int unlockStock(@Param("skuId") Long skuId, @Param("quantity") int quantity);

    /**
     * 确认扣减预占库存（支付成功后）
     *
     * @param skuId    SKU ID
     * @param quantity 数量
     * @return 影响行数
     */
    int confirmLockedStock(@Param("skuId") Long skuId, @Param("quantity") int quantity);
}
