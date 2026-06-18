import request from './request'

export function sendChat(data) {
  return request.post('/v1/ai/chat', data)
}

export function getSessions() {
  return request.get('/v1/ai/sessions')
}
