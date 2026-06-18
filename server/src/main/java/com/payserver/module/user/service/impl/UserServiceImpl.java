package com.payserver.module.user.service.impl;

import com.payserver.common.exception.BizException;
import com.payserver.common.util.Md5Util;
import com.payserver.module.user.mapper.UserAddressMapper;
import com.payserver.module.user.mapper.UserMapper;
import com.payserver.module.user.model.dto.AddressRequest;
import com.payserver.module.user.model.dto.LoginRequest;
import com.payserver.module.user.model.dto.LoginVO;
import com.payserver.module.user.model.dto.RegisterRequest;
import com.payserver.module.user.model.dto.UserProfileVO;
import com.payserver.module.user.model.entity.SysUser;
import com.payserver.module.user.model.entity.UserAddress;
import com.payserver.module.user.service.UserService;
import com.payserver.security.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户业务服务实现
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserAddressMapper userAddressMapper;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserMapper userMapper, UserAddressMapper userAddressMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.userAddressMapper = userAddressMapper;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户登录
     */
    @Override
    public LoginVO login(LoginRequest request) {
        SysUser user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new BizException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BizException("账号已被禁用");
        }
        if (!Md5Util.matches(request.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        return buildLoginVO(user);
    }

    /**
     * 用户注册
     */
    @Override
    public LoginVO register(RegisterRequest request) {
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new BizException("用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(Md5Util.encrypt(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setPhone(request.getPhone());
        user.setRole("USER");
        user.setStatus(1);
        userMapper.insert(user);
        return buildLoginVO(user);
    }

    /**
     * 获取用户个人资料
     */
    @Override
    public UserProfileVO getProfile(Long userId) {
        SysUser user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        UserProfileVO vo = new UserProfileVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setRole(user.getRole());
        return vo;
    }

    /**
     * 查询用户收货地址列表
     */
    @Override
    public List<UserAddress> listAddresses(Long userId) {
        return userAddressMapper.findByUserId(userId);
    }

    /**
     * 新增收货地址
     */
    @Override
    public UserAddress addAddress(Long userId, AddressRequest request) {
        UserAddress address = toAddress(userId, request);
        userAddressMapper.insert(address);
        return address;
    }

    /**
     * 更新收货地址
     */
    @Override
    public UserAddress updateAddress(Long userId, Long addressId, AddressRequest request) {
        UserAddress existing = userAddressMapper.findById(addressId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BizException("地址不存在");
        }
        UserAddress address = toAddress(userId, request);
        address.setId(addressId);
        userAddressMapper.update(address);
        return userAddressMapper.findById(addressId);
    }

    /**
     * 删除收货地址
     */
    @Override
    public void deleteAddress(Long userId, Long addressId) {
        UserAddress existing = userAddressMapper.findById(addressId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BizException("地址不存在");
        }
        userAddressMapper.deleteById(addressId);
    }

    /**
     * 构建登录响应
     */
    private LoginVO buildLoginVO(SysUser user) {
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setRole(user.getRole());
        return vo;
    }

    /**
     * 请求参数转地址实体
     */
    private UserAddress toAddress(Long userId, AddressRequest request) {
        UserAddress address = new UserAddress();
        address.setUserId(userId);
        address.setReceiver(request.getReceiver());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetail(request.getDetail());
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : 0);
        return address;
    }
}
