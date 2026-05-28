import request from '@/utils/request'

export const sendMessage = (data) => {
  return request({
    url: '/message/send',
    method: 'post',
    data
  })
}

export const getConversations = (userId, params) => {
  return request({
    url: '/message/conversations',
    method: 'get',
    params: { userId, ...params }
  })
}

export const getConversation = (userId, otherUserId, relatedPostId) => {
  return request({
    url: '/message/conversation',
    method: 'get',
    params: { userId, otherUserId, relatedPostId }
  })
}

export const getUnreadCount = (userId) => {
  return request({
    url: '/message/unread',
    method: 'get',
    params: { userId }
  })
}

export const markAsRead = (userId, otherUserId, relatedPostId) => {
  return request({
    url: '/message/read',
    method: 'put',
    params: { userId, otherUserId, relatedPostId }
  })
}

export const markAllAsRead = (userId) => {
  return request({
    url: '/message/readAll',
    method: 'put',
    params: { userId }
  })
}
