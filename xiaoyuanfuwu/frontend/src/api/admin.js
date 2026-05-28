import request from '@/utils/request'

// 获取统计数据
export function getStatistics() {
  return request({
    url: '/admin/statistics',
    method: 'get'
  })
}

// 用户管理
export function getUserList(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

export function updateUserStatus(id, status) {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'put',
    data: { status }
  })
}

export function updateUser(data) {
  return request({
    url: '/admin/users',
    method: 'put',
    data
  })
}

// 活动管理
export function getActivityList(params) {
  return request({
    url: '/admin/activities',
    method: 'get',
    params
  })
}

export function deleteActivity(id) {
  return request({
    url: `/admin/activities/${id}`,
    method: 'delete'
  })
}

// 帖子管理
export function getPostList(params) {
  return request({
    url: '/admin/posts',
    method: 'get',
    params
  })
}

export function approvePost(id) {
  return request({
    url: `/admin/posts/${id}/approve`,
    method: 'put'
  })
}

export function rejectPost(id) {
  return request({
    url: `/admin/posts/${id}/reject`,
    method: 'put'
  })
}

// 社团管理
export function getClubList(params) {
  return request({
    url: '/admin/clubs',
    method: 'get',
    params
  })
}

export function deleteClub(id) {
  return request({
    url: `/admin/clubs/${id}`,
    method: 'delete'
  })
}
