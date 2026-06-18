package com.payserver.module.user.mapper;

import com.payserver.module.user.model.entity.UserAddress;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户收货地址 MyBatis Mapper
 */
@Mapper
public interface UserAddressMapper {

    /**
     * 查询用户全部收货地址
     *
     * @param userId 用户ID
     * @return 地址列表
     */
    @Select("SELECT id, user_id, receiver, phone, province, city, district, detail, is_default, create_time "
            + "FROM user_address WHERE user_id = #{userId} ORDER BY is_default DESC, id DESC")
    List<UserAddress> findByUserId(Long userId);

    /**
     * 根据地址ID查询
     *
     * @param id 地址ID
     * @return 地址实体
     */
    @Select("SELECT id, user_id, receiver, phone, province, city, district, detail, is_default, create_time "
            + "FROM user_address WHERE id = #{id}")
    UserAddress findById(Long id);

    /**
     * 新增收货地址
     *
     * @param address 地址实体
     * @return 影响行数
     */
    @Insert("INSERT INTO user_address (user_id, receiver, phone, province, city, district, detail, is_default) "
            + "VALUES (#{userId}, #{receiver}, #{phone}, #{province}, #{city}, #{district}, #{detail}, #{isDefault})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserAddress address);

    /**
     * 更新收货地址
     *
     * @param address 地址实体
     * @return 影响行数
     */
    @Update("UPDATE user_address SET receiver = #{receiver}, phone = #{phone}, province = #{province}, "
            + "city = #{city}, district = #{district}, detail = #{detail}, is_default = #{isDefault} "
            + "WHERE id = #{id} AND user_id = #{userId}")
    int update(UserAddress address);

    /**
     * 删除收货地址
     *
     * @param id 地址ID
     * @return 影响行数
     */
    @Delete("DELETE FROM user_address WHERE id = #{id}")
    int deleteById(Long id);
}
