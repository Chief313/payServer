package com.payserver.security;

import com.payserver.common.exception.BizException;
import com.payserver.common.result.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Set;

/**
 * JWT 认证拦截器
 * 解析 Token 并校验角色权限
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    /** 无需登录即可访问的路径前缀 */
    private static final Set<String> WHITE_LIST = Set.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/user/products",
            "/api/v1/user/categories",
            "/api/v1/ai/chat",
            "/api/v1/user/payment/alipay/notify"
    );

    public JwtInterceptor(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    /**
     * 请求前置处理：解析 JWT 并校验角色
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String uri = request.getRequestURI();
        if (isWhiteListed(uri)) {
            return true;
        }
        String token = extractToken(request);
        if (token == null) {
            writeUnauthorized(response, "未登录，请先登录");
            return false;
        }
        try {
            Claims claims = jwtUtil.parseToken(token);
            UserContext context = new UserContext();
            context.setUserId(claims.get("userId", Long.class));
            context.setUsername(claims.getSubject());
            context.setRole(claims.get("role", String.class));
            UserContext.set(context);

            if (handler instanceof HandlerMethod handlerMethod) {
                RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
                if (requireRole != null && requireRole.value().length > 0) {
                    boolean allowed = Arrays.asList(requireRole.value()).contains(context.getRole());
                    if (!allowed) {
                        writeForbidden(response, "无权限访问");
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            writeUnauthorized(response, "登录已过期，请重新登录");
            return false;
        }
    }

    /**
     * 请求完成后清理 ThreadLocal
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    /**
     * 判断路径是否在白名单中
     */
    private boolean isWhiteListed(String uri) {
        return WHITE_LIST.stream().anyMatch(uri::startsWith);
    }

    /**
     * 从请求头提取 Bearer Token
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(401, message)));
    }

    private void writeForbidden(HttpServletResponse response, String message) throws Exception {
        response.setStatus(403);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(403, message)));
    }
}
