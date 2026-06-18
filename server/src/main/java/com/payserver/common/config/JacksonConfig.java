package com.payserver.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson 配置类
 * 注册 ObjectMapper Bean，供 JSON 序列化/反序列化使用
 */
@Configuration
public class JacksonConfig {

    /**
     * 创建全局 ObjectMapper 实例
     *
     * @return ObjectMapper Bean
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
