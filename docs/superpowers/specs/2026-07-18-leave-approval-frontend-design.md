# 学生请假审核系统 — 前端设计规格

> 创建日期：2026-07-18
> 项目类型：前端 SPA（与现有 Spring Boot 后端对接）
> 学员背景：Vue3 前端经验，后端学习完毕，准备学习部署

---

## 一、系统概述

为已有的 Spring Boot 请假审核系统搭建前端管理页面，实现完整的前后端交互。

**三种角色、两级审批：**
- **学生** — 提交请假申请、查看自己的申请记录
- **老师** — 初审（通过/驳回）、查看待审核列表
- **学院领导** — 复核（通过/驳回）、查看待审核列表、管理用户

**与后端的关系：**
- 前端通过 HTTP 请求调用后端 REST API
- 所有接口路径以 `/api/` 开头
- 统一返回格式 `{ code, msg, data }`
- JWT Token 存储在 localStorage，每次请求通过 `Authorization: Bearer <token>` 携带

---

## 二、技术选型

| 组件 | 技术 | 版本 |
|------|------|------|
| 框架 | Vue 3 | 3.4+ |
| 语言 | TypeScript | 5.x |
| 构建工具 | Vite | 5.x |
| UI 组件库 | Element Plus | 2.x |
| 路由 | Vue Router | 4.x |
| 状态管理 | Pinia | 2.x |
| HTTP 客户端 | Axios | 1.x |
| 包管理 | pnpm | 9.x |

---

## 三、项目结构

```
frontend/
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
├── src/
│   ├── main.ts                  ← 入口：挂载 App、注册 Router/Pinia/Element Plus
│   ├── App.vue                  ← 根组件
│   ├── api/                     ← 封装 axios，按模块拆分
│   │   ├── request.ts           ← axios 实例（baseURL、拦截器、token 注入）
│   │   ├── auth.ts              ← 登录/注册/获取当前用户
│   │   ├── leave.ts             ← 请假 CRUD + 审核
│   │   └── user.ts              ← 用户管理
│   ├── router/
│   │   └── index.ts             ← 路由表 + 导航守卫（未登录 → /login）
│   ├── stores/
│   │   ├── auth.ts              ← 登录态：token、当前用户、角色
│   │   └── app.ts               ← 全局 UI 状态：侧边栏折叠
│   ├── pages/                   ← 页面级组件（XxxPage.vue）
│   │   ├── LoginPage.vue
│   │   ├── DashboardPage.vue
│   │   ├── LeaveCreatePage.vue
│   │   ├── LeaveListPage.vue
│   │   ├── ReviewListPage.vue
│   │   └── UserListPage.vue
│   ├── components/              ← 可复用组件
│   │   ├── AppLayout.vue        ← 布局壳（侧边栏 + 顶栏 + router-view）
│   │   ├── ReviewDialog.vue     ← 审核弹窗
│   │   └── LeaveForm.vue        ← 请假表单（提交页复用）
│   └── types/                   ← TypeScript 类型定义
│       └── index.ts             ← User、LeaveRequest、ApiResponse 等
```

---

## 四、路由设计

| 路径 | 页面 | 所需角色 | 说明 |
|------|------|---------|------|
| `/login` | LoginPage | 游客 | 登录/注册，未登录统一跳转到此 |
| `/dashboard` | DashboardPage | 全部 | 欢迎页 + 统计卡片 + 快捷入口 |
| `/leave/create` | LeaveCreatePage | STUDENT | 提交请假申请表 |
| `/leave/list` | LeaveListPage | 全部 | 请假记录表格，支持状态筛选 |
| `/review/pending` | ReviewListPage | TEACHER/DEAN | 待审核列表 + 审核操作 |
| `/users` | UserListPage | DEAN | 用户 CRUD 管理 |

**导航守卫：**
- 未登录访问任何路由 → 重定向到 `/login`
- 已登录访问 `/login` → 重定向到 `/dashboard`
- 无权限访问受保护路由 → 重定向到 `/dashboard`（不弹 403 页，简化处理）

**侧边栏菜单（按角色动态显示）：**

| 菜单项 | 图标 | 学生 | 老师 | 领导 |
|--------|------|:---:|:---:|:---:|
| 首页 | HomeFilled | ✅ | ✅ | ✅ |
| 提交请假 | Edit | ✅ | - | - |
| 请假记录 | Document | ✅ | ✅ | ✅ |
| 审核列表 | Check | - | ✅ | ✅ |
| 用户管理 | User | - | - | ✅ |

---

## 五、页面详细设计

### 5.1 登录页 `LoginPage.vue`

**路由：** `/login`

**布局：** 全屏居中卡片，左侧品牌色渐变区域（放系统名称/图标），右侧表单

**功能：**
- 登录模式：用户名 + 密码 → 调用 `POST /api/auth/login`
- 注册模式（点击"立即注册"切换）：用户名 + 密码 + 真实姓名 + 角色选择（下拉框：学生/老师/领导）+ 手机号 → 调用 `POST /api/auth/register`
- 登录成功后：存 token + 用户信息到 Pinia auth store → 跳转首页
- 表单校验：用户名必填（3-50 字符）、密码必填（6-100 字符）
- 错误提示：后端返回的错误信息用 `ElMessage.error()` 展示

### 5.2 首页 `DashboardPage.vue`

**路由：** `/dashboard`

**布局：** 顶部欢迎语 + 统计卡片行 + 快捷入口行

**功能：**
- 欢迎区域：显示"欢迎回来，{realName}" + 当前角色标签
- 统计卡片（学生视角）：待审核数 / 已通过数 / 已驳回数（从请假列表 API 拉全部数据后前端统计）
- 统计卡片（老师/领导视角）：待审核数 + 快捷跳转
- 快捷入口：点击卡片跳转到对应功能页

### 5.3 提交请假 `LeaveCreatePage.vue`

**路由：** `/leave/create`（仅 STUDENT）

**功能：**
- `el-form` 表单：请假类型（下拉：事假/病假）、开始时间、结束时间、请假原因（textarea）
- `studentId` 从当前登录用户自动获取，不在表单上显示
- 前端校验：类型必选、时间必填且结束时间 > 开始时间、原因必填
- 提交调用 `POST /api/leave-requests`，成功后 `ElMessage.success()` + 跳转到请假记录页

### 5.4 请假记录 `LeaveListPage.vue`

**路由：** `/leave/list`

**功能：**
- `el-tabs` 状态筛选：全部 / 待审核 / 已通过 / 已驳回（学生视角）
- 老师/领导额外 Tab：待我审核
- `el-table` 列：请假类型、时间范围、原因（截断）、状态（`el-tag` 彩色标签）、操作（详情按钮）
- 点击"详情"弹出 `el-dialog`，展示完整请假信息（含审核意见 + 时间线）
- 分页：使用 `el-pagination`，前端自己处理（后端暂不支持分页参数）
- 状态标签颜色映射：
  - PENDING → 橙色 `warning`
  - TEACHER_APPROVED → 蓝色
  - TEACHER_REJECTED → 红色 `danger`
  - DEAN_APPROVED → 绿色 `success`
  - DEAN_REJECTED → 红色 `danger`

### 5.5 审核列表 `ReviewListPage.vue`

**路由：** `/review/pending`（TEACHER/DEAN）

**功能：**
- 调用 `GET /api/leave-requests/pending` 获取待审核列表
- `el-table` 列：学生姓名、类型、起止时间、原因、提交时间、操作（审核按钮）
- 点击"审核"弹出 `ReviewDialog.vue`：
  - 展示被审核请假完整信息
  - 审核意见输入框（必填）
  - 通过按钮 → `PUT /api/leave-requests/{id}/review-first`（老师）或 `/review-second`（领导）
  - 驳回按钮 → 同上接口，审核意见传递区分通过/驳回意图
  - 操作后关闭弹窗、刷新列表、提示成功
- 注意：后端 `reviewFirst` / `reviewSecond` 的 `ReviewDto` 只有 `comment` 字段，通过/驳回由 `comment` 内容区分（需确认后端逻辑，如果后端通过 `comment` 关键字判断，前端需约定规则；如果后端仅记录意见，可能需调整接口）

**与后端协调：** 当前后端 `reviewFirst` / `reviewSecond` 方法接收 `comment` 字符串，但无法区分"通过"还是"驳回"。有两种处理方式：
1. 后端新增 `approved` 字段到 `ReviewDto`（推荐）
2. 前端约定 comment 前缀（如 `[通过]` / `[驳回]`）

本设计推荐方式 1，实现时优先与后端对齐。

### 5.6 用户管理 `UserListPage.vue`

**路由：** `/users`（仅 DEAN）

**功能：**
- 搜索栏：搜索用户名（前端过滤，后端暂不支持搜索参数）
- `el-table` 列：用户名、姓名、角色（`el-tag`）、手机号、创建时间、操作（编辑/删除）
- 新增/编辑：`el-dialog` 内嵌 `el-form`（用户名、密码、姓名、角色选择、手机号）
  - 新增 → `POST /api/users`
  - 编辑 → `PUT /api/users/{id}`
- 删除：`ElMessageBox.confirm()` 二次确认 → `DELETE /api/users/{id}`
- 分页：前端处理

---

## 六、状态管理

### auth store（Pinia）

```typescript
interface AuthState {
  token: string | null          // JWT Token，持久化到 localStorage
  user: User | null             // 当前用户信息
  isLoggedIn: boolean           // 计算属性：!!token
  role: string | null           // 计算属性：user?.role
}
```

**关键 Action：**
- `login(username, password)` — 调用 API → 存 token/user
- `register(...)` — 调用 API 注册
- `logout()` — 清除 token/user → 跳转登录页
- `fetchCurrentUser()` — 调用 `GET /api/auth/me` 刷新用户信息

### app store（Pinia）

```typescript
interface AppState {
  sidebarCollapsed: boolean     // 侧边栏折叠状态（响应式）
}
```

---

## 七、API 层设计

### `request.ts` — axios 实例

- `baseURL`: `http://localhost:8080`（开发环境，Vite 代理后可改为 `/api`）
- 请求拦截器：从 Pinia auth store 读取 token，加到 `Authorization: Bearer <token>` 请求头
- 响应拦截器：`code !== 200` 时 `ElMessage.error(msg)`；`code === 401` 时清除 token 跳转登录页

### 按模块拆分

| 模块 | 函数 | 方法 | 路径 |
|------|------|------|------|
| `auth.ts` | `login(data)` | POST | `/api/auth/login` |
| `auth.ts` | `register(data)` | POST | `/api/auth/register` |
| `auth.ts` | `getMe()` | GET | `/api/auth/me` |
| `leave.ts` | `getAll()` | GET | `/api/leave-requests` |
| `leave.ts` | `getById(id)` | GET | `/api/leave-requests/{id}` |
| `leave.ts` | `create(data)` | POST | `/api/leave-requests` |
| `leave.ts` | `reviewFirst(id, data)` | PUT | `/api/leave-requests/{id}/review-first` |
| `leave.ts` | `reviewSecond(id, data)` | PUT | `/api/leave-requests/{id}/review-second` |
| `leave.ts` | `getPending()` | GET | `/api/leave-requests/pending` |
| `user.ts` | `getAll()` | GET | `/api/users` |
| `user.ts` | `create(data)` | POST | `/api/users` |
| `user.ts` | `update(id, data)` | PUT | `/api/users/{id}` |
| `user.ts` | `remove(id)` | DELETE | `/api/users/{id}` |

---

## 八、TypeScript 类型定义

```typescript
// 后端统一返回体
interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

// 用户
interface User {
  id: number
  username: string
  password?: string        // 仅在创建/编辑时使用，查询时不应返回
  realName: string
  role: 'STUDENT' | 'TEACHER' | 'DEAN'
  phone: string
  createTime: string
  updateTime: string
}

// 请假申请
interface LeaveRequest {
  id: number
  studentId: number
  type: string             // SICK / PERSONAL
  startTime: string
  endTime: string
  reason: string
  status: LeaveStatus
  teacherId: number | null
  teacherComment: string | null
  teacherTime: string | null
  deanId: number | null
  deanComment: string | null
  deanTime: string | null
  createTime: string
  updateTime: string
}

type LeaveStatus = 'PENDING' | 'TEACHER_APPROVED' | 'TEACHER_REJECTED'
                 | 'DEAN_APPROVED' | 'DEAN_REJECTED'

// 请求体
interface LoginData { username: string; password: string }
interface RegisterData { username: string; password: string; realName: string; role: string; phone?: string }
interface LeaveFormData { studentId: number; type: string; startTime: string; endTime: string; reason: string }
interface ReviewData { comment: string; approved: boolean }  // approved 需与后端对齐
interface UserFormData { username: string; password?: string; realName: string; role: string; phone?: string }
```

---

## 九、Vite 配置要点

```typescript
// vite.config.ts
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // 后端地址
        changeOrigin: true,
      }
    }
  }
})
```

- 开发时 Vite 代理 `/api` 到后端，避免跨域问题
- 生产部署时打包成静态文件，由 Nginx 或 Spring Boot 托管

---

## 十、后端需补充的配置

### CORS 配置（如不使用 Vite 代理）

在 `SecurityConfig.java` 中添加：

```java
.cors(cors -> cors.configurationSource(request -> {
    var config = new org.springframework.web.cors.CorsConfiguration();
    config.addAllowedOrigin("http://localhost:5173");
    config.addAllowedMethod("*");
    config.addAllowedHeader("*");
    config.setAllowCredentials(true);
    return config;
}))
```

如果使用 Vite 代理则不需要，但生产部署时可能需要。

### ReviewDto 增强（推荐）

当前 `ReviewDto` 只有 `comment` 字段，无法区分"通过"和"驳回"：

```java
// 建议改为
@Data
public class ReviewDto {
    @NotBlank(message = "审核意见不能为空")
    private String comment;

    @NotNull(message = "审核结果不能为空")
    private Boolean approved;  // true=通过, false=驳回
}
```

---

## 十一、实现顺序

1. **项目初始化** — Vite + Vue3 + TS + Element Plus 脚手架搭建
2. **基础设施** — `request.ts` axios 封装、types 定义、router、Pinia stores
3. **AppLayout** — 布局壳 + 侧边栏 + 路由出口
4. **LoginPage** — 登录/注册
5. **DashboardPage** — 首页
6. **LeaveCreatePage** — 提交请假
7. **LeaveListPage** — 请假记录
8. **ReviewListPage** — 审核列表 + ReviewDialog
9. **UserListPage** — 用户管理
