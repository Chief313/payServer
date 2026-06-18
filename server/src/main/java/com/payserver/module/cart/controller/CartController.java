package com.payserver.module.cart.controller;

import com.payserver.common.result.Result;
import com.payserver.module.cart.model.vo.CartItemVO;
import com.payserver.module.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 * 提供用户购物车增删改查接口
 */
@RestController
@RequestMapping("/api/v1/user/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 查询当前用户购物车列表
     *
     * @return 购物车条目列表
     */
    @GetMapping
    public Result<List<CartItemVO>> list() {
        return Result.ok(cartService.list());
    }

    /**
     * 添加商品到购物车
     *
     * @param body 请求体，包含 skuId 与 quantity
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> add(@RequestBody Map<String, Object> body) {
        Long skuId = Long.valueOf(body.get("skuId").toString());
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        cartService.add(skuId, quantity);
        return Result.ok();
    }

    /**
     * 更新购物车条目数量
     *
     * @param id   购物车条目ID
     * @param body 请求体，包含 quantity
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result<Void> updateQuantity(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        cartService.updateQuantity(id, quantity);
        return Result.ok();
    }

    /**
     * 移除购物车条目
     *
     * @param id 购物车条目ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        cartService.remove(id);
        return Result.ok();
    }
}
