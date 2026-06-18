import request from './request'

/**
 * 上传商品图片
 * @param {FormData} formData 包含 file 字段
 * @returns {Promise<{ fileUrl: string }>}
 */
export function uploadPic(formData) {
  return request.post('/pic/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
