package com.payserver.module.ai.controller;

import com.payserver.common.result.Result;
import com.payserver.module.ai.model.dto.ChatRequest;
import com.payserver.module.ai.model.dto.ChatResponse;
import com.payserver.module.ai.model.entity.ChatSession;
import com.payserver.module.ai.service.ChatService;
import com.payserver.security.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AI 客服聊天接口
 */
@RestController
@RequestMapping("/api/v1/ai")
public class AiChatController {

    private final ChatService chatService;

    public AiChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 发送聊天消息
     *
     * @param request 聊天请求
     * @return AI 回复
     */
    @PostMapping("/chat")
    public Result<ChatResponse> chat(@RequestBody ChatRequest request) {
        ChatResponse response = chatService.chat(request.getSessionId(), request.getMessage());
        return Result.ok(response);
    }

    /**
     * 查询当前用户的会话列表
     *
     * @return 会话列表
     */
    @GetMapping("/sessions")
    public Result<List<ChatSession>> sessions() {
        UserContext userContext = UserContext.get();
        Long userId = userContext != null ? userContext.getUserId() : null;
        List<ChatSession> sessions = chatService.listSessions(userId);
        return Result.ok(sessions);
    }
}
