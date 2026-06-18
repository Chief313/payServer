package com.payserver.module.ai.mapper;

import com.payserver.module.ai.model.entity.ChatMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI 客服消息 MyBatis Mapper
 */
@Mapper
public interface ChatMessageMapper {

    /**
     * 插入消息记录
     *
     * @param message 消息实体
     * @return 影响行数
     */
    @Insert("INSERT INTO chat_message (session_id, role, content) VALUES (#{sessionId}, #{role}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChatMessage message);

    /**
     * 查询会话下的历史消息
     *
     * @param sessionId 会话ID
     * @return 消息列表（按时间升序）
     */
    @Select("SELECT id, session_id, role, content, create_time FROM chat_message WHERE session_id = #{sessionId} ORDER BY create_time ASC")
    List<ChatMessage> findBySessionId(Long sessionId);
}
