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
