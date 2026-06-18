import request from './request'

export function createAlipay(orderNo) {
  return request.post('/v1/user/payment/alipay/create', { orderNo })
}
