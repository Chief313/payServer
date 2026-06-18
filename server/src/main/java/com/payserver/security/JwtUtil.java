package com.payserver.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 * 负责 Token 的生成与解析
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expireMillis;

    /**
     * 初始化 JWT 配置
     *
     * @param secret      JWT 密钥
     * @param expireHours 过期小时数
     */
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expire-hours}") int expireHours) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireMillis = expireHours * 3600_000L;
    }

    /**
     * 生成 JWT Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param role     角色
     * @return Token 字符串
     */
    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + expireMillis);
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expire)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 解析 JWT Token
     *
     * @param token Token 字符串
     * @return 载荷信息
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
