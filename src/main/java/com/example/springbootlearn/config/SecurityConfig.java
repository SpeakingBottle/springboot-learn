package com.example.springbootlearn.config;

import com.example.springbootlearn.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置类 —— 整个安全机制的控制中心
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    /**
     * 密码编码器 —— BCrypt 是目前最常用的单向加密算法
     *
     * "123456" 加密后长这样：$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
     * 不可逆，只能用 matches() 方法比对
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager —— 负责执行认证逻辑
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 过滤器链 —— Spring Security 的核心配置
     *
     * 这里决定了：
     *   - 哪些接口不需要登录就能访问
     *   - 哪些接口必须登录
     *   - 用什么样的认证方式（JWT / Session / ...）
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. 关闭 CSRF（RESTful API 用 Token 认证，不需要 CSRF 防护）
            .csrf(csrf -> csrf.disable())

            // 2. 无状态会话（不用 Session，每次请求都带 Token）
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 3. 配置访问规则
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()   // 登录/注册接口，允许匿名访问
                .requestMatchers("/docs.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // API 文档
                .anyRequest().authenticated()                   // 其他所有接口都要登录
            )

            // 4. 把我们的 JWT 过滤器加到 Spring Security 过滤器链里，
            //    放在 UsernamePasswordAuthenticationFilter 前面
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
