package com.payserver.module.product.mapper;

import com.payserver.module.product.model.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商品分类 MyBatis Mapper
 */
@Mapper
public interface CategoryMapper {

    /**
     * 查询全部分类
     *
     * @return 分类列表
     */
    @Select("SELECT id, name, parent_id, sort, status, create_time FROM category ORDER BY sort ASC, id ASC")
    List<Category> findAll();

    /**
     * 根据ID查询分类
     *
     * @param id 分类ID
     * @return 分类实体
     */
    @Select("SELECT id, name, parent_id, sort, status, create_time FROM category WHERE id = #{id}")
    Category findById(Long id);

    /**
     * 新增分类
     *
     * @param category 分类实体
     * @return 影响行数
     */
    @Insert("INSERT INTO category (name, parent_id, sort, status) VALUES (#{name}, #{parentId}, #{sort}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);

    /**
     * 更新分类
     *
     * @param category 分类实体
     * @return 影响行数
     */
    @Update("UPDATE category SET name = #{name}, parent_id = #{parentId}, sort = #{sort}, status = #{status} WHERE id = #{id}")
    int update(Category category);

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 影响行数
     */
    @Delete("DELETE FROM category WHERE id = #{id}")
    int deleteById(Long id);
}
