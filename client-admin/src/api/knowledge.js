import request from './request'

export function listKnowledge() {
  return request.get('/v1/admin/ai/knowledge/list')
}

export function uploadKnowledge(formData) {
  return request.post('/v1/admin/ai/knowledge/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export function deleteKnowledge(id) {
  return request.delete(`/v1/admin/ai/knowledge/${id}`)
}
