package com.payserver.module.user.service;

import com.payserver.module.user.model.dto.AddressRequest;
import com.payserver.module.user.model.dto.LoginRequest;
import com.payserver.module.user.model.dto.LoginVO;
import com.payserver.module.user.model.dto.RegisterRequest;
import com.payserver.module.user.model.dto.UserProfileVO;
import com.payserver.module.user.model.entity.UserAddress;

import java.util.List;

/**
 * 用户业务服务接口
 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param request 登录参数
     * @return 登录结果（含 Token）
     */
    LoginVO login(LoginRequest request);

    /**
     * 用户注册
     *
     * @param request 注册参数
     * @return 登录结果（含 Token）
     */
    LoginVO register(RegisterRequest request);

    /**
     * 获取用户个人资料
     *
     * @param userId 用户ID
     * @return 个人资料
     */
    UserProfileVO getProfile(Long userId);

    /**
     * 查询用户收货地址列表
     *
     * @param userId 用户ID
     * @return 地址列表
     */
    List<UserAddress> listAddresses(Long userId);

    /**
     * 新增收货地址
     *
     * @param userId  用户ID
     * @param request 地址参数
     * @return 新增后的地址
     */
    UserAddress addAddress(Long userId, AddressRequest request);

    /**
     * 更新收货地址
     *
     * @param userId    用户ID
     * @param addressId 地址ID
     * @param request   地址参数
     * @return 更新后的地址
     */
    UserAddress updateAddress(Long userId, Long addressId, AddressRequest request);

    /**
     * 删除收货地址
     *
     * @param userId    用户ID
     * @param addressId 地址ID
     */
    void deleteAddress(Long userId, Long addressId);
}
