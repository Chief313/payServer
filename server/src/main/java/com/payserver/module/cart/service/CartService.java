package com.payserver.module.cart.service;

import com.payserver.module.cart.model.vo.CartItemVO;

import java.util.List;

/**
 * 购物车业务接口
 */
public interface CartService {

    /**
     * 查询当前用户购物车列表
     *
     * @return 购物车条目列表
     */
    List<CartItemVO> list();

    /**
     * 添加商品到购物车
     *
     * @param skuId    SKU ID
     * @param quantity 数量
     */
    void add(Long skuId, Integer quantity);

    /**
     * 更新购物车条目数量
     *
     * @param id       购物车条目ID
     * @param quantity 新数量
     */
    void updateQuantity(Long id, Integer quantity);

    /**
     * 移除购物车条目
     *
     * @param id 购物车条目ID
     */
    void remove(Long id);
}
