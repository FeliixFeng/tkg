package com.wtu.interceptor;

import com.wtu.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    // 修改方法签名以确保正确覆盖超类方法
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 添加日志输出
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Authorization Header: " + request.getHeader("Authorization"));

        String token = request.getHeader("Authorization");

        if (token != null) {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7); // 去掉 "Bearer " 前缀
            }
            try {
                Claims claims = jwtUtils.validateToken(token);
                System.out.println("Token validation successful. User: " + claims.getSubject());
                // 将用户信息存入请求属性，供后续使用
                request.setAttribute("username", claims.getSubject());
                return true;
            } catch (Exception e) {
                System.out.println("Token validation failed: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid Token");
                return false;
            }
        }

        System.out.println("Authorization header missing or invalid");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Authorization header missing or invalid");
        return false;
    }
}
