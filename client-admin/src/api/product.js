import request from './request'

export function getCategories() {
  return request.get('/v1/admin/categories')
}

export function createCategory(data) {
  return request.post('/v1/admin/categories', data)
}

export function updateCategory(id, data) {
  return request.put(`/v1/admin/categories/${id}`, data)
}

export function deleteCategory(id) {
  return request.delete(`/v1/admin/categories/${id}`)
}

export function getProducts(params) {
  return request.get('/v1/admin/products', { params })
}

export function createProduct(data) {
  return request.post('/v1/admin/products', data)
}

export function updateProduct(id, data) {
  return request.put(`/v1/admin/products/${id}`, data)
}

export function deleteProduct(id) {
  return request.delete(`/v1/admin/products/${id}`)
}

export function createSku(productId, data) {
  return request.post(`/v1/admin/products/${productId}/skus`, data)
}
