package com.payserver.module.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付宝沙箱配置
 * 绑定 application.yml 中 alipay 前缀的配置项
 */
@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {

    /** 是否沙箱环境 */
    private boolean sandbox;
    /** 应用ID */
    private String appId;
    /** 应用私钥 */
    private String privateKey;
    /** 支付宝公钥 */
    private String alipayPublicKey;
    /** 异步通知地址 */
    private String notifyUrl;
    /** 同步跳转地址 */
    private String returnUrl;
    /** 网关地址 */
    private String gatewayUrl;
}
