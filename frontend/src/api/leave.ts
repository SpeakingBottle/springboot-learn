import request from './request'
import type { ApiResponse, LeaveRequest, LeaveFormData, ReviewData } from '@/types'

/** 获取所有请假单 */
export function getAllLeaves() {
  return request.get<ApiResponse<LeaveRequest[]>>('/api/leave-requests')
}

/** 根据 ID 获取请假单 */
export function getLeaveById(id: number) {
  return request.get<ApiResponse<LeaveRequest>>(`/api/leave-requests/${id}`)
}

/** 学生提交请假申请 */
export function createLeave(data: LeaveFormData) {
  return request.post<ApiResponse<null>>('/api/leave-requests', data)
}

/** 老师初审 */
export function reviewFirst(id: number, data: ReviewData) {
  return request.put<ApiResponse<null>>(`/api/leave-requests/${id}/review-first`, data)
}

/** 领导复审 */
export function reviewSecond(id: number, data: ReviewData) {
  return request.put<ApiResponse<null>>(`/api/leave-requests/${id}/review-second`, data)
}

/** 获取待审核列表 */
export function getPendingLeaves() {
  return request.get<ApiResponse<LeaveRequest[]>>('/api/leave-requests/pending')
}
