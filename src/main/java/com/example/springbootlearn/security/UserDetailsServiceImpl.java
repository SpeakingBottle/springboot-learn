package com.example.springbootlearn.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springbootlearn.entity.User;
import com.example.springbootlearn.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;

/**
 * 自定义用户加载服务
 *
 * Spring Security 不认识你数据库里的 User 实体，
 * 这个类的作用就是把数据库的用户转成 Spring Security 能理解的 UserDetails
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库查询用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在：" + username);
        }

        // 把数据库的 role 字段转成 Spring Security 的 GrantedAuthority
        // 数据库里是 "STUDENT"，要转成 "ROLE_STUDENT"
        var authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole())
        );

        // 把我们的 User 对象转成 Spring Security 的 UserDetails
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),                // 用户名
                user.getPassword(),                // 密码（应该是 BCrypt 加密的）
                authorities                        // 权限列表
        );
    }
}
