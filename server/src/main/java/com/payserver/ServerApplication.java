package com.payserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(excludeName = "org.springframework.ai.vectorstore.redis.autoconfigure.RedisVectorStoreAutoConfiguration")
@MapperScan("com.payserver.module.**.mapper")
@EnableScheduling
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}
