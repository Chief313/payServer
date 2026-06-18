package com.payserver.module.product.mapper;

import com.payserver.module.product.model.entity.Product;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商品 MyBatis Mapper
 */
@Mapper
public interface ProductMapper {

    /**
     * 根据ID查询商品
     *
     * @param id 商品ID
     * @return 商品实体
     */
    @Select("SELECT id, category_id, name, description, cover_url, price, status, create_time, update_time "
            + "FROM product WHERE id = #{id}")
    Product findById(Long id);

    /**
     * 分页查询上架商品（用户端）
     *
     * @param categoryId 分类ID，可为空
     * @param offset     偏移量
     * @param limit      每页条数
     * @return 商品列表
     */
    @Select("<script>"
            + "SELECT id, category_id, name, description, cover_url, price, status, create_time, update_time "
            + "FROM product WHERE status = 1 "
            + "<if test='categoryId != null'> AND (category_id = #{categoryId} OR category_id IN (SELECT id FROM category WHERE parent_id = #{categoryId})) </if>"
            + "ORDER BY id DESC LIMIT #{offset}, #{limit}"
            + "</script>")
    List<Product> findOnSaleList(@Param("categoryId") Long categoryId,
                                 @Param("offset") int offset,
                                 @Param("limit") int limit);

    /**
     * 统计上架商品数量（用户端）
     *
     * @param categoryId 分类ID，可为空
     * @return 总记录数
     */
    @Select("<script>"
            + "SELECT COUNT(*) FROM product WHERE status = 1 "
            + "<if test='categoryId != null'> AND (category_id = #{categoryId} OR category_id IN (SELECT id FROM category WHERE parent_id = #{categoryId})) </if>"
            + "</script>")
    long countOnSale(@Param("categoryId") Long categoryId);

    /**
     * 分页查询全部商品（管理端）
     *
     * @param categoryId 分类ID，可为空
     * @param offset     偏移量
     * @param limit      每页条数
     * @return 商品列表
     */
    @Select("<script>"
            + "SELECT id, category_id, name, description, cover_url, price, status, create_time, update_time "
            + "FROM product WHERE 1 = 1 "
            + "<if test='categoryId != null'> AND (category_id = #{categoryId} OR category_id IN (SELECT id FROM category WHERE parent_id = #{categoryId})) </if>"
            + "ORDER BY id DESC LIMIT #{offset}, #{limit}"
            + "</script>")
    List<Product> findAdminList(@Param("categoryId") Long categoryId,
                                @Param("offset") int offset,
                                @Param("limit") int limit);

    /**
     * 统计全部商品数量（管理端）
     *
     * @param categoryId 分类ID，可为空
     * @return 总记录数
     */
    @Select("<script>"
            + "SELECT COUNT(*) FROM product WHERE 1 = 1 "
            + "<if test='categoryId != null'> AND (category_id = #{categoryId} OR category_id IN (SELECT id FROM category WHERE parent_id = #{categoryId})) </if>"
            + "</script>")
    long countAdmin(@Param("categoryId") Long categoryId);

    /**
     * 新增商品
     *
     * @param product 商品实体
     * @return 影响行数
     */
    @Insert("INSERT INTO product (category_id, name, description, cover_url, price, status) "
            + "VALUES (#{categoryId}, #{name}, #{description}, #{coverUrl}, #{price}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    /**
     * 更新商品
     *
     * @param product 商品实体
     * @return 影响行数
     */
    @Update("UPDATE product SET category_id = #{categoryId}, name = #{name}, description = #{description}, "
            + "cover_url = #{coverUrl}, price = #{price}, status = #{status} WHERE id = #{id}")
    int update(Product product);

    /**
     * 删除商品
     *
     * @param id 商品ID
     * @return 影响行数
     */
    @Delete("DELETE FROM product WHERE id = #{id}")
    int deleteById(Long id);
}
