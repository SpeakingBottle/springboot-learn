package com.example.springbootlearn.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器 —— 在每次请求到达 Controller 之前执行
 *
 * OncePerRequestFilter 保证每个请求只执行一次（即使请求被转发）
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 从请求头获取 Token
        String token = extractToken(request);

        // 2. 如果有 Token 且 Token 有效
        if (token != null && jwtUtils.validateToken(token)) {
            // 3. 从 Token 中取出用户名
            String username = jwtUtils.getUsernameFromToken(token);

            // 4. 从数据库加载用户信息
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 5. 把用户信息存入 Spring Security 上下文
            //    这样后续的 Controller 就能通过 SecurityContextHolder 获取当前用户
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 6. 放行，继续下一个过滤器
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头 "Authorization: Bearer xxx" 中提取 Token
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);  // 截掉 "Bearer " 前缀
        }
        return null;
    }
}
