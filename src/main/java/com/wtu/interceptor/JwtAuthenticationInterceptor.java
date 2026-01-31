package com.wtu.interceptor;

import com.wtu.constant.MessageConstant;
import com.wtu.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        String requestURI = request.getRequestURI();
        log.debug("Request URI: {}", requestURI);

        // 白名单：不需要认证的接口
        if (requestURI.contains("/api/user/login") ||
            requestURI.contains("/api/user/register") ||
            requestURI.contains("/api/user/get_user/")) {
            log.debug("Public API accessed, skipping authentication: {}", requestURI);
            return true;
        }

        log.debug("Authorization Header: {}", request.getHeader("Authorization"));

        String token = request.getHeader("Authorization");

        if (token != null) {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7); // 去掉 "Bearer " 前缀
            }
            try {
                Claims claims = jwtUtils.validateToken(token);
                log.debug("Token validation successful. User: {}", claims.getSubject());
                // 将用户信息存入请求属性，供后续使用
                request.setAttribute("username", claims.getSubject());
                return true;
            } catch (Exception e) {
                log.warn("Token validation failed: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write(MessageConstant.TOKEN_INVALID);
                return false;
            }
        }

        log.warn("Authorization header missing or invalid");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(MessageConstant.TOKEN_MISSING);
        return false;
    }
}
