package com.payserver.module.order.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 * 对应数据库表 t_order
 */
@Data
public class Order {

    /** 主键ID */
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
    /** 更新时间 */
    private LocalDateTime updateTime;
}
