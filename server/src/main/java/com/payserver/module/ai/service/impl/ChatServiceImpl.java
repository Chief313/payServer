package com.payserver.module.ai.service.impl;


import org.springframework.ai.vectorstore.VectorStore;
import com.payserver.common.exception.BizException;
import com.payserver.module.ai.mapper.ChatMessageMapper;
import com.payserver.module.ai.mapper.ChatSessionMapper;
import com.payserver.module.ai.model.dto.ChatResponse;
import com.payserver.module.ai.model.entity.ChatMessage;
import com.payserver.module.ai.model.entity.ChatSession;
import com.payserver.module.ai.service.ChatService;
import com.payserver.security.UserContext;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 客服聊天服务实现
 * 基于 RAG 检索增强与 ChatModel 生成回复
 */
@Service
public class ChatServiceImpl implements ChatService {

    private static final int RAG_TOP_K = 5;

    private final ChatModel chatModel;
    private final VectorStore vectorStore;
    private final ChatSessionMapper chatSessionMapper;
    private final ChatMessageMapper chatMessageMapper;

    public ChatServiceImpl(ChatModel chatModel,
                           VectorStore vectorStore,
                           ChatSessionMapper chatSessionMapper,
                           ChatMessageMapper chatMessageMapper) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
        this.chatSessionMapper = chatSessionMapper;
        this.chatMessageMapper = chatMessageMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChatResponse chat(Long sessionId, String message) {
        if (message == null || message.isBlank()) {
            throw new BizException("消息内容不能为空");
        }

        ChatSession session = resolveSession(sessionId, message);
        saveMessage(session.getId(), "user", message);

        List<Document> retrieved = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(message)
                        .topK(RAG_TOP_K)
                        .build()
        );

        String context = retrieved.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));


        List<Message> promptMessages = buildPromptMessages(session.getId(), context, message);
        String reply = chatModel.call(new Prompt(promptMessages))
                .getResult()
                .getOutput()
                .getText();

        saveMessage(session.getId(), "assistant", reply);

        ChatResponse response = new ChatResponse();
        response.setSessionId(session.getId());
        response.setReply(reply);
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChatSession> listSessions(Long userId) {
        if (userId == null) {
            throw new BizException("请先登录后查看会话列表");
        }
        return chatSessionMapper.findByUserId(userId);
    }

    /**
     * 获取或创建会话
     */
    private ChatSession resolveSession(Long sessionId, String message) {
        if (sessionId != null) {
            ChatSession existing = chatSessionMapper.findById(sessionId);
            if (existing == null) {
                throw new BizException("会话不存在");
            }
            return existing;
        }

        ChatSession session = new ChatSession();
        UserContext userContext = UserContext.get();
        session.setUserId(userContext != null ? userContext.getUserId() : null);
        session.setTitle(truncateTitle(message));
        chatSessionMapper.insert(session);
        return session;
    }

    /**
     * 构建包含 RAG 上下文与历史消息的 Prompt
     */
    private List<Message> buildPromptMessages(Long sessionId, String context, String userMessage) {
        List<Message> messages = new ArrayList<>();

        String systemPrompt = """
                你是电商平台的智能客服助手，请基于以下知识库内容回答用户问题。
                若知识库中没有相关信息，请礼貌说明并给出通用建议。
                回答应简洁、准确、友好。

                【知识库参考内容】
                %s
                """.formatted(context.isBlank() ? "（暂无相关知识）" : context);
        messages.add(new SystemMessage(systemPrompt));

        List<ChatMessage> history = chatMessageMapper.findBySessionId(sessionId);
        for (ChatMessage item : history) {
            if ("user".equals(item.getRole())) {
                messages.add(new UserMessage(item.getContent()));
            } else if ("assistant".equals(item.getRole())) {
                messages.add(new AssistantMessage(item.getContent()));
            }
        }

        return messages;
    }

    /**
     * 保存消息到数据库
     */
    private void saveMessage(Long sessionId, String role, String content) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSessionId(sessionId);
        chatMessage.setRole(role);
        chatMessage.setContent(content);
        chatMessageMapper.insert(chatMessage);
    }

    /**
     * 截取首条消息作为会话标题
     */
    private String truncateTitle(String message) {
        String trimmed = message.trim();
        return trimmed.length() <= 30 ? trimmed : trimmed.substring(0, 30) + "...";
    }
}
