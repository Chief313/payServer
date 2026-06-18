package com.payserver.module.ai.mapper;

import com.payserver.module.ai.model.entity.ChatSession;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * AI 客服会话 MyBatis Mapper
 */
@Mapper
public interface ChatSessionMapper {

    /**
     * 插入会话记录
     *
     * @param session 会话实体
     * @return 影响行数
     */
    @Insert("INSERT INTO chat_session (user_id, title) VALUES (#{userId}, #{title})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChatSession session);

    /**
     * 根据 ID 查询会话
     *
     * @param id 会话ID
     * @return 会话实体
     */
    @Select("SELECT id, user_id, title, create_time, update_time FROM chat_session WHERE id = #{id}")
    ChatSession findById(Long id);

    /**
     * 查询指定用户的会话列表
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    @Select("SELECT id, user_id, title, create_time, update_time FROM chat_session WHERE user_id = #{userId} ORDER BY update_time DESC")
    List<ChatSession> findByUserId(Long userId);

    /**
     * 更新会话标题与更新时间
     *
     * @param session 会话实体
     * @return 影响行数
     */
    @Update("UPDATE chat_session SET title = #{title}, update_time = NOW() WHERE id = #{id}")
    int updateTitle(ChatSession session);
}
