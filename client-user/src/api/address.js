import request from './request'

export function getAddresses() {
  return request.get('/v1/user/addresses')
}

export function addAddress(data) {
  return request.post('/v1/user/addresses', data)
}
