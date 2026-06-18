import request from './request'

export function login(data) {
  return request.post('/v1/auth/login', data)
}

export function register(data) {
  return request.post('/v1/auth/register', data)
}
