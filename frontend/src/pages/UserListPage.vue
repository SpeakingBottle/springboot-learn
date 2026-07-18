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
