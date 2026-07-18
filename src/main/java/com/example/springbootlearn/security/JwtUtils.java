package com.example.springbootlearn.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类 —— 负责生成、解析、验证 Token
 */
@Component
public class JwtUtils {

    // 密钥（生产环境应该放到配置文件里，而且至少要256位）
    private static final String SECRET = "my-super-secret-key-for-jwt-signing-please-change-in-production";
    // Token 有效期：24 小时（毫秒）
    private static final long EXPIRATION = 24 * 60 * 60 * 1000;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 Token
     *
     * @param userId 用户 ID，存到 Token 的载荷里
     * @param username 用户名
     * @return JWT 字符串
     */
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION);

        return Jwts.builder()
                .subject(username)                    // 主题存用户名
                .claim("userId", userId)              // 自定义字段存用户 ID
                .issuedAt(now)                        // 签发时间
                .expiration(expiry)                   // 过期时间
                .signWith(getKey())                   // 签名
                .compact();
    }

    /**
     * 从 Token 中解析出用户名
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 从 Token 中解析出用户 ID
     */
    public Long getUserIdFromToken(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    /**
     * 验证 Token 是否有效
     *
     * @return true=有效，false=过期/伪造/格式错误
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Token 过期、签名不对、格式不对，统统视为无效
            return false;
        }
    }

    /**
     * 解析 Token 的载荷（私有方法，上面的方法都调用它）
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
