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
