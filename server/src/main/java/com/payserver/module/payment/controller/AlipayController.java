package com.payserver.module.payment.controller;

import com.payserver.common.result.Result;
import com.payserver.module.payment.service.AlipayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝支付控制器
 * 提供支付创建与异步通知接口
 */
@RestController
@RequestMapping("/api/v1/user/payment/alipay")
@RequiredArgsConstructor
public class AlipayController {

    private final AlipayService alipayService;

    /**
     * 创建支付宝支付表单
     *
     * @param body 请求体，包含 orderNo
     * @return 支付表单 HTML
     */
    @PostMapping("/create")
    public Result<Map<String, String>> create(@RequestBody Map<String, String> body) {
        String orderNo = body.get("orderNo");
        String payForm = alipayService.createPayForm(orderNo);
        return Result.ok(Map.of("payForm", payForm));
    }

    /**
     * 支付宝异步通知回调
     * 对支付宝网关返回纯文本 success/failure
     *
     * @param request HTTP 请求
     * @return 处理结果文本
     */
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values != null && values.length > 0) {
                params.put(key, values[0]);
            }
        });
        boolean success = alipayService.handleNotify(params);
        return success ? "success" : "failure";
    }
}
