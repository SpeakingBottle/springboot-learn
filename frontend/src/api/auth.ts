import request from './request'
import type { ApiResponse, LoginData, LoginResult, RegisterData, User } from '@/types'

/** 登录 */
export function login(data: LoginData) {
  return request.post<ApiResponse<LoginResult>>('/api/auth/login', data)
}

/** 注册 */
export function register(data: RegisterData) {
  return request.post<ApiResponse<null>>('/api/auth/register', data)
}

/** 获取当前用户信息 */
export function getMe() {
  return request.get<ApiResponse<User>>('/api/auth/me')
}
