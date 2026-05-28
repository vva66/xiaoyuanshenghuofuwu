import request from '@/utils/request'

// 获取帖子列表
export const getPostList = (params) => {
  return request({
    url: '/help/list',
    method: 'get',
    params
  })
}

// 获取帖子详情
export const getPostDetail = (id) => {
  return request({
    url: `/help/detail/${id}`,
    method: 'get'
  })
}

// 发布帖子
export const publishPost = (data) => {
  return request({
    url: '/help/publish',
    method: 'post',
    data
  })
}

// 更新帖子
export const updatePost = (data) => {
  return request({
    url: '/help/update',
    method: 'put',
    data
  })
}

// 删除帖子
export const deletePost = (id) => {
  return request({
    url: `/help/delete/${id}`,
    method: 'delete'
  })
}

// 获取我的帖子
export const getMyPosts = (userId, params) => {
  return request({
    url: `/help/myPosts/${userId}`,
    method: 'get',
    params
  })
}

// 标记已解决
export const resolvePost = (id, userId) => {
  return request({
    url: `/help/resolve/${id}`,
    method: 'put',
    params: { userId }
  })
}

// 关闭帖子
export const closePost = (id, userId) => {
  return request({
    url: `/help/close/${id}`,
    method: 'put',
    params: { userId }
  })
}

// 添加评论
export const addComment = (data) => {
  return request({
    url: '/help/comment/add',
    method: 'post',
    data
  })
}

// 删除评论
export const deleteComment = (id) => {
  return request({
    url: `/help/comment/delete/${id}`,
    method: 'delete'
  })
}

// 获取评论列表
export const getCommentList = (postId, params) => {
  return request({
    url: `/help/comment/list/${postId}`,
    method: 'get',
    params
  })
}

// 获取评论的回复
export const getReplies = (parentId) => {
  return request({
    url: `/help/comment/replies/${parentId}`,
    method: 'get'
  })
}
