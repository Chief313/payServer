package com.payserver.module.ai.service;

import com.payserver.module.ai.model.dto.ChatResponse;
import com.payserver.module.ai.model.entity.ChatSession;

import java.util.List;

/**
 * AI 客服聊天服务接口
 */
public interface ChatService {

    /**
     * 发送聊天消息并获取 AI 回复（含 RAG 检索）
     *
     * @param sessionId 会话ID，可为空
     * @param message   用户消息
     * @return 聊天响应
     */
    ChatResponse chat(Long sessionId, String message);

    /**
     * 查询当前用户的会话列表
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<ChatSession> listSessions(Long userId);
}
