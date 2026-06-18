import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi } from '../api/auth'
import router from '../router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('admin_user') || 'null'))

  const isLoggedIn = computed(() => !!token.value)

  async function login(username, password) {
    const data = await loginApi({ username, password })
    if (data.role !== 'ADMIN') {
      throw new Error('非管理员账号，无法登录后台')
    }
    token.value = data.token
    userInfo.value = {
      userId: data.userId,
      username: data.username,
      nickname: data.nickname,
      role: data.role,
    }
    localStorage.setItem('admin_token', data.token)
    localStorage.setItem('admin_user', JSON.stringify(userInfo.value))
    return data
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_user')
    router.push('/login')
  }

  return { token, userInfo, isLoggedIn, login, logout }
})
