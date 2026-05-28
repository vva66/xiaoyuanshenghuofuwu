import request from '@/utils/request'

export const getGuides = (category) => {
  return request({
    url: '/freshman/guides',
    method: 'get',
    params: category ? { category } : {}
  })
}

export const getGuideDetail = (id) => {
  return request({
    url: `/freshman/guide/${id}`,
    method: 'get'
  })
}
