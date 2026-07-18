package com.example.springbootlearn.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springbootlearn.common.Result;
import com.example.springbootlearn.dto.CreateUserDto;
import com.example.springbootlearn.dto.LoginDto;
import com.example.springbootlearn.entity.User;
import com.example.springbootlearn.mapper.UserMapper;
import com.example.springbootlearn.security.JwtUtils;
import com.example.springbootlearn.security.SecurityUtils;
import com.example.springbootlearn.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证接口 —— 登录 + 注册
 *
 * /api/auth/** 在 SecurityConfig 里配置了 permitAll()，无需登录即可访问
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
        private UserMapper userMapper;

    /**
     * 登录
     *
     * 流程：
     *   1. 接收 username + password
     *   2. 交给 AuthenticationManager 校验
     *   3. 校验通过 → 生成 JWT Token 返回
     *   4. 校验失败 → 抛异常，GlobalExceptionHandler 捕获后返回 500
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDto dto) {
        // 这一步会调用 UserDetailsServiceImpl.loadUserByUsername() 加载用户，
        // 然后用 PasswordEncoder.matches() 比对密码
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        // 认证通过，查出用户信息，生成 Token
        User user = userService.lambdaQuery()
                .eq(User::getUsername, dto.getUsername())
                .one();

        String token = jwtUtils.generateToken(user.getId(), user.getUsername());

        // 组装返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole());

        return Result.success(data);
    }

    /**
     * 注册
     *
     * 注意：注册时要把密码用 BCrypt 加密再存数据库！
     * 明文存密码是绝对不能做的事
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody CreateUserDto dto) {
        // 1. 检查用户名是否已被占用
        long count = userService.lambdaQuery()
                .eq(User::getUsername, dto.getUsername())
                .count();
        if (count > 0) {
            return Result.error(400, "用户名已被占用");
        }

        // 2. DTO → Entity，密码 BCrypt 加密
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));  // 覆盖掉明文密码

        // 3. 保存到数据库
        userService.save(user);
        return Result.success();
    }

    // 获取当前用户
    @GetMapping("/me")
    public Result<User> me(){
        String username = SecurityUtils.getCurrentUsername();   //  获取当前用户名
        // 从数据库查询用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
        return Result.success(user);
    }
}
