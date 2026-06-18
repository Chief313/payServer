package com.payserver.module.payment.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.payserver.common.exception.BizException;
import com.payserver.module.order.mapper.OrderMapper;
import com.payserver.module.order.model.entity.Order;
import com.payserver.module.order.model.enums.OrderStatus;
import com.payserver.module.order.service.OrderService;
import com.payserver.module.payment.config.AlipayConfig;
import com.payserver.module.payment.mapper.PaymentMapper;
import com.payserver.module.payment.model.entity.PaymentRecord;
import com.payserver.module.payment.service.AlipayService;
import com.payserver.security.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 支付宝沙箱支付业务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlipayServiceImpl implements AlipayService {

    private final AlipayConfig alipayConfig;
    private final OrderMapper orderMapper;
    private final PaymentMapper paymentMapper;
    private final OrderService orderService;

    /**
     * 创建支付宝电脑网站支付表单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createPayForm(String orderNo) {
        Long userId = requireUserId();
        Order order = orderMapper.findByOrderNo(orderNo);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BizException(403, "无权支付该订单");
        }
        if (!OrderStatus.PENDING_PAY.name().equals(order.getStatus())) {
            throw new BizException("订单状态不允许支付");
        }

        PaymentRecord existing = paymentMapper.findByOrderNo(orderNo);
        if (existing == null) {
            PaymentRecord record = new PaymentRecord();
            record.setOrderNo(orderNo);
            record.setPayChannel("ALIPAY");
            record.setAmount(order.getTotalAmount());
            record.setStatus("PENDING");
            paymentMapper.insert(record);
        }

        try {
            AlipayClient client = buildClient();
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setNotifyUrl(alipayConfig.getNotifyUrl());
            request.setReturnUrl(alipayConfig.getReturnUrl());
            String bizContent = String.format(
                    "{\"out_trade_no\":\"%s\",\"total_amount\":\"%s\",\"subject\":\"订单支付-%s\",\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}",
                    orderNo, order.getTotalAmount().toPlainString(), orderNo);
            request.setBizContent(bizContent);
            return client.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            log.error("创建支付宝支付表单失败, orderNo={}", orderNo, e);
            throw new BizException("创建支付失败：" + e.getMessage());
        }
    }

    /**
     * 处理支付宝异步通知，验签后更新订单与支付记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleNotify(Map<String, String> params) {
        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(
                    params,
                    alipayConfig.getAlipayPublicKey(),
                    "UTF-8",
                    "RSA2");
            if (!signVerified) {
                log.warn("支付宝通知验签失败, params={}", params);
                return false;
            }

            String tradeStatus = params.get("trade_status");
            if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
                return true;
            }

            String orderNo = params.get("out_trade_no");
            String tradeNo = params.get("trade_no");
            String totalAmount = params.get("total_amount");
            BigDecimal payAmount = new BigDecimal(totalAmount);

            PaymentRecord record = paymentMapper.findByOrderNo(orderNo);
            if (record != null && "SUCCESS".equals(record.getStatus())) {
                return true;
            }

            String notifyData = params.toString();
            paymentMapper.updateSuccess(orderNo, tradeNo, LocalDateTime.now(), notifyData);
            orderService.markPaid(orderNo, payAmount);
            return true;
        } catch (Exception e) {
            log.error("处理支付宝通知异常", e);
            return false;
        }
    }

    /**
     * 构建支付宝客户端
     */
    private AlipayClient buildClient() {
        return new DefaultAlipayClient(
                alipayConfig.getGatewayUrl(),
                alipayConfig.getAppId(),
                alipayConfig.getPrivateKey(),
                "json",
                "UTF-8",
                alipayConfig.getAlipayPublicKey(),
                "RSA2");
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
