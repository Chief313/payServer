package com.payserver.module.payment.mapper;

import com.payserver.module.payment.model.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 支付记录数据访问层
 */
@Mapper
public interface PaymentMapper {

    /**
     * 插入支付记录
     *
     * @param record 支付记录
     * @return 影响行数
     */
    int insert(PaymentRecord record);

    /**
     * 根据订单号查询支付记录
     *
     * @param orderNo 订单号
     * @return 支付记录
     */
    PaymentRecord findByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 更新支付成功
     *
     * @param orderNo    订单号
     * @param tradeNo    第三方交易号
     * @param payTime    支付时间
     * @param notifyData 通知原文
     * @return 影响行数
     */
    int updateSuccess(@Param("orderNo") String orderNo,
                      @Param("tradeNo") String tradeNo,
                      @Param("payTime") LocalDateTime payTime,
                      @Param("notifyData") String notifyData);
}
