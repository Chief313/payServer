package com.payserver.module.user.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户收货地址实体
 * 对应数据库表 user_address
 */
@Data
public class UserAddress {

    /** 主键ID */
    private Long id;
    /** 用户ID */
    private Long userId;
    /** 收货人 */
    private String receiver;
    /** 联系电话 */
    private String phone;
    /** 省份 */
    private String province;
    /** 城市 */
    private String city;
    /** 区县 */
    private String district;
    /** 详细地址 */
    private String detail;
    /** 是否默认：1是 0否 */
    private Integer isDefault;
    /** 创建时间 */
    private LocalDateTime createTime;
}
