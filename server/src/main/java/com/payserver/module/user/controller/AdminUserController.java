package com.payserver.module.user.controller;

import com.payserver.common.exception.BizException;
import com.payserver.common.result.PageResult;
import com.payserver.common.result.Result;
import com.payserver.common.util.Md5Util;
import com.payserver.module.user.mapper.UserMapper;
import com.payserver.module.user.model.entity.SysUser;
import com.payserver.security.RequireRole;
import com.payserver.security.UserContext;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端用户管理接口。
 */
@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final UserMapper userMapper;

    public AdminUserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping
    @RequireRole("ADMIN")
    public Result<PageResult<SysUser>> listUsers(@RequestParam(required = false) String keyword,
                                                 @RequestParam(required = false) String role,
                                                 @RequestParam(required = false) Integer status,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int offset = (safePage - 1) * safeSize;
        List<SysUser> list = userMapper.findAdminList(trimToNull(keyword), trimToNull(role), status, offset, safeSize);
        list.forEach(this::hidePassword);
        long total = userMapper.countAdmin(trimToNull(keyword), trimToNull(role), status);
        return Result.ok(PageResult.of(list, total, safePage, safeSize));
    }

    @PostMapping
    @RequireRole("ADMIN")
    public Result<SysUser> createUser(@RequestBody SysUser user) {
        validateUser(user, true);
        if (userMapper.findByUsername(user.getUsername()) != null) {
            throw new BizException("用户名已存在");
        }
        user.setUsername(user.getUsername().trim());
        user.setNickname(defaultIfBlank(user.getNickname(), user.getUsername()));
        user.setPassword(Md5Util.encrypt(user.getPassword()));
        user.setRole(normalizeRole(user.getRole()));
        user.setStatus(user.getStatus() == null ? 1 : user.getStatus());
        userMapper.insert(user);
        SysUser created = userMapper.findById(user.getId());
        return Result.ok(hidePassword(created));
    }

    @PutMapping("/{id}")
    @RequireRole("ADMIN")
    public Result<SysUser> updateUser(@PathVariable Long id, @RequestBody SysUser user) {
        SysUser existing = userMapper.findById(id);
        if (existing == null) {
            throw new BizException("用户不存在");
        }
        user.setId(id);
        user.setNickname(defaultIfBlank(user.getNickname(), existing.getUsername()));
        user.setRole(normalizeRole(user.getRole()));
        user.setStatus(user.getStatus() == null ? 1 : user.getStatus());
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(Md5Util.encrypt(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        userMapper.adminUpdate(user);
        return Result.ok(hidePassword(userMapper.findById(id)));
    }

    @DeleteMapping("/{id}")
    @RequireRole("ADMIN")
    public Result<Void> deleteUser(@PathVariable Long id) {
        SysUser existing = userMapper.findById(id);
        if (existing == null) {
            throw new BizException("用户不存在");
        }
        UserContext context = UserContext.get();
        if (context != null && id.equals(context.getUserId())) {
            throw new BizException("不能删除当前登录账号");
        }
        userMapper.deleteById(id);
        return Result.ok();
    }

    private void validateUser(SysUser user, boolean requirePassword) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new BizException("用户名不能为空");
        }
        if (requirePassword && (user.getPassword() == null || user.getPassword().isBlank())) {
            throw new BizException("密码不能为空");
        }
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "USER";
        }
        String normalized = role.trim().toUpperCase();
        if (!"USER".equals(normalized) && !"ADMIN".equals(normalized)) {
            throw new BizException("角色不合法");
        }
        return normalized;
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private SysUser hidePassword(SysUser user) {
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }
}
