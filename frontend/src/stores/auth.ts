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
