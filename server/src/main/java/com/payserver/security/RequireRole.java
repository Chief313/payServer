package com.payserver.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色鉴权注解
 * 标注在 Controller 方法上，限制访问角色
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    /**
     * 允许访问的角色列表
     *
     * @return 角色数组
     */
    String[] value() default {};
}
