import request from '@/utils/request'

// 获取食堂列表
export const getCanteenList = (params) => {
  return request({
    url: '/canteen/list',
    method: 'get',
    params
  })
}

// 获取食堂详情
export const getCanteenDetail = (id) => {
  return request({
    url: `/canteen/detail/${id}`,
    method: 'get'
  })
}

// 获取菜品列表
export const getDishesByCanteenId = (canteenId) => {
  return request({
    url: `/canteen/dishes/${canteenId}`,
    method: 'get'
  })
}

// 获取推荐菜品
export const getRecommendDishes = () => {
  return request({
    url: '/canteen/recommend',
    method: 'get'
  })
}
