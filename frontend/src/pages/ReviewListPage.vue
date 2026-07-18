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
    // 重置弹窗按钮 loading 态
    dialogRef.value!.loading = false
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
