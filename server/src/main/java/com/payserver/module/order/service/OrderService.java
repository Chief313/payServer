package com.payserver.module.order.service;

import com.payserver.common.result.PageResult;
import com.payserver.module.order.model.entity.Order;
import com.payserver.module.order.model.vo.OrderDetailVO;

/**
 * 订单业务接口
 */
public interface OrderService {

    /**
     * 从购物车创建订单
     *
     * @param addressId 收货地址ID
     * @return 新订单号
     */
    String createFromCart(Long addressId);

    /**
     * 分页查询当前用户订单
     *
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return 分页结果
     */
    PageResult<Order> listUserOrders(int page, int size);

    /**
     * 查询订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderDetailVO getDetail(Long orderId);

    /**
     * 管理端分页查询订单
     *
     * @param status 订单状态，可为空
     * @param page   页码
     * @param size   每页大小
     * @return 分页结果
     */
    PageResult<Order> adminList(String status, int page, int size);

    /**
     * 订单发货
     *
     * @param orderId 订单ID
     */
    void ship(Long orderId);

    /**
     * 取消超时未支付订单并释放库存
     *
     * @return 取消的订单数量
     */
    int cancelTimeoutOrders();

    /**
     * 支付成功回调处理
     *
     * @param orderNo   订单号
     * @param payAmount 实付金额
     */
    void markPaid(String orderNo, java.math.BigDecimal payAmount);
}
