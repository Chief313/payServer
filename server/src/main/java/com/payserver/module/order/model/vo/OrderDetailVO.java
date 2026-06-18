package com.payserver.module.order.model.vo;

import com.payserver.module.order.model.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情展示对象
 */
@Data
public class OrderDetailVO {

    /** 订单ID */
    private Long id;
    /** 订单号 */
    private String orderNo;
    /** 用户ID */
    private Long userId;
    /** 订单总额 */
    private BigDecimal totalAmount;
    /** 实付金额 */
    private BigDecimal payAmount;
    /** 订单状态 */
    private String status;
    /** 收货地址快照（JSON） */
    private String addressSnapshot;
    /** 支付时间 */
    private LocalDateTime payTime;
    /** 发货时间 */
    private LocalDateTime shipTime;
    /** 完成时间 */
    private LocalDateTime completeTime;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 订单明细列表 */
    private List<OrderItem> items;
}
