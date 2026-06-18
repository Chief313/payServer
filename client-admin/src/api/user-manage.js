import request from './request'

export function getUsers(params) {
  return request.get('/v1/admin/users', { params })
}

export function createUser(data) {
  return request.post('/v1/admin/users', data)
}

export function updateUser(id, data) {
  return request.put(`/v1/admin/users/${id}`, data)
}

export function deleteUser(id) {
  return request.delete(`/v1/admin/users/${id}`)
}
