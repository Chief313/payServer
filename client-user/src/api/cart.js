import request from './request'

export function getCart() {
  return request.get('/v1/user/cart')
}

export function addToCart(data) {
  return request.post('/v1/user/cart', data)
}

export function updateCartItem(id, quantity) {
  return request.put(`/v1/user/cart/${id}`, { quantity })
}

export function removeCartItem(id) {
  return request.delete(`/v1/user/cart/${id}`)
}
