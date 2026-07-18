package com.example.springbootlearn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springbootlearn.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper —— 继承 BaseMapper 后，自动获得以下方法：
 *   selectById(id)         根据 ID 查询
 *   selectList(wrapper)    条件查询列表
 *   selectPage(page, wrapper)  分页查询
 *   insert(entity)         新增
 *   updateById(entity)     根据 ID 更新
 *   deleteById(id)         根据 ID 删除
 *
 * 全都不用写 SQL，MyBatis-Plus 自动生成！
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 如果内置方法不够用，可以在这里定义自定义方法
    // 比如：根据用户名查询
    // User selectByUsername(@Param("username") String username);
}
