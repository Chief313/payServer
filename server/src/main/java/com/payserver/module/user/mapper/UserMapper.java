package com.payserver.module.user.mapper;

import com.payserver.module.user.model.entity.SysUser;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 系统用户 MyBatis Mapper
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体，不存在时返回 null
     */
    @Select("SELECT id, username, password, nickname, phone, email, role, status, create_time, update_time "
            + "FROM sys_user WHERE username = #{username}")
    SysUser findByUsername(String username);

    /**
     * 根据用户ID查询用户
     *
     * @param id 用户ID
     * @return 用户实体，不存在时返回 null
     */
    @Select("SELECT id, username, password, nickname, phone, email, role, status, create_time, update_time "
            + "FROM sys_user WHERE id = #{id}")
    SysUser findById(Long id);

    /**
     * 管理端分页查询用户。
     */
    @Select("<script>"
            + "SELECT id, username, password, nickname, phone, email, role, status, create_time, update_time "
            + "FROM sys_user WHERE 1 = 1 "
            + "<if test='keyword != null and keyword != \"\"'>"
            + "AND (username LIKE CONCAT('%', #{keyword}, '%') "
            + "OR nickname LIKE CONCAT('%', #{keyword}, '%') "
            + "OR phone LIKE CONCAT('%', #{keyword}, '%') "
            + "OR email LIKE CONCAT('%', #{keyword}, '%')) "
            + "</if>"
            + "<if test='role != null and role != \"\"'> AND role = #{role} </if>"
            + "<if test='status != null'> AND status = #{status} </if>"
            + "ORDER BY id DESC LIMIT #{offset}, #{limit}"
            + "</script>")
    List<SysUser> findAdminList(@Param("keyword") String keyword,
                                @Param("role") String role,
                                @Param("status") Integer status,
                                @Param("offset") int offset,
                                @Param("limit") int limit);

    /**
     * 管理端统计用户数量。
     */
    @Select("<script>"
            + "SELECT COUNT(*) FROM sys_user WHERE 1 = 1 "
            + "<if test='keyword != null and keyword != \"\"'>"
            + "AND (username LIKE CONCAT('%', #{keyword}, '%') "
            + "OR nickname LIKE CONCAT('%', #{keyword}, '%') "
            + "OR phone LIKE CONCAT('%', #{keyword}, '%') "
            + "OR email LIKE CONCAT('%', #{keyword}, '%')) "
            + "</if>"
            + "<if test='role != null and role != \"\"'> AND role = #{role} </if>"
            + "<if test='status != null'> AND status = #{status} </if>"
            + "</script>")
    long countAdmin(@Param("keyword") String keyword,
                    @Param("role") String role,
                    @Param("status") Integer status);

    /**
     * 新增用户
     *
     * @param user 用户实体
     * @return 影响行数
     */
    @Insert("INSERT INTO sys_user (username, password, nickname, phone, email, role, status) "
            + "VALUES (#{username}, #{password}, #{nickname}, #{phone}, #{email}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysUser user);

    /**
     * 更新用户信息
     *
     * @param user 用户实体
     * @return 影响行数
     */
    @Update("UPDATE sys_user SET nickname = #{nickname}, phone = #{phone}, email = #{email}, "
            + "password = #{password}, status = #{status} WHERE id = #{id}")
    int update(SysUser user);

    /**
     * 管理端更新用户基础信息。
     */
    @Update("<script>"
            + "UPDATE sys_user SET nickname = #{nickname}, phone = #{phone}, email = #{email}, "
            + "role = #{role}, status = #{status} "
            + "<if test='password != null and password != \"\"'>, password = #{password}</if> "
            + "WHERE id = #{id}"
            + "</script>")
    int adminUpdate(SysUser user);

    /**
     * 删除用户。
     */
    @Delete("DELETE FROM sys_user WHERE id = #{id}")
    int deleteById(Long id);
}
