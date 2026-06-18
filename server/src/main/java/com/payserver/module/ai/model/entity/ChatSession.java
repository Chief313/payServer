package com.payserver.module.ai.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 客服会话实体
 * 对应数据库表 chat_session
 */
@Data
public class ChatSession {

    /** 会话ID */
    private Long id;
    /** 用户ID，匿名会话为空 */
    private Long userId;
    /** 会话标题 */
    private String title;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
