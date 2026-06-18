package com.payserver.common.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.RedisClient;

/**
 * Explicit Redis vector store configuration for AI knowledge retrieval.
 */
@Configuration
public class AiVectorStoreConfig {

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(RedisClient.class)
    public RedisClient vectorStoreRedisClient(@Value("${spring.data.redis.host:localhost}") String host,
                                              @Value("${spring.data.redis.port:6379}") int port) {
        return RedisClient.builder()
                .hostAndPort(host, port)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(VectorStore.class)
    public VectorStore vectorStore(RedisClient redisClient,
                                   EmbeddingModel embeddingModel,
                                   @Value("${spring.ai.vectorstore.redis.index-name:payserver-knowledge}") String indexName,
                                   @Value("${spring.ai.vectorstore.redis.prefix:ai:doc:}") String prefix,
                                   @Value("${spring.ai.vectorstore.redis.initialize-schema:true}") boolean initializeSchema) {
        return RedisVectorStore.builder(redisClient, embeddingModel)
                .indexName(indexName)
                .prefix(prefix)
                .initializeSchema(initializeSchema)
                .build();
    }
}
