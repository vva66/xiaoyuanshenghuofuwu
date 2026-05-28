import request from '@/utils/request'

// 获取课程列表
export const getUserCourses = (userId) => {
  return request({
    url: `/course/list/${userId}`,
    method: 'get'
  })
}

// 添加课程
export const addCourse = (data) => {
  return request({
    url: '/course/add',
    method: 'post',
    data
  })
}

// 更新课程
export const updateCourse = (data) => {
  return request({
    url: '/course/update',
    method: 'put',
    data
  })
}

// 删除课程
export const deleteCourse = (id) => {
  return request({
    url: `/course/delete/${id}`,
    method: 'delete'
  })
}
