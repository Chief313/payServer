package com.payserver.module.order.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payserver.common.exception.BizException;
import com.payserver.common.result.PageResult;
import com.payserver.common.util.OrderNoUtil;
import com.payserver.module.cart.mapper.CartMapper;
import com.payserver.module.cart.model.vo.CartItemVO;
import com.payserver.module.order.mapper.OrderItemMapper;
import com.payserver.module.order.mapper.OrderMapper;
import com.payserver.module.order.model.entity.Order;
import com.payserver.module.order.model.entity.OrderItem;
import com.payserver.module.order.model.enums.OrderStatus;
import com.payserver.module.order.model.vo.OrderDetailVO;
import com.payserver.module.order.service.OrderService;
import com.payserver.module.product.mapper.SkuMapper;
import com.payserver.module.product.model.entity.SkuDetail;
import com.payserver.module.user.mapper.AddressMapper;
import com.payserver.module.user.model.entity.UserAddress;
import com.payserver.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单业务实现类
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    /** 待支付超时分钟数 */
    private static final int PAY_TIMEOUT_MINUTES = 30;

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final SkuMapper skuMapper;
    private final AddressMapper addressMapper;
    private final ObjectMapper objectMapper;

    /**
     * 从购物车选中条目创建订单，并预占库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createFromCart(Long addressId) {
        Long userId = requireUserId();
        UserAddress address = addressMapper.findById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BizException("收货地址不存在");
        }
        List<CartItemVO> cartItems = cartMapper.listByUserId(userId);
        List<CartItemVO> selected = cartItems.stream()
                .filter(item -> item.getSelected() != null && item.getSelected() == 1)
                .toList();
        if (selected.isEmpty()) {
            throw new BizException("请先选择要结算的商品");
        }

        // 预占库存
        for (CartItemVO item : selected) {
            SkuDetail detail = skuMapper.findDetailById(item.getSkuId());
            if (detail == null || detail.getProductStatus() == null || detail.getProductStatus() != 1) {
                throw new BizException("商品已下架：" + item.getProductName());
            }
            int locked = skuMapper.lockStock(item.getSkuId(), item.getQuantity());
            if (locked == 0) {
                throw new BizException("库存不足：" + item.getProductName());
            }
        }

        BigDecimal totalAmount = selected.stream()
                .map(CartItemVO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String orderNo = OrderNoUtil.generate();
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING_PAY.name());
        order.setAddressSnapshot(buildAddressSnapshot(address));
        orderMapper.insert(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItemVO item : selected) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(item.getProductId());
            orderItem.setSkuId(item.getSkuId());
            orderItem.setProductName(item.getProductName());
            orderItem.setSkuName(item.getSkuName());
            orderItem.setPrice(item.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItems.add(orderItem);
        }
        orderItemMapper.batchInsert(orderItems);
        cartMapper.deleteSelectedByUserId(userId);
        return orderNo;
    }

    /**
     * 分页查询当前用户订单
     */
    @Override
    public PageResult<Order> listUserOrders(int page, int size) {
        Long userId = requireUserId();
        int offset = Math.max(page - 1, 0) * size;
        List<Order> list = orderMapper.listByUserId(userId, offset, size);
        long total = orderMapper.countByUserId(userId);
        return PageResult.of(list, total, page, size);
    }

    /**
     * 查询订单详情
     */
    @Override
    public OrderDetailVO getDetail(Long orderId) {
        Long userId = requireUserId();
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        UserContext ctx = UserContext.get();
        if (!"ADMIN".equals(ctx.getRole()) && !order.getUserId().equals(userId)) {
            throw new BizException(403, "无权查看该订单");
        }
        OrderDetailVO vo = new OrderDetailVO();
        BeanUtils.copyProperties(order, vo);
        vo.setItems(orderItemMapper.listByOrderId(orderId));
        return vo;
    }

    /**
     * 管理端分页查询订单
     */
    @Override
    public PageResult<Order> adminList(String status, int page, int size) {
        int offset = Math.max(page - 1, 0) * size;
        List<Order> list = orderMapper.adminList(status, offset, size);
        long total = orderMapper.adminCount(status);
        return PageResult.of(list, total, page, size);
    }

    /**
     * 订单发货
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ship(Long orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        if (!OrderStatus.PAID.name().equals(order.getStatus())) {
            throw new BizException("仅已支付订单可发货");
        }
        int updated = orderMapper.updateShipped(orderId, LocalDateTime.now());
        if (updated == 0) {
            throw new BizException("发货失败，订单状态已变更");
        }
    }

    /**
     * 取消超时未支付订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelTimeoutOrders() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(PAY_TIMEOUT_MINUTES);
        List<Order> timeoutOrders = orderMapper.listTimeoutOrders(OrderStatus.PENDING_PAY.name(), deadline);
        for (Order order : timeoutOrders) {
            List<OrderItem> items = orderItemMapper.listByOrderId(order.getId());
            for (OrderItem item : items) {
                skuMapper.unlockStock(item.getSkuId(), item.getQuantity());
            }
            orderMapper.updateStatus(order.getId(), OrderStatus.CANCELLED.name());
        }
        return timeoutOrders.size();
    }

    /**
     * 支付成功，更新订单状态并确认扣减预占库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markPaid(String orderNo, BigDecimal payAmount) {
        Order order = orderMapper.findByOrderNo(orderNo);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        if (OrderStatus.PAID.name().equals(order.getStatus())
                || OrderStatus.SHIPPED.name().equals(order.getStatus())
                || OrderStatus.COMPLETED.name().equals(order.getStatus())) {
            return;
        }
        if (!OrderStatus.PENDING_PAY.name().equals(order.getStatus())) {
            throw new BizException("订单状态不允许支付");
        }
        int updated = orderMapper.updatePaid(orderNo, payAmount, LocalDateTime.now());
        if (updated == 0) {
            return;
        }
        List<OrderItem> items = orderItemMapper.listByOrderId(order.getId());
        for (OrderItem item : items) {
            skuMapper.confirmLockedStock(item.getSkuId(), item.getQuantity());
        }
    }

    /**
     * 构建收货地址 JSON 快照
     */
    private String buildAddressSnapshot(UserAddress address) {
        try {
            Map<String, String> snapshot = new HashMap<>();
            snapshot.put("receiver", address.getReceiver());
            snapshot.put("phone", address.getPhone());
            snapshot.put("province", address.getProvince());
            snapshot.put("city", address.getCity());
            snapshot.put("district", address.getDistrict());
            snapshot.put("detail", address.getDetail());
            return objectMapper.writeValueAsString(snapshot);
        } catch (Exception e) {
            throw new BizException("地址快照生成失败");
        }
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
}
