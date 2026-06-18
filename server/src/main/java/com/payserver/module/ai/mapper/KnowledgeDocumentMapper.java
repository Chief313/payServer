package com.payserver.module.ai.mapper;

import com.payserver.module.ai.model.entity.KnowledgeDocument;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * AI 知识库文档 MyBatis Mapper
 */
@Mapper
public interface KnowledgeDocumentMapper {

    /**
     * 插入知识库文档记录
     *
     * @param document 文档实体
     * @return 影响行数
     */
    @Insert("INSERT INTO knowledge_document (title, file_name, file_type, file_path, file_size, chunk_count, vector_status) "
            + "VALUES (#{title}, #{fileName}, #{fileType}, COALESCE(#{filePath}, ''), #{fileSize}, #{chunkCount}, #{vectorStatus})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(KnowledgeDocument document);

    /**
     * 根据 ID 查询文档
     *
     * @param id 文档ID
     * @return 文档实体
     */
    @Select("SELECT id, title, file_name, file_type, file_path, file_size, chunk_count, vector_status, create_time, update_time "
            + "FROM knowledge_document WHERE id = #{id}")
    KnowledgeDocument findById(Long id);

    /**
     * 查询全部知识库文档
     *
     * @return 文档列表
     */
    @Select("SELECT id, title, file_name, file_type, file_path, file_size, chunk_count, vector_status, create_time, update_time "
            + "FROM knowledge_document ORDER BY create_time DESC")
    List<KnowledgeDocument> findAll();

    /**
     * 更新文档本地路径
     *
     * @param document 文档实体
     * @return 影响行数
     */
    @Update("UPDATE knowledge_document SET file_path = #{filePath}, update_time = NOW() WHERE id = #{id}")
    int updateFilePath(KnowledgeDocument document);

    /**
     * 更新文档向量化状态与分块数量
     *
     * @param document 文档实体
     * @return 影响行数
     */
    @Update("UPDATE knowledge_document SET chunk_count = #{chunkCount}, vector_status = #{vectorStatus}, update_time = NOW() WHERE id = #{id}")
    int updateVectorStatus(KnowledgeDocument document);

    /**
     * 删除文档记录
     *
     * @param id 文档ID
     * @return 影响行数
     */
    @Delete("DELETE FROM knowledge_document WHERE id = #{id}")
    int deleteById(Long id);
}
