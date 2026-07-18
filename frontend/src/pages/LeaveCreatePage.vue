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
