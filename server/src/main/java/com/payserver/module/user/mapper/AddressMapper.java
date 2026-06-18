package com.payserver.module.user.mapper;

import com.payserver.module.user.model.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户收货地址数据访问层
 */
@Mapper
public interface AddressMapper {

    /**
     * 根据ID查询收货地址
     *
     * @param id 地址ID
     * @return 收货地址
     */
    UserAddress findById(@Param("id") Long id);
}
