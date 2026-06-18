package com.payserver.module.user.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户注册请求参数
 */
@Data
public class RegisterRequest {

    /** 登录用户名 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 登录密码 */
    @NotBlank(message = "密码不能为空")
    private String password;

    /** 昵称 */
    private String nickname;

    /** 手机号 */
    private String phone;
}
