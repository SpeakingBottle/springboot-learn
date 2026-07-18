import request from './request'
import type { ApiResponse, User, UserFormData } from '@/types'

/** 获取所有用户 */
export function getAllUsers() {
  return request.get<ApiResponse<User[]>>('/api/users')
}

/** 新增用户 */
export function createUser(data: UserFormData) {
  return request.post<ApiResponse<null>>('/api/users', data)
}

/** 更新用户 */
export function updateUser(id: number, data: Partial<User>) {
  return request.put<ApiResponse<null>>(`/api/users/${id}`, data)
}

/** 删除用户 */
export function deleteUser(id: number) {
  return request.delete<ApiResponse<null>>(`/api/users/${id}`)
}
