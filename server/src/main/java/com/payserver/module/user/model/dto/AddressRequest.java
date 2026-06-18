package com.payserver.module.user.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 收货地址新增/修改请求参数
 */
@Data
public class AddressRequest {

    /** 收货人 */
    @NotBlank(message = "收货人不能为空")
    private String receiver;

    /** 联系电话 */
    @NotBlank(message = "联系电话不能为空")
    private String phone;

    /** 省份 */
    @NotBlank(message = "省份不能为空")
    private String province;

    /** 城市 */
    @NotBlank(message = "城市不能为空")
    private String city;

    /** 区县 */
    @NotBlank(message = "区县不能为空")
    private String district;

    /** 详细地址 */
    @NotBlank(message = "详细地址不能为空")
    private String detail;

    /** 是否默认：1 是，0 否 */
    private Integer isDefault;
}
