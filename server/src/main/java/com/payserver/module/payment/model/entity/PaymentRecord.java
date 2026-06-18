package com.payserver.module.payment.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体
 * 对应数据库表 payment_record
 */
@Data
public class PaymentRecord {

    /** 主键ID */
    private Long id;
    /** 订单号 */
    private String orderNo;
    /** 支付渠道 */
    private String payChannel;
    /** 第三方交易号 */
    private String tradeNo;
    /** 支付金额 */
    private BigDecimal amount;
    /** 支付状态：PENDING/SUCCESS/FAILED */
    private String status;
    /** 支付时间 */
    private LocalDateTime payTime;
    /** 异步通知原文 */
    private String notifyData;
    /** 创建时间 */
    private LocalDateTime createTime;
}
