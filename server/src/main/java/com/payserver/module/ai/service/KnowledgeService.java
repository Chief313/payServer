package com.payserver.module.ai.service;

import com.payserver.module.ai.model.entity.KnowledgeDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * AI 知识库服务接口
 * 负责文档上传、解析、分块与向量存储
 */
public interface KnowledgeService {

    /**
     * 上传并解析知识库文档
     *
     * @param file  上传文件
     * @param title 文档标题
     * @return 保存后的文档实体
     */
    KnowledgeDocument upload(MultipartFile file, String title);

    /**
     * 查询全部知识库文档
     *
     * @return 文档列表
     */
    List<KnowledgeDocument> list();

    /**
     * 删除知识库文档及其向量数据
     *
     * @param id 文档ID
     */
    void delete(Long id);
}
