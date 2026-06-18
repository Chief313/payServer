package com.payserver.module.auth.controller;

import com.payserver.common.result.Result;
import com.payserver.module.user.model.dto.LoginRequest;
import com.payserver.module.user.model.dto.LoginVO;
import com.payserver.module.user.model.dto.RegisterRequest;
import com.payserver.module.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * 提供登录与注册接口
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户登录
     *
     * @param request 登录参数
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(userService.login(request));
    }

    /**
     * 用户注册
     *
     * @param request 注册参数
     * @return 登录结果
     */
    @PostMapping("/register")
    public Result<LoginVO> register(@Valid @RequestBody RegisterRequest request) {
        return Result.ok(userService.register(request));
    }
}
