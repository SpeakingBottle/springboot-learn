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

  loading.value = true

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
