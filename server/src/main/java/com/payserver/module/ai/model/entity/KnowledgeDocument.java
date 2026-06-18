package com.payserver.module.ai.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 知识库文档实体
 * 对应数据库表 knowledge_document
 */
@Data
public class KnowledgeDocument {

    /** 文档ID */
    private Long id;
    /** 文档标题 */
    private String title;
    /** 原始文件名 */
    private String fileName;
    /** 文件类型：txt/doc/docx/pdf/md/markdown */
    private String fileType;
    /** 本地存储路径 */
    private String filePath;
    /** 文件大小（字节） */
    private Long fileSize;
    /** 向量分块数量 */
    private Integer chunkCount;
    /** 向量化状态：PENDING / DONE / FAILED */
    private String vectorStatus;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
