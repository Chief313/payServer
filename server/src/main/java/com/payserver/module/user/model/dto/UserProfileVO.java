package com.payserver.module.user.model.dto;

import lombok.Data;

/**
 * 用户个人资料响应数据
 */
@Data
public class UserProfileVO {

    /** 用户ID */
    private Long userId;
    /** 用户名 */
    private String username;
    /** 昵称 */
    private String nickname;
    /** 手机号 */
    private String phone;
    /** 邮箱 */
    private String email;
    /** 角色 */
    private String role;
}
