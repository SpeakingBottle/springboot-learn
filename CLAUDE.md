# CLAUDE.md — 学生请假审核系统

## 项目概述

这是一个 **Spring Boot 全家桶教学项目**，通过构建"学生请假审核系统"来学习后端开发。

**业务**：学生提交请假申请 → 老师初审 → 学院领导复核（两级审批、三种角色）

## 学员背景

- Java 语法基础（变量、循环、类、接口），无正式项目经验
- 有前端经验（Vue3），理解 HTTP 协议
- 后端技术栈从零学起

## 技术栈

| 组件 | 技术 | 版本 |
|------|------|------|
| 框架 | Spring Boot | 3.4.0 |
| JDK | Java | 21 |
| 数据库 | MySQL | 8.0+ |
| ORM | MyBatis-Plus | 3.5+ |
| 安全 | Spring Security + JWT | 6.x |
| 缓存 | Redis | 7.x |
| 文档 | SpringDoc OpenAPI | 2.x |
| 构建 | Maven | 3.9+ |
| API 调试 | Apifox | - |

## 项目结构

```
src/main/java/com/example/springbootlearn/
├── SpringbootLearnApplication.java   ← 启动类
├── common/          ← 公共类（统一返回体、异常处理）
├── config/          ← 配置类（Security、Redis、MyBatis-Plus）
├── entity/          ← 数据库实体类
├── dto/             ← 请求体对象
├── vo/              ← 返回体对象
├── mapper/          ← MyBatis-Plus Mapper 接口
├── service/         ← 业务逻辑接口
│   └── impl/        ← 业务逻辑实现
├── security/        ← JWT 工具、过滤器
└── controller/      ← REST 接口
```

## 教学方式（每课四步）

1. **我讲**（5-10 分钟）— 概念 + 代码示例，逐行解释
2. **你做**（15-20 分钟）— 类似但不同的练习任务，有明确验收标准
3. **我检查**（5 分钟）— 读取代码，指出正确/优化/Bug
4. **跑起来验证** — 启动项目，用 Apifox 验证

## 课程进度

| 课次 | 标题 | 状态 |
|------|------|------|
| 1 | Hello Spring Boot | ✅ 完成 |
| 2 | 数据库落地 | ✅ 完成 |
| 3 | 第一个 CRUD | ✅ 完成 |
| 4 | 规范与容错 | ✅ 完成 |
| 5 | 登录与认证 | ✅ 完成 |
| 6 | 角色权限 | ✅ 完成 |
| 7 | 审核流程 | ✅ 完成 |
| 8 | Redis 缓存 | ✅ 完成 |
| 9 | API 文档 | ✅ 完成 |

## 环境配置

- MySQL：root / root
- Redis：本地安装
- IDE：不限（IntelliJ IDEA 或 VSCode + Java 插件）
- API 调试：Apifox

## 设计文档

完整系统设计见：`docs/superpowers/specs/2026-07-15-leave-approval-system-design.md`

## 关键约定

- 所有代码注释用中文
- 统一返回格式：`{code, msg, data}`
- 接口路径以 `/api/` 开头（最终版本）
- 数据库表名用单数下划线：`user`、`leave_request`
