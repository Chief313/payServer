package com.payserver.module.cart.mapper;

import com.payserver.module.cart.model.entity.CartItem;
import com.payserver.module.cart.model.vo.CartItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车数据访问层
 */
@Mapper
public interface CartMapper {

    /**
     * 查询用户购物车列表（含商品信息）
     *
     * @param userId 用户ID
     * @return 购物车展示列表
     */
    List<CartItemVO> listByUserId(@Param("userId") Long userId);

    /**
     * 根据用户与 SKU 查询购物车条目
     *
     * @param userId 用户ID
     * @param skuId  SKU ID
     * @return 购物车条目，不存在时返回 null
     */
    CartItem findByUserAndSku(@Param("userId") Long userId, @Param("skuId") Long skuId);

    /**
     * 新增购物车条目
     *
     * @param item 购物车条目
     * @return 影响行数
     */
    int insert(CartItem item);

    /**
     * 更新购物车条目
     *
     * @param item 购物车条目
     * @return 影响行数
     */
    int update(CartItem item);

    /**
     * 根据ID查询购物车条目
     *
     * @param id 条目ID
     * @return 购物车条目
     */
    CartItem findById(@Param("id") Long id);

    /**
     * 删除购物车条目
     *
     * @param id 条目ID
     * @return 影响行数
     */
    int delete(@Param("id") Long id);

    /**
     * 删除用户已选中的购物车条目
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteSelectedByUserId(@Param("userId") Long userId);
}
