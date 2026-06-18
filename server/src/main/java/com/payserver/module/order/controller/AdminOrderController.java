package com.payserver.module.order.controller;

import com.payserver.common.result.PageResult;
import com.payserver.common.result.Result;
import com.payserver.module.order.model.entity.Order;
import com.payserver.module.order.model.vo.OrderDetailVO;
import com.payserver.module.order.service.OrderService;
import com.payserver.security.RequireRole;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端订单控制器
 * 提供订单管理与发货接口
 */
@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    /**
     * 分页查询全部订单
     *
     * @param status 订单状态，可选
     * @param page   页码，默认1
     * @param size   每页大小，默认10
     * @return 分页订单列表
     */
    @GetMapping
    @RequireRole("ADMIN")
    public Result<PageResult<Order>> list(@RequestParam(required = false) String status,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        return Result.ok(orderService.adminList(status, page, size));
    }

    /**
     * 查询订单详情
     *
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/{id}")
    @RequireRole("ADMIN")
    public Result<OrderDetailVO> detail(@PathVariable Long id) {
        return Result.ok(orderService.getDetail(id));
    }

    /**
     * 订单发货
     *
     * @param id 订单ID
     * @return 操作结果
     */
    @PutMapping("/{id}/ship")
    @RequireRole("ADMIN")
    public Result<Void> ship(@PathVariable Long id) {
        orderService.ship(id);
        return Result.ok();
    }

    /**
     * 手动触发取消超时未支付订单
     *
     * @return 取消的订单数量
     */
    @PostMapping("/cancel-timeout")
    @RequireRole("ADMIN")
    public Result<Integer> cancelTimeout() {
        return Result.ok(orderService.cancelTimeoutOrders());
    }
}
