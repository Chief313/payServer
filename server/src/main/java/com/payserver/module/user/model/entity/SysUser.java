package com.payserver.module.user.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统用户实体，对应 sys_user 表
 */
@Data
public class SysUser {

    /** 用户ID */
    private Long id;
    /** 登录用户名 */
    private String username;
    /** MD5 加密密码 */
    private String password;
    /** 昵称 */
    private String nickname;
    /** 手机号 */
    private String phone;
    /** 邮箱 */
    private String email;
    /** 角色：USER / ADMIN */
    private String role;
    /** 状态：1 正常，0 禁用 */
    private Integer status;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
