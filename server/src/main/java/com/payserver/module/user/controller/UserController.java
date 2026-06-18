package com.payserver.module.user.controller;

import com.payserver.common.result.Result;
import com.payserver.module.user.model.dto.AddressRequest;
import com.payserver.module.user.model.dto.UserProfileVO;
import com.payserver.module.user.model.entity.UserAddress;
import com.payserver.module.user.service.UserService;
import com.payserver.security.UserContext;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户控制器
 * 提供个人资料与收货地址管理
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取当前用户个人资料
     *
     * @return 个人资料
     */
    @GetMapping("/profile")
    public Result<UserProfileVO> getProfile() {
        Long userId = requireUserId();
        return Result.ok(userService.getProfile(userId));
    }

    /**
     * 查询当前用户收货地址列表
     *
     * @return 地址列表
     */
    @GetMapping("/addresses")
    public Result<List<UserAddress>> listAddresses() {
        Long userId = requireUserId();
        return Result.ok(userService.listAddresses(userId));
    }

    /**
     * 新增收货地址
     *
     * @param request 地址参数
     * @return 新增地址
     */
    @PostMapping("/addresses")
    public Result<UserAddress> addAddress(@Valid @RequestBody AddressRequest request) {
        Long userId = requireUserId();
        return Result.ok(userService.addAddress(userId, request));
    }

    /**
     * 更新收货地址
     *
     * @param id      地址ID
     * @param request 地址参数
     * @return 更新后的地址
     */
    @PutMapping("/addresses/{id}")
    public Result<UserAddress> updateAddress(@PathVariable Long id,
                                             @Valid @RequestBody AddressRequest request) {
        Long userId = requireUserId();
        return Result.ok(userService.updateAddress(userId, id, request));
    }

    /**
     * 删除收货地址
     *
     * @param id 地址ID
     * @return 操作结果
     */
    @DeleteMapping("/addresses/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id) {
        Long userId = requireUserId();
        userService.deleteAddress(userId, id);
        return Result.ok();
    }

    /**
     * 获取当前登录用户ID
     */
    private Long requireUserId() {
        UserContext context = UserContext.get();
        return context.getUserId();
    }
}
