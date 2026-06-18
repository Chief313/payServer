package com.payserver.module.ai.model.dto;

import lombok.Data;

/**
 * AI 聊天响应 DTO
 */
@Data
public class ChatResponse {

    /** 会话ID */
    private Long sessionId;
    /** AI 回复内容 */
    private String reply;
}
