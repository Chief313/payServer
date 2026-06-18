import request from './request'

export function getOrders(params) {
  return request.get('/v1/user/orders', { params })
}

export function getOrderDetail(id) {
  return request.get(`/v1/user/orders/${id}`)
}

export function createOrder(addressId) {
  return request.post('/v1/user/orders', { addressId })
}
