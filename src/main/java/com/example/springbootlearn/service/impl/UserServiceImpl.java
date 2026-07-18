package com.example.springbootlearn.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springbootlearn.entity.User;
import com.example.springbootlearn.mapper.UserMapper;
import com.example.springbootlearn.service.UserService;

import java.io.Serializable;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 用户 Service 实现类
 *
 * 继承 ServiceImpl<UserMapper, User> 就拥有了 IService 的全部默认实现
 * 自定义业务逻辑写在这个类里
 */
@Service  // 告诉 Spring：这是一个 Service 组件，请管理它的生命周期
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    // 自定义业务方法在这里实现

    @Override
    @Cacheable(value = "user",key = "#id")
    public User getById(Serializable id){
        // 第一次调用 → 执行这个方法 → 返回值自动存 Redis
        // 之后调用   → 跳过方法体 → 直接从 Redis 拿
        return super.getById(id);
    }

    //@CacheEvict — 数据变更时清除缓存
    @Override
    @CacheEvict(value = "user",key = "#entity.id")  // 用 entity 的 id 属性做 key
    public boolean updateById(User entity){
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(value = "user",key = "#id")
    public boolean removeById(Serializable id){
        return super.removeById(id);
    }
}
