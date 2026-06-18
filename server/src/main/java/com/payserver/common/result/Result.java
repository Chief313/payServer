package com.payserver.common.result;

import lombok.Data;

/**
 * 统一 API 响应封装类
 * 所有 Controller 返回值必须使用 Result 包装
 *
 * @param <T> 业务数据类型
 */
@Data
public class Result<T> {

    /** 业务状态码，200 表示成功 */
    private int code;
    /** 提示信息 */
    private String message;
    /** 业务数据 */
    private T data;
    /** 响应时间戳 */
    private long timestamp;

    /**
     * 构造成功响应
     *
     * @param data 业务数据
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    /**
     * 构造无数据的成功响应
     *
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /**
     * 构造失败响应
     *
     * @param code    业务错误码
     * @param message 错误信息
     * @param <T>     数据类型
     * @return 失败响应
     */
    public static <T> Result<T> fail(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
}
