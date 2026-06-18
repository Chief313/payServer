import request from './request'

export function getOrders(params) {
  return request.get('/v1/admin/orders', { params })
}

export function getOrderDetail(id) {
  return request.get(`/v1/admin/orders/${id}`)
}

export function shipOrder(id) {
  return request.put(`/v1/admin/orders/${id}/ship`)
}
