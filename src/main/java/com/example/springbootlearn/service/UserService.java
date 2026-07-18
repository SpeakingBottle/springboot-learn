package com.example.springbootlearn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springbootlearn.entity.User;

/**
 * 用户 Service 接口
 *
 * 继承 IService<User> 后，自动获得更高级的 CRUD 方法：
 *   save(entity)           新增（单条/批量）
 *   getById(id)            根据 ID 查询
 *   list(wrapper)          条件查询列表
 *   page(page, wrapper)    分页查询
 *   updateById(entity)     根据 ID 更新
 *   removeById(id)         根据 ID 删除
 *
 * Mapper 定位是"操作数据库"，Service 定位是"处理业务逻辑"
 * 比如：注册用户时，要校验用户名是否重复、加密密码，这些写在 Service 里
 */
public interface UserService extends IService<User> {
    // 自定义业务方法在这里声明
}
