package com.payserver.module.cart.service.impl;

import com.payserver.common.exception.BizException;
import com.payserver.module.cart.mapper.CartMapper;
import com.payserver.module.cart.model.entity.CartItem;
import com.payserver.module.cart.model.vo.CartItemVO;
import com.payserver.module.cart.service.CartService;
import com.payserver.module.product.mapper.SkuMapper;
import com.payserver.module.product.model.entity.Sku;
import com.payserver.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车业务实现类
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final SkuMapper skuMapper;

    /**
     * 查询当前用户购物车列表
     */
    @Override
    public List<CartItemVO> list() {
        Long userId = requireUserId();
        return cartMapper.listByUserId(userId);
    }

    /**
     * 添加商品到购物车，已存在则累加数量
     */
    @Override
    public void add(Long skuId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BizException("购买数量必须大于0");
        }
        Long userId = requireUserId();
        Sku sku = skuMapper.findById(skuId);
        if (sku == null) {
            throw new BizException("商品规格不存在");
        }
        if (sku.getStock() < quantity) {
            throw new BizException("库存不足");
        }
        CartItem existing = cartMapper.findByUserAndSku(userId, skuId);
        if (existing != null) {
            int newQty = existing.getQuantity() + quantity;
            if (sku.getStock() < newQty) {
                throw new BizException("库存不足");
            }
            existing.setQuantity(newQty);
            cartMapper.update(existing);
            return;
        }
        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setSkuId(skuId);
        item.setQuantity(quantity);
        item.setSelected(1);
        cartMapper.insert(item);
    }

    /**
     * 更新购物车条目数量
     */
    @Override
    public void updateQuantity(Long id, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BizException("购买数量必须大于0");
        }
        Long userId = requireUserId();
        CartItem item = findOwnedItem(id, userId);
        Sku sku = skuMapper.findById(item.getSkuId());
        if (sku == null || sku.getStock() < quantity) {
            throw new BizException("库存不足");
        }
        item.setQuantity(quantity);
        cartMapper.update(item);
    }

    /**
     * 移除购物车条目
     */
    @Override
    public void remove(Long id) {
        Long userId = requireUserId();
        findOwnedItem(id, userId);
        cartMapper.delete(id);
    }

    /**
     * 获取当前登录用户ID
     */
    private Long requireUserId() {
        UserContext ctx = UserContext.get();
        if (ctx == null || ctx.getUserId() == null) {
            throw new BizException(401, "未登录");
        }
        return ctx.getUserId();
    }

    /**
     * 查询并校验购物车条目归属
     */
    private CartItem findOwnedItem(Long id, Long userId) {
        CartItem item = cartMapper.findById(id);
        if (item == null || !item.getUserId().equals(userId)) {
            throw new BizException("购物车条目不存在");
        }
        return item;
    }
}
