import request from '@/utils/request'

// 获取社团列表
export const getClubList = (params) => {
  return request({
    url: '/club/list',
    method: 'get',
    params
  })
}

// 获取社团详情
export const getClubDetail = (id) => {
  return request({
    url: `/club/detail/${id}`,
    method: 'get'
  })
}

// 创建社团
export const createClub = (data) => {
  return request({
    url: '/club/create',
    method: 'post',
    data
  })
}

// 加入社团
export const joinClub = ({ clubId, userId, userName }) => {
  return request({
    url: '/club/join',
    method: 'post',
    data: { clubId, userId, userName }
  })
}

// 退出社团
export const leaveClub = ({ clubId, userId }) => {
  return request({
    url: `/club/leave/${clubId}`,
    method: 'post',
    data: { userId }
  })
}

// 获取我加入的社团
export const getMyClubs = (userId) => {
  return request({
    url: '/club/my-clubs',
    method: 'get',
    params: { userId }
  })
}

// 更新社团
export const updateClub = (data) => {
  return request({
    url: '/club/update',
    method: 'put',
    data
  })
}

// 删除社团
export const deleteClub = (id) => {
  return request({
    url: `/club/delete/${id}`,
    method: 'delete'
  })
}

// 获取待审核社团列表
export const getPendingClubs = (params) => {
  return request({
    url: '/club/pending',
    method: 'get',
    params
  })
}

// 获取所有社团（管理端）
export const getAllClubs = (params) => {
  return request({
    url: '/club/admin/list',
    method: 'get',
    params
  })
}

// 审核通过社团
export const approveClub = (id) => {
  return request({
    url: `/club/approve/${id}`,
    method: 'post'
  })
}

// 拒绝社团申请
export const rejectClub = (id) => {
  return request({
    url: `/club/reject/${id}`,
    method: 'post'
  })
}

// 创建社团活动
export const createClubActivity = (data) => {
  return request({
    url: '/club/activity/create',
    method: 'post',
    data
  })
}

// 获取社团活动列表
export const getClubActivityList = (params) => {
  return request({
    url: `/club/activity/club/${params.clubId}`,
    method: 'get'
  })
}

// 获取社团活动详情
export const getClubActivityDetail = (id) => {
  return request({
    url: `/club/activity/detail/${id}`,
    method: 'get'
  })
}

// 报名社团活动
export const registerClubActivity = (data) => {
  return request({
    url: '/club/activity/register',
    method: 'post',
    data
  })
}

// 取消报名
export const cancelClubActivity = (data) => {
  return request({
    url: '/club/activity/cancel',
    method: 'post',
    data
  })
}

// 检查用户是否为社长
export const isPresident = (clubId, userId) => {
  return request({
    url: '/club/isPresident',
    method: 'get',
    params: { clubId, userId }
  })
}

// 获取社团成员列表
export const getClubMembers = (clubId) => {
  return request({
    url: `/club/members/${clubId}`,
    method: 'get'
  })
}

// 获取我管理的社团（社长身份）
export const getMyManagedClubs = (userId) => {
  return request({
    url: '/club/my-managed',
    method: 'get',
    params: { userId }
  })
}

// 移除社团成员
export const removeMember = (clubId, userId) => {
  return request({
    url: `/club/member/remove`,
    method: 'post',
    data: { clubId, userId }
  })
}

// 获取入社申请列表
export const getJoinRequests = (clubId) => {
  return request({
    url: `/club/join-requests/${clubId}`,
    method: 'get'
  })
}

// 审核入社申请
export const approveJoinRequest = (requestId, status) => {
  return request({
    url: '/club/join-request/handle',
    method: 'post',
    data: { requestId, status }
  })
}

// 创建社团任务
export const createTask = (data) => {
  return request({
    url: '/club/task/create',
    method: 'post',
    data
  })
}

// 获取社团任务列表
export const getClubTasks = (clubId) => {
  return request({
    url: `/club/tasks/${clubId}`,
    method: 'get'
  })
}

// 完成任务
export const completeTask = (taskId, userId) => {
  return request({
    url: '/club/task/complete',
    method: 'post',
    data: { taskId, userId }
  })
}

// 删除任务
export const deleteTask = (taskId) => {
  return request({
    url: `/club/task/delete/${taskId}`,
    method: 'delete'
  })
}

// 活动签到
export const signInActivity = (activityId, userId) => {
  return request({
    url: '/club/activity/signin',
    method: 'post',
    data: { activityId, userId }
  })
}

// 获取活动签到记录
export const getActivitySignIns = (activityId) => {
  return request({
    url: `/club/activity/signins/${activityId}`,
    method: 'get'
  })
}

// 获取活动报名列表
export const getActivityRegistrations = (activityId) => {
  return request({
    url: `/club/activity/registrations/${activityId}`,
    method: 'get'
  })
}

// 更新活动信息
export const updateClubActivity = (data) => {
  return request({
    url: '/club/activity/update',
    method: 'put',
    data
  })
}

// 删除活动
export const deleteClubActivity = (activityId) => {
  return request({
    url: `/club/activity/delete/${activityId}`,
    method: 'delete'
  })
}
