package com.payserver.module.order.controller;

import com.payserver.common.result.PageResult;
import com.payserver.common.result.Result;
import com.payserver.module.order.model.entity.Order;
import com.payserver.module.order.model.vo.OrderDetailVO;
import com.payserver.module.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户订单控制器
 * 提供用户端订单查询与下单接口
 */
@RestController
@RequestMapping("/api/v1/user/orders")
@RequiredArgsConstructor
public class UserOrderController {

    private final OrderService orderService;

    /**
     * 从购物车创建订单
     *
     * @param body 请求体，包含 addressId
     * @return 新订单号
     */
    @PostMapping
    public Result<Map<String, String>> create(@RequestBody Map<String, Object> body) {
        Long addressId = Long.valueOf(body.get("addressId").toString());
        String orderNo = orderService.createFromCart(addressId);
        return Result.ok(Map.of("orderNo", orderNo));
    }

    /**
     * 分页查询当前用户订单
     *
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @return 分页订单列表
     */
    @GetMapping
    public Result<PageResult<Order>> list(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return Result.ok(orderService.listUserOrders(page, size));
    }

    /**
     * 查询订单详情
     *
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/{id}")
    public Result<OrderDetailVO> detail(@PathVariable Long id) {
        return Result.ok(orderService.getDetail(id));
    }
}
