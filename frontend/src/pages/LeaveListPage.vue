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
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

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
    // 后端返回全部请假单，学生视角只保留本人的
    if (authStore.role === 'STUDENT') {
      allData.value = allData.value.filter(l => l.studentId === authStore.user?.id)
    }
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
