package com.example.springbootlearn.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类 —— 和数据库的 user 表一一对应
 *
 * @Data 是 Lombok 注解，自动生成 getter、setter、toString 等方法
 *       省得你手写上百行样板代码
 */
@Data
@TableName("user")  // 指定对应的数据库表名
public class User {

    @TableId(type = IdType.AUTO)  // 主键，自增
    private Long id;

    private String username;      // 登录账号

    private String password;      // 加密后的密码

    private String realName;      // 真实姓名（驼峰命名 → 自动映射数据库下划线字段 real_name）

    private String role;          // 角色：STUDENT / TEACHER / DEAN

    private String phone;         // 手机号

    private LocalDateTime createTime;         // 创建时间（数据库自动填充）

    private LocalDateTime updateTime;         // 更新时间（数据库自动填充）
}
