package com.payserver.module.payment.service;

import java.util.Map;

/**
 * 支付宝支付业务接口
 */
public interface AlipayService {

    /**
     * 创建支付宝电脑网站支付表单
     *
     * @param orderNo 订单号
     * @return 支付表单 HTML
     */
    String createPayForm(String orderNo);

    /**
     * 处理支付宝异步通知
     *
     * @param params 通知参数
     * @return 是否处理成功
     */
    boolean handleNotify(Map<String, String> params);
}
