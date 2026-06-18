import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi } from '../api/auth'
import router from '../router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('user_token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('user_info') || 'null'))

  const isLoggedIn = computed(() => !!token.value)

  function setAuth(data) {
    token.value = data.token
    userInfo.value = {
      userId: data.userId,
      username: data.username,
      nickname: data.nickname,
      role: data.role,
    }
    localStorage.setItem('user_token', data.token)
    localStorage.setItem('user_info', JSON.stringify(userInfo.value))
  }

  async function login(username, password) {
    const data = await loginApi({ username, password })
    setAuth(data)
    return data
  }

  async function register(form) {
    const data = await registerApi(form)
    setAuth(data)
    return data
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('user_token')
    localStorage.removeItem('user_info')
    router.push('/login')
  }

  return { token, userInfo, isLoggedIn, login, register, logout }
})
