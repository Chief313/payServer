package com.payserver.module.user.model.dto;

import lombok.Data;

/**
 * 登录成功响应数据
 */
@Data
public class LoginVO {

    /** JWT Token */
    private String token;
    /** 用户ID */
    private Long userId;
    /** 用户名 */
    private String username;
    /** 昵称 */
    private String nickname;
    /** 角色 */
    private String role;
}
