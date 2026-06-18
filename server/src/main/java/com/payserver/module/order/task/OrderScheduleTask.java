package com.payserver.module.order.task;

import com.payserver.module.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订单定时任务
 * 定期取消超时未支付订单并释放库存
 */
@Component
@RequiredArgsConstructor
public class OrderScheduleTask {

    private final OrderService orderService;

    /**
     * 每 5 分钟扫描一次超时未支付订单
     */
    @Scheduled(fixedRate = 300_000)
    public void cancelTimeoutOrders() {
        orderService.cancelTimeoutOrders();
    }
}
