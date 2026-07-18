// ========== 后端统一返回体 ==========
export interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

// ========== 用户 ==========
export interface User {
  id: number
  username: string
  password?: string
  realName: string
  role: 'STUDENT' | 'TEACHER' | 'DEAN'
  phone: string
  createTime: string
  updateTime: string
}

// ========== 请假状态 ==========
export type LeaveStatus =
  | 'PENDING'
  | 'TEACHER_APPROVED'
  | 'TEACHER_REJECTED'
  | 'DEAN_APPROVED'
  | 'DEAN_REJECTED'

// ========== 状态标签配置 ==========
export const LEAVE_STATUS_MAP: Record<LeaveStatus, { text: string; type: '' | 'success' | 'warning' | 'info' | 'danger' }> = {
  PENDING: { text: '待审核', type: 'warning' },
  TEACHER_APPROVED: { text: '老师已通过', type: 'info' },
  TEACHER_REJECTED: { text: '老师已驳回', type: 'danger' },
  DEAN_APPROVED: { text: '已通过', type: 'success' },
  DEAN_REJECTED: { text: '已驳回', type: 'danger' },
}

// ========== 角色名称映射 ==========
export const ROLE_MAP: Record<string, string> = {
  STUDENT: '学生',
  TEACHER: '老师',
  DEAN: '学院领导',
}

// ========== 请假申请 ==========
export interface LeaveRequest {
  id: number
  studentId: number
  type: string
  startTime: string
  endTime: string
  reason: string
  status: LeaveStatus
  teacherId: number | null
  teacherComment: string | null
  teacherTime: string | null
  deanId: number | null
  deanComment: string | null
  deanTime: string | null
  createTime: string
  updateTime: string
}

// ========== 请求体 ==========
export interface LoginData {
  username: string
  password: string
}

export interface RegisterData {
  username: string
  password: string
  realName: string
  role: string
  phone?: string
}

export interface LeaveFormData {
  studentId: number
  type: string
  startTime: string
  endTime: string
  reason: string
}

export interface ReviewData {
  comment: string
}

export interface UserFormData {
  username: string
  password?: string
  realName: string
  role: string
  phone?: string
}

// ========== 登录响应 ==========
export interface LoginResult {
  token: string
  userId: number
  username: string
  role: string
}
