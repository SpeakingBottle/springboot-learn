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
