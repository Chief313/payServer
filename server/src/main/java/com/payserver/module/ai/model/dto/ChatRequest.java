package com.payserver.module.ai.model.dto;

import lombok.Data;

/**
 * AI 聊天请求 DTO
 */
@Data
public class ChatRequest {

    /** 会话ID，为空时自动创建新会话 */
    private Long sessionId;
    /** 用户消息内容 */
    private String message;
}
