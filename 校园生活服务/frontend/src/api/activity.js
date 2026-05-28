import request from '@/utils/request'

// 获取活动列表
export const getActivityList = (params) => {
  return request({
    url: '/activity/list',
    method: 'get',
    params
  })
}

// 获取推荐活动
export const getRecommendActivities = () => {
  return request({
    url: '/activity/recommend',
    method: 'get'
  })
}

// 获取活动详情
export const getActivityDetail = (id) => {
  return request({
    url: `/activity/detail/${id}`,
    method: 'get'
  })
}

// 创建活动
export const createActivity = (data) => {
  return request({
    url: '/activity/create',
    method: 'post',
    data
  })
}

// 更新活动
export const updateActivity = (data) => {
  return request({
    url: '/activity/update',
    method: 'put',
    data
  })
}

// 删除活动
export const deleteActivity = (id) => {
  return request({
    url: `/activity/delete/${id}`,
    method: 'delete'
  })
}
