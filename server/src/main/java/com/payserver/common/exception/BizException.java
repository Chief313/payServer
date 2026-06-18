package com.payserver.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 用于抛出可预期的业务错误，由全局异常处理器统一转换为 Result
 */
@Getter
public class BizException extends RuntimeException {

    /** 业务错误码 */
    private final int code;

    /**
     * 创建业务异常
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 使用默认 400 错误码创建业务异常
     *
     * @param message 错误信息
     */
    public BizException(String message) {
        this(400, message);
    }
}
