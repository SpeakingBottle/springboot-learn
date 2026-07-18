# 学生请假审核系统前端 — 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为 Spring Boot 请假审核系统搭建 Vue3+TS+Element Plus 前端 SPA，实现登录注册、请假提交、两级审核、用户管理等完整功能。

**Architecture:** 标准 Vue3 SPA 架构，Vite 代理后端 API，Pinia 管理登录态，Vue Router 控制页面跳转 + 导航守卫，Element Plus 提供 UI 组件，Axios 封装 HTTP 请求。所有 API 请求通过 `/api` 前缀代理到 `localhost:8080`。

**Tech Stack:** Vue 3.4+ / TypeScript 5.x / Vite 5.x / Element Plus 2.x / Vue Router 4.x / Pinia 2.x / Axios 1.x

## Global Constraints

- 所有代码注释用中文
- 页面组件放在 `pages/` 目录，命名为 `XxxPage.vue`
- 可复用组件放在 `components/` 目录
- 后端审核逻辑：comment 含"驳回"或"不同意"→ 驳回，否则通过（无需修改后端 ReviewDto）
- 统一返回格式 `{code, msg, data}`，code=200 为成功
- 接口路径以 `/api/` 开头，Vite 开发服务器代理到 `http://localhost:8080`
- JWT Token 存储在 localStorage，key 为 `token`
- Element Plus 使用中文语言包

## 文件结构总览

```
frontend/
├── index.html                          # HTML 入口
├── package.json                        # 依赖配置
├── tsconfig.json                       # TS 配置
├── tsconfig.node.json                  # TS Node 配置
├── vite.config.ts                      # Vite 配置（含代理）
├── env.d.ts                            # 环境变量类型声明
└── src/
    ├── main.ts                         # 应用入口
    ├── App.vue                         # 根组件
    ├── api/
    │   ├── request.ts                  # axios 实例 + 拦截器
    │   ├── auth.ts                     # 认证 API
    │   ├── leave.ts                    # 请假 API
    │   └── user.ts                     # 用户 API
    ├── router/
    │   └── index.ts                    # 路由表 + 导航守卫
    ├── stores/
    │   ├── auth.ts                     # 登录态
    │   └── app.ts                      # UI 状态
    ├── types/
    │   └── index.ts                    # 全部 TS 类型
    ├── pages/
    │   ├── LoginPage.vue               # 登录/注册
    │   ├── DashboardPage.vue           # 首页
    │   ├── LeaveCreatePage.vue         # 提交请假
    │   ├── LeaveListPage.vue           # 请假记录
    │   ├── ReviewListPage.vue          # 审核列表
    │   └── UserListPage.vue            # 用户管理
    └── components/
        ├── AppLayout.vue               # 布局壳
        └── ReviewDialog.vue            # 审核弹窗
```

---

### Task 1: 项目脚手架

**Files:**
- Create: `frontend/package.json`
- Create: `frontend/index.html`
- Create: `frontend/vite.config.ts`
- Create: `frontend/tsconfig.json`
- Create: `frontend/tsconfig.node.json`
- Create: `frontend/env.d.ts`
- Create: `frontend/src/main.ts`（骨架）
- Create: `frontend/src/App.vue`（骨架）

**Produces:** 可启动的空白 Vite + Vue3 + TS + Element Plus 项目

---

- [ ] **Step 1: 创建前端目录并初始化 package.json**

```bash
mkdir -p frontend/src/{api,router,stores,types,pages,components}
```

创建 `frontend/package.json`：

```json
{
  "name": "leave-approval-frontend",
  "private": true,
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vue-tsc && vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.4.0",
    "vue-router": "^4.3.0",
    "pinia": "^2.1.0",
    "axios": "^1.7.0",
    "element-plus": "^2.7.0",
    "@element-plus/icons-vue": "^2.3.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "typescript": "^5.4.0",
    "vite": "^5.4.0",
    "vue-tsc": "^2.0.0",
    "@types/node": "^20.0.0"
  }
}
```

- [ ] **Step 2: 安装依赖**

```bash
cd frontend && pnpm install
```

Expected: 无报错，`node_modules/` 生成。

- [ ] **Step 3: 创建 index.html**

创建 `frontend/index.html`：

```html
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>学生请假审核系统</title>
  </head>
  <body>
    <div id="app"></div>
    <script type="module" src="/src/main.ts"></script>
  </body>
</html>
```

- [ ] **Step 4: 创建 Vite 配置**

创建 `frontend/vite.config.ts`：

```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
```

- [ ] **Step 5: 创建 TypeScript 配置**

创建 `frontend/tsconfig.json`：

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "module": "ESNext",
    "moduleResolution": "bundler",
    "strict": true,
    "jsx": "preserve",
    "resolveJsonModule": true,
    "isolatedModules": true,
    "esModuleInterop": true,
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "skipLibCheck": true,
    "noEmit": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    }
  },
  "include": ["src/**/*.ts", "src/**/*.d.ts", "src/**/*.vue", "env.d.ts"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

创建 `frontend/tsconfig.node.json`：

```json
{
  "compilerOptions": {
    "composite": true,
    "skipLibCheck": true,
    "module": "ESNext",
    "moduleResolution": "bundler",
    "allowSyntheticDefaultImports": true
  },
  "include": ["vite.config.ts"]
}
```

创建 `frontend/env.d.ts`：

```typescript
/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}
```

- [ ] **Step 6: 创建骨架 main.ts 和 App.vue**

创建 `frontend/src/main.ts`（骨架，后续任务补充）：

```typescript
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import App from './App.vue'

const app = createApp(App)
app.use(ElementPlus, { locale: zhCn })
app.mount('#app')
```

创建 `frontend/src/App.vue`（骨架）：

```vue
<template>
  <div>学生请假审核系统 — 加载中...</div>
</template>

<script setup lang="ts">
</script>
```

- [ ] **Step 7: 验证项目可启动**

```bash
cd frontend && pnpm dev
```

Expected: Vite 启动成功，浏览器打开 `http://localhost:5173` 显示"学生请假审核系统 — 加载中..."

- [ ] **Step 8: 提交**

```bash
cd frontend && git init && git add -A && git commit -m "feat: 初始化 Vue3 + TS + Element Plus 项目脚手架"
```

> 注意：如果根目录 `.gitignore` 未包含 `node_modules/` 和 `dist/`，需要补充。

---

### Task 2: TypeScript 类型定义

**Files:**
- Create: `frontend/src/types/index.ts`

**Produces:** 全部 TS 类型/接口，供后续 API 层、Store 层、页面层使用

---

- [ ] **Step 1: 创建类型定义文件**

创建 `frontend/src/types/index.ts`：

```typescript
// ========== 后端统一返回体 ==========
export interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

// ========== 用户 ==========
export interface User {
  id: number
  username: string
  password?: string
  realName: string
  role: 'STUDENT' | 'TEACHER' | 'DEAN'
  phone: string
  createTime: string
  updateTime: string
}

// ========== 请假状态 ==========
export type LeaveStatus =
  | 'PENDING'
  | 'TEACHER_APPROVED'
  | 'TEACHER_REJECTED'
  | 'DEAN_APPROVED'
  | 'DEAN_REJECTED'

// ========== 状态标签配置 ==========
export const LEAVE_STATUS_MAP: Record<LeaveStatus, { text: string; type: '' | 'success' | 'warning' | 'info' | 'danger' }> = {
  PENDING: { text: '待审核', type: 'warning' },
  TEACHER_APPROVED: { text: '老师已通过', type: 'info' },
  TEACHER_REJECTED: { text: '老师已驳回', type: 'danger' },
  DEAN_APPROVED: { text: '已通过', type: 'success' },
  DEAN_REJECTED: { text: '已驳回', type: 'danger' },
}

// ========== 角色名称映射 ==========
export const ROLE_MAP: Record<string, string> = {
  STUDENT: '学生',
  TEACHER: '老师',
  DEAN: '学院领导',
}

// ========== 请假申请 ==========
export interface LeaveRequest {
  id: number
  studentId: number
  type: string
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

// ========== 请求体 ==========
export interface LoginData {
  username: string
  password: string
}

export interface RegisterData {
  username: string
  password: string
  realName: string
  role: string
  phone?: string
}

export interface LeaveFormData {
  studentId: number
  type: string
  startTime: string
  endTime: string
  reason: string
}

export interface ReviewData {
  comment: string
}

export interface UserFormData {
  username: string
  password?: string
  realName: string
  role: string
  phone?: string
}

// ========== 登录响应 ==========
export interface LoginResult {
  token: string
  userId: number
  username: string
  role: string
}
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/types/index.ts
git commit -m "feat: 添加 TypeScript 类型定义"
```

---

### Task 3: Axios 请求封装

**Files:**
- Create: `frontend/src/api/request.ts`

**Produces:** 带拦截器的 axios 实例，自动注入 token、统一错误处理

---

- [ ] **Step 1: 创建 request.ts**

创建 `frontend/src/api/request.ts`：

```typescript
import axios from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResponse } from '@/types'

// 创建 axios 实例，baseURL 为空（Vite 代理处理 /api 前缀）
const request = axios.create({
  baseURL: '',
  timeout: 10000,
})

// 请求拦截器 —— 自动注入 JWT Token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 —— 统一错误处理
request.interceptors.response.use(
  (response) => {
    const res = response.data as ApiResponse<unknown>
    // code=200 正常返回
    if (res.code === 200) {
      return response
    }
    // 401 未登录 → 清除 token 并跳转登录页
    if (res.code === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
      return Promise.reject(new Error(res.msg || '未登录'))
    }
    // 其他错误 → 弹提示
    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  (error) => {
    // HTTP 错误（网络异常、500 等）
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
      return Promise.reject(error)
    }
    ElMessage.error(error.message || '网络异常')
    return Promise.reject(error)
  }
)

export default request
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/api/request.ts
git commit -m "feat: 封装 axios 请求实例（token 注入 + 统一错误处理）"
```

---

### Task 4: API 模块

**Files:**
- Create: `frontend/src/api/auth.ts`
- Create: `frontend/src/api/leave.ts`
- Create: `frontend/src/api/user.ts`

**Consumes:** `request.ts` 导出的 axios 实例, `types/index.ts` 类型

**Produces:** 三个 API 模块，每个函数返回 Promise

---

- [ ] **Step 1: 创建 auth.ts**

创建 `frontend/src/api/auth.ts`：

```typescript
import request from './request'
import type { ApiResponse, LoginData, LoginResult, RegisterData, User } from '@/types'

/** 登录 */
export function login(data: LoginData) {
  return request.post<ApiResponse<LoginResult>>('/api/auth/login', data)
}

/** 注册 */
export function register(data: RegisterData) {
  return request.post<ApiResponse<null>>('/api/auth/register', data)
}

/** 获取当前用户信息 */
export function getMe() {
  return request.get<ApiResponse<User>>('/api/auth/me')
}
```

- [ ] **Step 2: 创建 leave.ts**

创建 `frontend/src/api/leave.ts`：

```typescript
import request from './request'
import type { ApiResponse, LeaveRequest, LeaveFormData, ReviewData } from '@/types'

/** 获取所有请假单 */
export function getAllLeaves() {
  return request.get<ApiResponse<LeaveRequest[]>>('/api/leave-requests')
}

/** 根据 ID 获取请假单 */
export function getLeaveById(id: number) {
  return request.get<ApiResponse<LeaveRequest>>(`/api/leave-requests/${id}`)
}

/** 学生提交请假申请 */
export function createLeave(data: LeaveFormData) {
  return request.post<ApiResponse<null>>('/api/leave-requests', data)
}

/** 老师初审 */
export function reviewFirst(id: number, data: ReviewData) {
  return request.put<ApiResponse<null>>(`/api/leave-requests/${id}/review-first`, data)
}

/** 领导复审 */
export function reviewSecond(id: number, data: ReviewData) {
  return request.put<ApiResponse<null>>(`/api/leave-requests/${id}/review-second`, data)
}

/** 获取待审核列表 */
export function getPendingLeaves() {
  return request.get<ApiResponse<LeaveRequest[]>>('/api/leave-requests/pending')
}
```

- [ ] **Step 3: 创建 user.ts**

创建 `frontend/src/api/user.ts`：

```typescript
import request from './request'
import type { ApiResponse, User, UserFormData } from '@/types'

/** 获取所有用户 */
export function getAllUsers() {
  return request.get<ApiResponse<User[]>>('/api/users')
}

/** 新增用户 */
export function createUser(data: UserFormData) {
  return request.post<ApiResponse<null>>('/api/users', data)
}

/** 更新用户 */
export function updateUser(id: number, data: Partial<User>) {
  return request.put<ApiResponse<null>>(`/api/users/${id}`, data)
}

/** 删除用户 */
export function deleteUser(id: number) {
  return request.delete<ApiResponse<null>>(`/api/users/${id}`)
}
```

- [ ] **Step 4: 提交**

```bash
git add frontend/src/api/auth.ts frontend/src/api/leave.ts frontend/src/api/user.ts
git commit -m "feat: 添加 API 模块（auth / leave / user）"
```

---

### Task 5: Pinia 状态管理

**Files:**
- Create: `frontend/src/stores/auth.ts`
- Create: `frontend/src/stores/app.ts`

**Consumes:** API 模块, types

**Produces:** 两个 Pinia store，供页面和路由守卫使用

---

- [ ] **Step 1: 创建 auth store**

创建 `frontend/src/stores/auth.ts`：

```typescript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, LoginData, RegisterData } from '@/types'
import { login as loginApi, register as registerApi, getMe } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  // ========== 状态 ==========
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<User | null>(null)

  // ========== 计算属性 ==========
  const isLoggedIn = computed(() => !!token.value)
  const role = computed(() => user.value?.role ?? null)
  const realName = computed(() => user.value?.realName ?? '')

  // ========== 方法 ==========

  /** 登录 */
  async function login(data: LoginData) {
    const res = await loginApi(data)
    const result = res.data.data
    token.value = result.token
    localStorage.setItem('token', result.token)
    // 登录后立即获取完整用户信息
    await fetchUser()
  }

  /** 注册 */
  async function register(data: RegisterData) {
    await registerApi(data)
  }

  /** 获取当前用户信息 */
  async function fetchUser() {
    if (!token.value) return
    try {
      const res = await getMe()
      user.value = res.data.data
    } catch {
      // token 失效，清除登录态
      logout()
    }
  }

  /** 退出登录 */
  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
  }

  return { token, user, isLoggedIn, role, realName, login, register, fetchUser, logout }
})
```

- [ ] **Step 2: 创建 app store**

创建 `frontend/src/stores/app.ts`：

```typescript
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 侧边栏折叠状态
  const sidebarCollapsed = ref(false)

  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  return { sidebarCollapsed, toggleSidebar }
})
```

- [ ] **Step 3: 提交**

```bash
git add frontend/src/stores/auth.ts frontend/src/stores/app.ts
git commit -m "feat: 添加 Pinia 状态管理（auth + app store）"
```

---

### Task 6: Vue Router 路由配置

**Files:**
- Create: `frontend/src/router/index.ts`

**Consumes:** auth store, 所有页面组件（先创建占位符）

**Produces:** 路由表 + 导航守卫

---

- [ ] **Step 1: 创建路由配置**

创建 `frontend/src/router/index.ts`：

```typescript
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/pages/LoginPage.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: () => import('@/pages/DashboardPage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/leave/create',
      name: 'LeaveCreate',
      component: () => import('@/pages/LeaveCreatePage.vue'),
      meta: { requiresAuth: true, roles: ['STUDENT'] },
    },
    {
      path: '/leave/list',
      name: 'LeaveList',
      component: () => import('@/pages/LeaveListPage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/review/pending',
      name: 'ReviewList',
      component: () => import('@/pages/ReviewListPage.vue'),
      meta: { requiresAuth: true, roles: ['TEACHER', 'DEAN'] },
    },
    {
      path: '/users',
      name: 'UserList',
      component: () => import('@/pages/UserListPage.vue'),
      meta: { requiresAuth: true, roles: ['DEAN'] },
    },
    {
      path: '/',
      redirect: '/dashboard',
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/dashboard',
    },
  ],
})

// 导航守卫
router.beforeEach(async (to, _from, next) => {
  const authStore = useAuthStore()

  // 已登录访问登录页 → 跳转首页
  if (to.path === '/login' && authStore.isLoggedIn) {
    return next('/dashboard')
  }

  // 需要登录的页面
  if (to.meta.requiresAuth) {
    if (!authStore.isLoggedIn) {
      return next('/login')
    }
    // 如果已登录但用户信息未加载，先加载
    if (!authStore.user) {
      await authStore.fetchUser()
    }
    // 角色权限检查
    if (to.meta.roles && Array.isArray(to.meta.roles)) {
      const roles = to.meta.roles as string[]
      if (authStore.role && !roles.includes(authStore.role)) {
        return next('/dashboard')
      }
    }
  }

  next()
})

export default router
```

> 注意：需要扩展 Vue Router 的 `RouteMeta` 类型。在 `env.d.ts` 或单独的类型声明文件中添加：

在 `frontend/src/types/index.ts` 末尾追加：

```typescript
// 扩展 Vue Router 的 RouteMeta
declare module 'vue-router' {
  interface RouteMeta {
    requiresAuth?: boolean
    roles?: string[]
  }
}
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/router/index.ts
git commit -m "feat: 添加 Vue Router 路由配置 + 导航守卫"
```

---

### Task 7: AppLayout 布局壳

**Files:**
- Create: `frontend/src/components/AppLayout.vue`

**Consumes:** router（侧边栏导航）, auth store（角色、用户名）, app store（折叠状态）

**Produces:** 经典后台布局壳——左侧菜单 + 顶部栏 + 右侧内容区

---

- [ ] **Step 1: 创建 AppLayout.vue**

创建 `frontend/src/components/AppLayout.vue`：

```vue
<template>
  <el-container class="app-layout">
    <!-- 侧边栏 -->
    <el-aside :width="sidebarCollapsed ? '64px' : '220px'" class="app-aside">
      <div class="app-logo" @click="goDashboard">
        <span v-if="!sidebarCollapsed" class="app-logo-text">请假审核系统</span>
        <span v-else class="app-logo-icon">🎓</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="sidebarCollapsed"
        :collapse-transition="false"
        router
        class="app-menu"
      >
        <!-- 首页 — 所有角色可见 -->
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <!-- 提交请假 — 仅学生 -->
        <el-menu-item v-if="authStore.role === 'STUDENT'" index="/leave/create">
          <el-icon><Edit /></el-icon>
          <span>提交请假</span>
        </el-menu-item>

        <!-- 请假记录 — 所有角色 -->
        <el-menu-item index="/leave/list">
          <el-icon><Document /></el-icon>
          <span>请假记录</span>
        </el-menu-item>

        <!-- 审核列表 — 老师/领导 -->
        <el-menu-item v-if="isReviewer" index="/review/pending">
          <el-icon><Check /></el-icon>
          <span>审核列表</span>
        </el-menu-item>

        <!-- 用户管理 — 仅领导 -->
        <el-menu-item v-if="authStore.role === 'DEAN'" index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧主区域 -->
    <el-container>
      <!-- 顶部栏 -->
      <el-header class="app-header">
        <div class="app-header-left">
          <el-button
            :icon="sidebarCollapsed ? Expand : Fold"
            text
            @click="appStore.toggleSidebar()"
          />
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="pageTitle">{{ pageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="app-header-right">
          <el-tag size="small" effect="plain" class="role-tag">
            {{ roleText }}
          </el-tag>
          <el-dropdown trigger="click">
            <span class="user-name">
              {{ authStore.realName || authStore.user?.username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  HomeFilled, Edit, Document, Check, User,
  Expand, Fold, ArrowDown, SwitchButton,
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'
import { ROLE_MAP } from '@/types'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const appStore = useAppStore()

const sidebarCollapsed = computed(() => appStore.sidebarCollapsed)
const activeMenu = computed(() => route.path)
const isReviewer = computed(() =>
  authStore.role === 'TEACHER' || authStore.role === 'DEAN'
)
const roleText = computed(() =>
  ROLE_MAP[authStore.role ?? ''] ?? authStore.role
)

// 简单的页面标题映射
const PAGE_TITLES: Record<string, string> = {
  '/dashboard': '首页',
  '/leave/create': '提交请假',
  '/leave/list': '请假记录',
  '/review/pending': '审核列表',
  '/users': '用户管理',
}
const pageTitle = computed(() => PAGE_TITLES[route.path] || '')

function goDashboard() {
  router.push('/dashboard')
}

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.app-layout {
  height: 100vh;
}

.app-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.app-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.app-logo-icon {
  font-size: 24px;
}

.app-menu {
  border-right: none;
  background-color: #304156;
}

.app-menu .el-menu-item {
  color: #bfcbd9;
}

.app-menu .el-menu-item:hover {
  background-color: #263445;
  color: #fff;
}

.app-menu .el-menu-item.is-active {
  background-color: #1d2b3c;
  color: #409eff;
}

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
  height: 60px;
}

.app-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.app-header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.role-tag {
  font-size: 12px;
}

.user-name {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  color: #333;
}

.app-main {
  background-color: #f0f2f5;
  min-height: calc(100vh - 60px);
  padding: 20px;
}
</style>
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/components/AppLayout.vue
git commit -m "feat: 添加 AppLayout 布局壳（侧边栏 + 顶栏 + 内容区）"
```

---

### Task 8: LoginPage 登录/注册页

**Files:**
- Create: `frontend/src/pages/LoginPage.vue`

**Consumes:** auth store（login, register）

**Produces:** 可用的登录/注册页面

---

- [ ] **Step 1: 创建 LoginPage.vue**

创建 `frontend/src/pages/LoginPage.vue`：

```vue
<template>
  <div class="login-page">
    <div class="login-container">
      <!-- 左侧品牌区 -->
      <div class="login-banner">
        <h1 class="banner-title">🎓</h1>
        <h2>学生请假审核系统</h2>
        <p>高效、规范的请假管理平台</p>
      </div>

      <!-- 右侧表单区 -->
      <div class="login-form-wrapper">
        <h3 class="form-title">{{ isRegister ? '创建账号' : '用户登录' }}</h3>

        <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-position="top"
          size="large"
          @submit.prevent="handleSubmit"
        >
          <el-form-item label="用户名" prop="username">
            <el-input v-model="formData.username" placeholder="请输入用户名" />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="formData.password"
              type="password"
              placeholder="请输入密码"
              show-password
            />
          </el-form-item>

          <!-- 注册额外字段 -->
          <template v-if="isRegister">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
            </el-form-item>

            <el-form-item label="角色" prop="role">
              <el-select v-model="formData.role" placeholder="请选择角色" style="width: 100%">
                <el-option label="学生" value="STUDENT" />
                <el-option label="老师" value="TEACHER" />
                <el-option label="学院领导" value="DEAN" />
              </el-select>
            </el-form-item>

            <el-form-item label="手机号" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入手机号（选填）" />
            </el-form-item>
          </template>

          <el-form-item>
            <el-button
              type="primary"
              native-type="submit"
              :loading="loading"
              style="width: 100%"
            >
              {{ isRegister ? '注 册' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="form-switch">
          <span>{{ isRegister ? '已有账号？' : '还没有账号？' }}</span>
          <el-button type="primary" link @click="toggleMode">
            {{ isRegister ? '立即登录' : '立即注册' }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const isRegister = ref(false)

// 表单数据（登录和注册共用）
const formData = reactive({
  username: '',
  password: '',
  realName: '',
  role: 'STUDENT',
  phone: '',
})

// 校验规则
const loginRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不低于 6 位', trigger: 'blur' },
  ],
}

const registerRules: FormRules = {
  ...loginRules,
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
}

const rules = ref<FormRules>(loginRules)

function toggleMode() {
  isRegister.value = !isRegister.value
  rules.value = isRegister.value ? registerRules : loginRules
  formRef.value?.resetFields()
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    if (isRegister.value) {
      await authStore.register({
        username: formData.username,
        password: formData.password,
        realName: formData.realName,
        role: formData.role,
        phone: formData.phone || undefined,
      })
      ElMessage.success('注册成功，请登录')
      toggleMode() // 切换到登录模式
    } else {
      await authStore.login({
        username: formData.username,
        password: formData.password,
      })
      ElMessage.success(`欢迎回来，${authStore.realName}`)
      router.push('/dashboard')
    }
  } catch {
    // 错误已在 request 拦截器中处理，无需额外操作
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-container {
  display: flex;
  width: 800px;
  min-height: 480px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.login-banner {
  flex: 1;
  background: linear-gradient(135deg, #1a237e 0%, #283593 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #fff;
  padding: 40px;
}

.banner-title {
  font-size: 64px;
  margin: 0 0 16px;
}

.login-banner h2 {
  font-size: 22px;
  margin: 0 0 8px;
}

.login-banner p {
  font-size: 14px;
  opacity: 0.8;
}

.login-form-wrapper {
  flex: 1;
  background: #fff;
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-title {
  margin: 0 0 24px;
  font-size: 20px;
  color: #303133;
  text-align: center;
}

.form-switch {
  text-align: center;
  font-size: 14px;
  color: #909399;
}
</style>
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/pages/LoginPage.vue
git commit -m "feat: 添加 LoginPage 登录/注册页面"
```

---

### Task 9: DashboardPage 首页

**Files:**
- Create: `frontend/src/pages/DashboardPage.vue`

**Consumes:** auth store, leave API, type 常量

**Produces:** 按角色展示欢迎卡片 + 统计数字 + 快捷入口的首页

---

- [ ] **Step 1: 创建 DashboardPage.vue**

创建 `frontend/src/pages/DashboardPage.vue`：

```vue
<template>
  <div class="dashboard-page">
    <!-- 欢迎区域 -->
    <el-card class="welcome-card">
      <div class="welcome-content">
        <h2>欢迎回来，{{ authStore.realName }}</h2>
        <p>当前角色：<el-tag size="small" effect="plain">{{ roleText }}</el-tag></p>
      </div>
    </el-card>

    <!-- 统计卡片行 -->
    <el-row :gutter="20" class="stats-row">
      <!-- 学生视角 -->
      <template v-if="authStore.role === 'STUDENT'">
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card stat-pending">
            <div class="stat-number">{{ stats.pending }}</div>
            <div class="stat-label">待审核</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card stat-approved">
            <div class="stat-number">{{ stats.approved }}</div>
            <div class="stat-label">已通过</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card stat-rejected">
            <div class="stat-number">{{ stats.rejected }}</div>
            <div class="stat-label">已驳回</div>
          </el-card>
        </el-col>
      </template>

      <!-- 老师 / 领导视角 -->
      <template v-else>
        <el-col :span="24">
          <el-card shadow="hover" class="stat-card stat-pending">
            <div class="stat-number">{{ pendingCount }}</div>
            <div class="stat-label">待审核申请</div>
          </el-card>
        </el-col>
      </template>
    </el-row>

    <!-- 快捷入口 -->
    <h3 class="section-title">快捷操作</h3>
    <el-row :gutter="20">
      <el-col :span="8" v-if="authStore.role === 'STUDENT'">
        <el-card shadow="hover" class="shortcut-card" @click="$router.push('/leave/create')">
          <el-icon :size="32"><Edit /></el-icon>
          <span>提交请假</span>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="shortcut-card" @click="$router.push('/leave/list')">
          <el-icon :size="32"><Document /></el-icon>
          <span>查看记录</span>
        </el-card>
      </el-col>
      <el-col :span="8" v-if="isReviewer">
        <el-card shadow="hover" class="shortcut-card" @click="$router.push('/review/pending')">
          <el-icon :size="32"><Check /></el-icon>
          <span>待审核列表</span>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Edit, Document, Check } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { getAllLeaves, getPendingLeaves } from '@/api/leave'
import type { LeaveRequest } from '@/types'
import { ROLE_MAP, LEAVE_STATUS_MAP } from '@/types'

const authStore = useAuthStore()
const isReviewer = computed(() =>
  authStore.role === 'TEACHER' || authStore.role === 'DEAN'
)
const roleText = computed(() =>
  ROLE_MAP[authStore.role ?? ''] ?? authStore.role
)

// 学生统计
const stats = ref({ pending: 0, approved: 0, rejected: 0 })
// 老师/领导待审核数
const pendingCount = ref(0)

onMounted(async () => {
  try {
    if (authStore.role === 'STUDENT') {
      const res = await getAllLeaves()
      const data: LeaveRequest[] = res.data.data
      stats.value.pending = data.filter(l => l.status === 'PENDING').length
      stats.value.approved = data.filter(l =>
        l.status === 'DEAN_APPROVED' || l.status === 'TEACHER_APPROVED'
      ).length
      stats.value.rejected = data.filter(l =>
        l.status === 'TEACHER_REJECTED' || l.status === 'DEAN_REJECTED'
      ).length
    } else {
      const res = await getPendingLeaves()
      const data: LeaveRequest[] = res.data.data
      // 按角色过滤：老师看 PENDING，领导看 TEACHER_APPROVED
      if (authStore.role === 'TEACHER') {
        pendingCount.value = data.filter(l => l.status === 'PENDING').length
      } else {
        pendingCount.value = data.filter(l => l.status === 'TEACHER_APPROVED').length
      }
    }
  } catch {
    // 忽略错误
  }
})
</script>

<style scoped>
.dashboard-page {
  max-width: 1000px;
}

.welcome-card {
  margin-bottom: 20px;
}

.welcome-content h2 {
  margin: 0 0 8px;
  font-size: 22px;
}

.stats-row {
  margin-bottom: 30px;
}

.stat-card {
  text-align: center;
  padding: 16px 0;
  cursor: default;
}

.stat-number {
  font-size: 36px;
  font-weight: bold;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.stat-pending .stat-number { color: #e6a23c; }
.stat-approved .stat-number { color: #67c23a; }
.stat-rejected .stat-number { color: #f56c6c; }

.section-title {
  margin: 0 0 16px;
  font-size: 16px;
  color: #303133;
}

.shortcut-card {
  text-align: center;
  cursor: pointer;
  transition: transform 0.2s;
  padding: 24px 0;
}

.shortcut-card:hover {
  transform: translateY(-4px);
}

.shortcut-card .el-icon {
  display: block;
  margin: 0 auto 12px;
  color: #409eff;
}

.shortcut-card span {
  font-size: 15px;
  color: #303133;
}
</style>
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/pages/DashboardPage.vue
git commit -m "feat: 添加 DashboardPage 首页（统计卡片 + 快捷入口）"
```

---

### Task 10: LeaveCreatePage 提交请假

**Files:**
- Create: `frontend/src/pages/LeaveCreatePage.vue`

**Consumes:** auth store（当前用户）, leave API（createLeave）

**Produces:** 请假申请表单页

---

- [ ] **Step 1: 创建 LeaveCreatePage.vue**

创建 `frontend/src/pages/LeaveCreatePage.vue`：

```vue
<template>
  <div class="leave-create-page">
    <el-card>
      <template #header>
        <span>提交请假申请</span>
      </template>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
        style="max-width: 600px"
      >
        <el-form-item label="请假类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择请假类型">
            <el-option label="事假" value="事假" />
            <el-option label="病假" value="病假" />
          </el-select>
        </el-form-item>

        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="formData.startTime"
            type="datetime"
            placeholder="选择开始时间"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>

        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="formData.endTime"
            type="datetime"
            placeholder="选择结束时间"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>

        <el-form-item label="请假原因" prop="reason">
          <el-input
            v-model="formData.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入请假原因"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">
            提 交
          </el-button>
          <el-button @click="handleReset">重 置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { createLeave } from '@/api/leave'

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const formData = reactive({
  type: '',
  startTime: '',
  endTime: '',
  reason: '',
})

// 校验结束时间必须大于开始时间
const validateEndTime = (_rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请选择结束时间'))
  } else if (formData.startTime && value <= formData.startTime) {
    callback(new Error('结束时间必须大于开始时间'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  type: [{ required: true, message: '请选择请假类型', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' },
    { validator: validateEndTime, trigger: 'change' },
  ],
  reason: [{ required: true, message: '请输入请假原因', trigger: 'blur' }],
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await createLeave({
      studentId: authStore.user!.id,  // 自动取当前用户 ID
      type: formData.type,
      startTime: formData.startTime,
      endTime: formData.endTime,
      reason: formData.reason,
    })
    ElMessage.success('请假申请已提交')
    router.push('/leave/list')
  } catch {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}

function handleReset() {
  formRef.value?.resetFields()
}
</script>

<style scoped>
.leave-create-page {
  max-width: 800px;
}
</style>
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/pages/LeaveCreatePage.vue
git commit -m "feat: 添加 LeaveCreatePage 提交请假页面"
```

---

### Task 11: LeaveListPage 请假记录

**Files:**
- Create: `frontend/src/pages/LeaveListPage.vue`

**Consumes:** auth store, leave API, 状态常量

**Produces:** 请假记录表格（Tab 筛选 + 详情弹窗）

---

- [ ] **Step 1: 创建 LeaveListPage.vue**

创建 `frontend/src/pages/LeaveListPage.vue`：

```vue
<template>
  <div class="leave-list-page">
    <el-card>
      <!-- Tab 筛选 -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="ALL" />
        <el-tab-pane label="待审核" name="PENDING" />
        <el-tab-pane label="已通过" name="APPROVED" />
        <el-tab-pane label="已驳回" name="REJECTED" />
      </el-tabs>

      <!-- 数据表格 -->
      <el-table :data="pagedData" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="type" label="类型" width="80" />
        <el-table-column label="时间范围" min-width="200">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }} ~ {{ formatDate(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="原因" min-width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="showDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-if="filteredData.length > pageSize"
        class="pagination"
        background
        layout="prev, pager, next"
        :total="filteredData.length"
        :page-size="pageSize"
        v-model:current-page="currentPage"
      />
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="请假详情" width="520px">
      <template v-if="currentDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="请假类型">{{ currentDetail.type }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusTag(currentDetail.status)" size="small">
              {{ getStatusText(currentDetail.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="开始时间" :span="2">
            {{ formatDateTime(currentDetail.startTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="结束时间" :span="2">
            {{ formatDateTime(currentDetail.endTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="请假原因" :span="2">
            {{ currentDetail.reason }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 审核记录 -->
        <template v-if="currentDetail.teacherId">
          <el-divider />
          <h4>老师初审</h4>
          <p>审核意见：{{ currentDetail.teacherComment }}</p>
          <p>审核时间：{{ formatDateTime(currentDetail.teacherTime) }}</p>
        </template>
        <template v-if="currentDetail.deanId">
          <el-divider />
          <h4>领导复审</h4>
          <p>审核意见：{{ currentDetail.deanComment }}</p>
          <p>审核时间：{{ formatDateTime(currentDetail.deanTime) }}</p>
        </template>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getAllLeaves } from '@/api/leave'
import type { LeaveRequest, LeaveStatus } from '@/types'
import { LEAVE_STATUS_MAP } from '@/types'

const loading = ref(false)
const allData = ref<LeaveRequest[]>([])
const activeTab = ref('ALL')
const currentPage = ref(1)
const pageSize = 12
const detailVisible = ref(false)
const currentDetail = ref<LeaveRequest | null>(null)

// 前端筛选
const filteredData = computed(() => {
  if (activeTab.value === 'ALL') return allData.value
  if (activeTab.value === 'APPROVED') {
    return allData.value.filter(l =>
      l.status === 'TEACHER_APPROVED' || l.status === 'DEAN_APPROVED'
    )
  }
  if (activeTab.value === 'REJECTED') {
    return allData.value.filter(l =>
      l.status === 'TEACHER_REJECTED' || l.status === 'DEAN_REJECTED'
    )
  }
  return allData.value.filter(l => l.status === activeTab.value)
})

const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredData.value.slice(start, start + pageSize)
})

function handleTabChange() {
  currentPage.value = 1
}

function formatDate(dateStr: string | null): string {
  if (!dateStr) return '-'
  return dateStr.slice(0, 10)
}

function formatDateTime(dateStr: string | null): string {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ')
}

function getStatusText(status: LeaveStatus): string {
  return LEAVE_STATUS_MAP[status]?.text ?? status
}

function getStatusTag(status: LeaveStatus): '' | 'success' | 'warning' | 'info' | 'danger' {
  return LEAVE_STATUS_MAP[status]?.type ?? ''
}

function showDetail(row: LeaveRequest) {
  currentDetail.value = row
  detailVisible.value = true
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getAllLeaves()
    allData.value = res.data.data
  } catch {
    // 忽略
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

h4 {
  margin: 8px 0 4px;
  font-size: 15px;
}

p {
  margin: 4px 0;
  font-size: 14px;
  color: #606266;
}
</style>
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/pages/LeaveListPage.vue
git commit -m "feat: 添加 LeaveListPage 请假记录页面"
```

---

### Task 12: ReviewListPage 审核列表 + ReviewDialog

**Files:**
- Create: `frontend/src/pages/ReviewListPage.vue`
- Create: `frontend/src/components/ReviewDialog.vue`

**Consumes:** auth store（角色）, leave API（getPendingLeaves, reviewFirst, reviewSecond）

**Produces:** 待审核列表 + 审核弹窗（通过/驳回）

---

- [ ] **Step 1: 创建 ReviewDialog.vue**

创建 `frontend/src/components/ReviewDialog.vue`：

```vue
<template>
  <el-dialog
    v-model="visible"
    :title="`审核申请 - ${leave?.type ?? ''}`"
    width="520px"
    @close="handleClose"
  >
    <template v-if="leave">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="请假类型">{{ leave.type }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTag(leave.status)" size="small">
            {{ getStatusText(leave.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="开始时间" :span="2">
          {{ formatDateTime(leave.startTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="结束时间" :span="2">
          {{ formatDateTime(leave.endTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="请假原因" :span="2">
          {{ leave.reason }}
        </el-descriptions-item>
        <el-descriptions-item label="提交时间" :span="2">
          {{ formatDateTime(leave.createTime) }}
        </el-descriptions-item>
      </el-descriptions>

      <el-divider />

      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="审核意见" prop="comment">
          <el-input
            v-model="formData.comment"
            type="textarea"
            :rows="3"
            placeholder="请输入审核意见（通过可不填意见，驳回必填）"
          />
        </el-form-item>
      </el-form>
    </template>

    <template #footer>
      <el-button @click="visible = false">取 消</el-button>
      <el-button type="danger" :loading="loading" @click="handleReview(false)">
        驳 回
      </el-button>
      <el-button type="success" :loading="loading" @click="handleReview(true)">
        通 过
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { LeaveRequest, LeaveStatus } from '@/types'
import { LEAVE_STATUS_MAP } from '@/types'

const props = defineProps<{
  modelValue: boolean
  leave: LeaveRequest | null
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'submit', comment: string, approved: boolean): void
}>()

const visible = ref(props.modelValue)
const loading = ref(false)
const formRef = ref<FormInstance>()

// 保持 modelValue 双向同步
watch(() => props.modelValue, (val) => { visible.value = val })
watch(visible, (val) => { emit('update:modelValue', val) })

const formData = reactive({ comment: '' })

const rules: FormRules = {
  comment: [],
}

function formatDateTime(dateStr: string | null): string {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ')
}

function getStatusText(status: LeaveStatus): string {
  return LEAVE_STATUS_MAP[status]?.text ?? status
}

function getStatusTag(status: LeaveStatus): '' | 'success' | 'warning' | 'info' | 'danger' {
  return LEAVE_STATUS_MAP[status]?.type ?? ''
}

async function handleReview(approved: boolean) {
  // 驳回时意见必填
  if (!approved && !formData.comment.trim()) {
    rules.comment = [{ required: true, message: '驳回必须填写意见', trigger: 'blur' }]
    await formRef.value?.validate().catch(() => {})
    return
  }

  // 后端通过关键词判断：含"驳回"/"不同意" → 驳回，否则 → 通过
  // 前端显式传 approved=false 时加"驳回："前缀
  const comment = approved
    ? (formData.comment.trim() || '同意请假')
    : `驳回：${formData.comment.trim()}`

  emit('submit', comment, approved)
}

function handleClose() {
  formData.comment = ''
  rules.comment = []
  formRef.value?.resetFields()
}

// 暴露方法给父组件设置 loading
defineExpose({ loading })
</script>
```

- [ ] **Step 2: 创建 ReviewListPage.vue**

创建 `frontend/src/pages/ReviewListPage.vue`：

```vue
<template>
  <div class="review-list-page">
    <el-card>
      <template #header>
        <span>{{ authStore.role === 'TEACHER' ? '待初审列表' : '待复审列表' }}</span>
      </template>

      <el-table :data="pagedData" border stripe v-loading="loading" style="width: 100%">
        <el-table-column label="请假类型" width="80">
          <template #default="{ row }">{{ row.type }}</template>
        </el-table-column>
        <el-table-column label="时间范围" min-width="200">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }} ~ {{ formatDate(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="原因" min-width="160" show-overflow-tooltip />
        <el-table-column label="提交时间" width="140">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openReview(row)">审核</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="filteredData.length > pageSize"
        class="pagination"
        background
        layout="prev, pager, next"
        :total="filteredData.length"
        :page-size="pageSize"
        v-model:current-page="currentPage"
      />
    </el-card>

    <!-- 审核弹窗 -->
    <ReviewDialog
      v-model="reviewVisible"
      :leave="currentLeave"
      @submit="handleReviewSubmit"
      ref="dialogRef"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { getPendingLeaves, reviewFirst, reviewSecond } from '@/api/leave'
import type { LeaveRequest } from '@/types'
import ReviewDialog from '@/components/ReviewDialog.vue'

const authStore = useAuthStore()

const loading = ref(false)
const allData = ref<LeaveRequest[]>([])
const currentPage = ref(1)
const pageSize = 12
const reviewVisible = ref(false)
const currentLeave = ref<LeaveRequest | null>(null)
const dialogRef = ref<InstanceType<typeof ReviewDialog>>()

// 按角色过滤待审核数据
const filteredData = computed(() => {
  if (authStore.role === 'TEACHER') {
    return allData.value.filter(l => l.status === 'PENDING')
  }
  // DEAN：只看老师已通过、待领导复核的
  return allData.value.filter(l => l.status === 'TEACHER_APPROVED')
})

const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredData.value.slice(start, start + pageSize)
})

function formatDate(dateStr: string | null): string {
  if (!dateStr) return '-'
  return dateStr.slice(0, 10)
}

function formatDateTime(dateStr: string | null): string {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ')
}

function openReview(row: LeaveRequest) {
  currentLeave.value = row
  reviewVisible.value = true
}

async function handleReviewSubmit(comment: string, _approved: boolean) {
  if (!currentLeave.value) return

  loading.value = true
  try {
    if (authStore.role === 'TEACHER') {
      await reviewFirst(currentLeave.value.id, { comment })
    } else {
      await reviewSecond(currentLeave.value.id, { comment })
    }
    ElMessage.success('审核完成')
    reviewVisible.value = false
    // 刷新列表
    await fetchData()
  } catch {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getPendingLeaves()
    allData.value = res.data.data
    currentPage.value = 1
  } catch {
    // 忽略
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
```

- [ ] **Step 3: 提交**

```bash
git add frontend/src/components/ReviewDialog.vue frontend/src/pages/ReviewListPage.vue
git commit -m "feat: 添加审核列表页 + 审核弹窗组件"
```

---

### Task 13: UserListPage 用户管理

**Files:**
- Create: `frontend/src/pages/UserListPage.vue`

**Consumes:** user API, 角色常量

**Produces:** 用户 CRUD 管理页面

---

- [ ] **Step 1: 创建 UserListPage.vue**

创建 `frontend/src/pages/UserListPage.vue`：

```vue
<template>
  <div class="user-list-page">
    <el-card>
      <!-- 工具栏 -->
      <div class="toolbar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户名..."
          style="width: 240px"
          clearable
        />
        <el-button type="primary" @click="openCreate">新增用户</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="pagedData" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ roleText(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="filteredData.length > pageSize"
        class="pagination"
        background
        layout="prev, pager, next"
        :total="filteredData.length"
        :page-size="pageSize"
        v-model:current-page="currentPage"
      />
    </el-card>

    <!-- 新增 / 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingUser ? '编辑用户' : '新增用户'"
      width="480px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码（编辑时空着表示不改）"
            show-password
          />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="学生" value="STUDENT" />
            <el-option label="老师" value="TEACHER" />
            <el-option label="学院领导" value="DEAN" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSave">保 存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getAllUsers, createUser, updateUser, deleteUser } from '@/api/user'
import type { User, UserFormData } from '@/types'
import { ROLE_MAP } from '@/types'

const loading = ref(false)
const submitting = ref(false)
const allData = ref<User[]>([])
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = 12
const dialogVisible = ref(false)
const editingUser = ref<User | null>(null)
const formRef = ref<FormInstance>()

const formData = reactive<UserFormData>({
  username: '',
  password: '',
  realName: '',
  role: 'STUDENT',
  phone: '',
})

// 编辑时密码非必填
const isEditing = computed(() => !!editingUser.value)

const rules = computed<FormRules>(() => ({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度 3~50', trigger: 'blur' },
  ],
  password: isEditing.value
    ? []
    : [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 100, message: '密码长度 6~100', trigger: 'blur' },
      ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
}))

// 前端搜索
const filteredData = computed(() => {
  if (!searchKeyword.value) return allData.value
  const kw = searchKeyword.value.toLowerCase()
  return allData.value.filter(u => u.username.toLowerCase().includes(kw))
})

const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredData.value.slice(start, start + pageSize)
})

function roleText(role: string) { return ROLE_MAP[role] ?? role }
function formatDateTime(dateStr: string | null): string {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ')
}

function openCreate() {
  editingUser.value = null
  formData.username = ''
  formData.password = ''
  formData.realName = ''
  formData.role = 'STUDENT'
  formData.phone = ''
  dialogVisible.value = true
}

function openEdit(user: User) {
  editingUser.value = user
  formData.username = user.username
  formData.password = ''
  formData.realName = user.realName
  formData.role = user.role
  formData.phone = user.phone
  dialogVisible.value = true
}

function handleDialogClose() {
  formRef.value?.resetFields()
}

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (editingUser.value) {
      const payload: any = {
        username: formData.username,
        realName: formData.realName,
        role: formData.role,
        phone: formData.phone,
      }
      if (formData.password) {
        payload.password = formData.password
      }
      await updateUser(editingUser.value.id, payload)
      ElMessage.success('更新成功')
    } else {
      await createUser({
        username: formData.username,
        password: formData.password!,
        realName: formData.realName,
        role: formData.role,
        phone: formData.phone || undefined,
      })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await fetchData()
  } catch {
    // 错误已在拦截器处理
  } finally {
    submitting.value = false
  }
}

async function handleDelete(user: User) {
  try {
    await ElMessageBox.confirm(
      `确定删除用户「${user.username}」吗？此操作不可撤销。`,
      '删除确认',
      { type: 'warning' }
    )
    await deleteUser(user.id)
    ElMessage.success('删除成功')
    await fetchData()
  } catch {
    // 取消或错误
  }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getAllUsers()
    allData.value = res.data.data
  } catch {
    // 忽略
  } finally {
    loading.value = false
  }
}

onMounted(() => fetchData())
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
```

- [ ] **Step 2: 提交**

```bash
git add frontend/src/pages/UserListPage.vue
git commit -m "feat: 添加 UserListPage 用户管理页面"
```

---

### Task 14: 组装 App.vue + main.ts

**Files:**
- Modify: `frontend/src/main.ts`
- Modify: `frontend/src/App.vue`

**Consumes:** 所有已完成模块

**Produces:** 完整可运行的应用

---

- [ ] **Step 1: 更新 main.ts**

覆写 `frontend/src/main.ts`：

```typescript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'

const app = createApp(App)

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')
```

- [ ] **Step 2: 更新 App.vue**

覆写 `frontend/src/App.vue`：

```vue
<template>
  <!-- 登录页不需要布局壳 -->
  <template v-if="route.path === '/login'">
    <router-view />
  </template>
  <!-- 其他页面用 AppLayout 包裹 -->
  <template v-else>
    <AppLayout />
  </template>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import AppLayout from '@/components/AppLayout.vue'

const route = useRoute()
</script>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}
</style>
```

- [ ] **Step 3: 提交**

```bash
git add frontend/src/main.ts frontend/src/App.vue
git commit -m "feat: 组装 App.vue + main.ts，应用入口完成"
```

---

### Task 15: 启动验证

**Files:** 无新建

**Purpose:** 启动前后端，验证全流程

---

- [ ] **Step 1: 启动后端**

```bash
cd 项目根目录 && mvn spring-boot:run
```

Expected: Spring Boot 启动在 `localhost:8080`

- [ ] **Step 2: 启动前端**

```bash
cd frontend && pnpm dev
```

Expected: Vite 启动在 `localhost:5173`

- [ ] **Step 3: 验证登录流程**

用 Apifox 或浏览器打开 `http://localhost:5173`：
1. 页面自动跳转到 `/login`
2. 注册一个学生账号
3. 注册成功后切换到登录
4. 登录成功后跳转到 `/dashboard`
5. 侧边栏显示：首页 / 提交请假 / 请假记录（学生角色）

- [ ] **Step 4: 验证请假流程**

1. 提交请假 → 填写表单 → 提交成功 → 跳转到请假记录
2. 请假记录中看到刚提交的申请，状态为"待审核"
3. 退出登录 → 用老师账号登录
4. 审核列表 → 看到待初审的申请 → 审核（通过/驳回）
5. 退出登录 → 用学生账号登录 → 请假记录中看到状态变化

- [ ] **Step 5: 验证用户管理（领导角色）**

1. 退出登录 → 用领导账号登录
2. 侧边栏多出"审核列表"和"用户管理"
3. 用户管理 → 新增/编辑/删除用户

- [ ] **Step 6: 验证路由守卫**

1. 未登录直接访问 `http://localhost:5173/dashboard` → 自动跳转 `/login`
2. 学生登录后手动输入 `http://localhost:5173/users` → 自动跳转 `/dashboard`

- [ ] **Step 7: 提交最终版本**

```bash
git add -A && git commit -m "feat: 前端应用完成，启动验证通过"
```

---

## 实现顺序总结

```
Task 1  → 项目脚手架 (package.json, vite.config.ts, tsconfig 等)
Task 2  → TypeScript 类型定义
Task 3  → Axios 请求封装
Task 4  → API 模块 (auth / leave / user)
Task 5  → Pinia 状态管理
Task 6  → Vue Router 路由配置
Task 7  → AppLayout 布局壳
Task 8  → LoginPage 登录/注册
Task 9  → DashboardPage 首页
Task 10 → LeaveCreatePage 提交请假
Task 11 → LeaveListPage 请假记录
Task 12 → ReviewListPage + ReviewDialog 审核
Task 13 → UserListPage 用户管理
Task 14 → App.vue + main.ts 组装
Task 15 → 启动验证
```

> Task 2-6 是基础设施层，完成后 Task 7-14 的页面可并行开发（每个页面只依赖基础设施）。
