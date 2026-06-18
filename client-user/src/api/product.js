import request from './request'

/** 获取商品分类列表 */
export function getCategories() {
  return request.get('/v1/user/categories')
}

export function getProducts(params) {
  return request.get('/v1/user/products', { params })
}

export function getProductDetail(id) {
  return request.get(`/v1/user/products/${id}`)
}
