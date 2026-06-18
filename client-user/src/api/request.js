import axios from 'axios'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000,
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('user_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    }
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    const status = error.response?.status
    const res = error.response?.data
    if (status === 401 || res?.code === 401) {
      localStorage.removeItem('user_token')
      localStorage.removeItem('user_info')
      const path = router.currentRoute.value.path
      if (path !== '/login' && path !== '/register') {
        router.push({ path: '/login', query: { redirect: path } })
      }
    }
    return Promise.reject(error)
  },
)

export default request
