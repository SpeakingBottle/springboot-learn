# 学生请假审核系统 — 教学设计与系统规格

> 创建日期：2026-07-15
> 项目类型：教学项目（Spring Boot 全家桶实战教学）
> 学员背景：Java 语法基础 + 前端经验，后端零项目经验

---

## 一、系统概述

一个经典的三角色两级审批工作流系统，用于教学后端技术栈：

- **学生** 提交请假申请
- **老师** 初审（通过/驳回）
- **学院领导** 复核（通过/驳回）

状态流转：

```
PENDING ──老师通过──→ TEACHER_APPROVED ──领导通过──→ DEAN_APPROVED
   │                      │                          │
   └──老师驳回──→ TEACHER_REJECTED      └──领导驳回──→ DEAN_REJECTED
```

## 二、技术栈

| 组件 | 技术 | 版本 |
|------|------|------|
| 框架 | Spring Boot | 3.x |
| 数据库 | MySQL | 8.0+ |
| ORM | MyBatis-Plus | 3.5+ |
| 安全 | Spring Security + JWT | 6.x + jjwt |
| 缓存 | Redis | 7.x |
| 文档 | SpringDoc OpenAPI | 2.x |
| 构建 | Maven | 3.9+ |
| JDK | Java | 21 |

## 三、数据库设计

### 用户表 `user`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| username | VARCHAR(50) | 登录账号，唯一 |
| password | VARCHAR(255) | BCrypt 加密后的密码 |
| real_name | VARCHAR(50) | 真实姓名 |
| role | VARCHAR(20) | STUDENT / TEACHER / DEAN |
| phone | VARCHAR(20) | 手机号 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 请假申请表 `leave_request`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| student_id | BIGINT | 外键，关联 user.id |
| type | VARCHAR(20) | SICK（病假）/ PERSONAL（事假） |
| start_time | DATETIME | 开始时间 |
| end_time | DATETIME | 结束时间 |
| reason | TEXT | 请假原因 |
| status | VARCHAR(30) | PENDING → TEACHER_APPROVED / TEACHER_REJECTED → DEAN_APPROVED / DEAN_REJECTED |
| teacher_id | BIGINT | 审核老师 ID，可空 |
| teacher_comment | VARCHAR(500) | 老师审核意见 |
| teacher_time | DATETIME | 老师审核时间 |
| dean_id | BIGINT | 复核领导 ID，可空 |
| dean_comment | VARCHAR(500) | 领导复核意见 |
| dean_time | DATETIME | 领导复核时间 |
| create_time | DATETIME | 提交时间 |
| update_time | DATETIME | 最后更新时间 |

## 四、项目目录结构

```
src/main/java/com/example/springbootlearn/
├── SpringbootLearnApplication.java
├── common/
│   ├── Result.java              ← 统一返回体
│   └── GlobalExceptionHandler.java ← 全局异常处理
├── config/
│   ├── SecurityConfig.java      ← Spring Security 配置
│   ├── RedisConfig.java         ← Redis 配置
│   └── MyBatisPlusConfig.java   ← MP 分页插件等
├── entity/
│   ├── User.java
│   └── LeaveRequest.java
├── dto/
│   ├── LoginDto.java            ← 登录请求体
│   ├── LeaveRequestDto.java     ← 提交请假请求体
│   └── ReviewDto.java           ← 审核请求体
├── vo/
│   ├── UserVo.java              ← 用户信息返回（不含密码）
│   └── LeaveRequestVo.java      ← 请假申请详情返回
├── mapper/
│   ├── UserMapper.java
│   └── LeaveRequestMapper.java
├── service/
│   ├── UserService.java
│   ├── LeaveRequestService.java
│   ├── impl/
│   │   ├── UserServiceImpl.java
│   │   └── LeaveRequestServiceImpl.java
├── security/
│   ├── JwtUtils.java            ← JWT 生成/解析工具
│   ├── JwtAuthFilter.java       ← JWT 拦截过滤器
│   ├── UserDetailsServiceImpl.java ← 自定义用户加载
│   └── SecurityUtils.java       ← 获取当前登录用户
└── controller/
    ├── AuthController.java       ← 登录/注册
    ├── UserController.java       ← 用户管理
    └── LeaveRequestController.java ← 请假申请/审核
```

## 五、课程大纲

| 课次 | 标题 | 核心知识点 | 课后可见成果 |
|------|------|-----------|-------------|
| 1 | Hello Spring Boot | Maven、启动类、配置文件、@RestController | 浏览器看到 Hello World |
| 2 | 数据库落地 | MySQL 建库建表、连接配置、数据源 | 项目成功连接 MySQL |
| 3 | 第一个 CRUD | MyBatis-Plus、Entity/Mapper/Service | Postman 增删改查用户表 |
| 4 | 规范与容错 | RESTful 设计、统一返回体、全局异常处理 | 所有接口返回格式统一 |
| 5 | 登录与认证 | Spring Security + JWT 生成与验证 | 登录拿到 token，无 token 被 401 |
| 6 | 角色权限 | RBAC 权限模型、@PreAuthorize | 不同角色访问不同接口 |
| 7 | 审核流程 | Service 业务编排、事务、状态机 | 完整请假→审核→复核流程 |
| 8 | Redis 缓存 | Spring Cache、Redis 序列化 | 高频数据查询加速 |
| 9 | API 文档 | SpringDoc + Knife4j | Swagger 页面操作所有接口 |

## 六、教学方式

每节课四步走：

1. **我讲**（5-10 分钟）— 概念 + 代码示例，逐行解释
2. **你做**（15-20 分钟）— 类似但不同的练习任务，有明确验收标准
3. **我检查**（5 分钟）— 读代码，指出正确/优化/Bug
4. **跑起来** — 启动项目，Postman / 浏览器验证

## 七、API 接口规划（最终状态）

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| POST | /api/auth/login | 无需登录 | 登录，返回 JWT |
| POST | /api/auth/register | 无需登录 | 注册 |
| GET | /api/user/me | 登录即可 | 获取当前用户信息 |
| GET | /api/leave-requests | 登录即可 | 查看自己的申请列表 |
| POST | /api/leave-requests | STUDENT | 提交请假申请 |
| PUT | /api/leave-requests/{id}/review-first | TEACHER | 老师初审 |
| PUT | /api/leave-requests/{id}/review-second | DEAN | 领导复核 |
| GET | /api/leave-requests/pending | TEACHER/DEAN | 查看待审核列表 |

## 八、环境要求

- JDK 21
- MySQL 8.0+（root / root）
- Redis 7.x
- Maven 3.9+
- IDE：IntelliJ IDEA 或 VSCode + Java 插件
- Apifox（API 调试）
