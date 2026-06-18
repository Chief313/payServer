package com.payserver.security;

import lombok.Data;

/**
 * 当前登录用户上下文信息
 * 存储在 ThreadLocal 中供业务层使用
 */
@Data
public class UserContext {

    /** 用户ID */
    private Long userId;
    /** 用户名 */
    private String username;
    /** 角色：USER / ADMIN */
    private String role;

    private static final ThreadLocal<UserContext> HOLDER = new ThreadLocal<>();

    /**
     * 设置当前用户上下文
     *
     * @param context 用户上下文
     */
    public static void set(UserContext context) {
        HOLDER.set(context);
    }

    /**
     * 获取当前用户上下文
     *
     * @return 用户上下文，未登录时返回 null
     */
    public static UserContext get() {
        return HOLDER.get();
    }

    /**
     * 清除当前用户上下文
     */
    public static void clear() {
        HOLDER.remove();
    }
}
