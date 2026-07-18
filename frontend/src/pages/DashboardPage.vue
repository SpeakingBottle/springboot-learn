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
      // 后端接口返回全部请假单，前端按当前登录学生过滤，只统计本人的申请
      const myLeaves = data.filter(l => l.studentId === authStore.user?.id)
      stats.value.pending = myLeaves.filter(l => l.status === 'PENDING').length
      stats.value.approved = myLeaves.filter(l =>
        l.status === 'DEAN_APPROVED' || l.status === 'TEACHER_APPROVED'
      ).length
      stats.value.rejected = myLeaves.filter(l =>
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
