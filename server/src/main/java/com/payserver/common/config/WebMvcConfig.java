package com.payserver.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web MVC 配置类
 * 配置跨域访问与 picserver 静态图片资源映射
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final Path picUploadDir;

    public WebMvcConfig(@Value("${pic.upload-dir:../picserver}") String picUploadDir) {
        this.picUploadDir = Paths.get(picUploadDir).toAbsolutePath().normalize();
    }

    /**
     * 配置跨域规则，允许前端开发环境访问
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    /**
     * 将 /pic/files/** 映射到 picserver 本地目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = picUploadDir.toUri().toString();
        registry.addResourceHandler("/pic/files/**")
                .addResourceLocations(location.endsWith("/") ? location : location + "/");
    }
}
