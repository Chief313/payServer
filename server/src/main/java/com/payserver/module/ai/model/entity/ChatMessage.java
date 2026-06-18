package com.payserver.module.ai.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 客服消息实体
 * 对应数据库表 chat_message
 */
@Data
public class ChatMessage {

    /** 消息ID */
    private Long id;
    /** 所属会话ID */
    private Long sessionId;
    /** 角色：user / assistant / system */
    private String role;
    /** 消息内容 */
    private String content;
    /** 创建时间 */
    private LocalDateTime createTime;
}
