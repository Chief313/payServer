<template>
  <div class="auth-page">
    <div class="auth-bg"></div>
    <div class="auth-card">
      <div class="auth-header">
        <span class="logo">🛍️</span>
        <h2>欢迎回来</h2>
        <p>登录 PayServer 商城，畅享购物</p>
      </div>
      <form @submit.prevent="handleLogin">
        <div class="form-item">
          <label>用户名</label>
          <input v-model="form.username" placeholder="user01" required />
        </div>
        <div class="form-item">
          <label>密码</label>
          <input v-model="form.password" type="password" placeholder="123456" required />
        </div>
        <p v-if="error" class="error">{{ error }}</p>
        <button type="submit" class="btn-primary btn-full" :disabled="loading">
          {{ loading ? '登录中...' : '登 录' }}
        </button>
      </form>
      <p class="footer-link">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </p>
      <p class="hint">测试账号：user01 / 123456</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const error = ref('')
const form = reactive({ username: '', password: '' })

async function handleLogin() {
  loading.value = true
  error.value = ''
  try {
    await userStore.login(form.username, form.password)
    router.push(route.query.redirect || '/')
  } catch (e) {
    error.value = e.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.auth-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 40%, #ec4899 100%);
}

.auth-bg::after {
  content: '';
  position: absolute;
  width: 600px;
  height: 600px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.06);
  top: -200px;
  right: -100px;
}

.auth-card {
  position: relative;
  z-index: 1;
  background: var(--color-surface);
  padding: 40px 44px;
  border-radius: var(--radius-lg);
  width: 420px;
  max-width: 92vw;
  box-shadow: var(--shadow-lg);
}

.auth-header {
  text-align: center;
  margin-bottom: 28px;
}

.logo {
  font-size: 40px;
  display: block;
  margin-bottom: 12px;
}

.auth-header h2 {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 6px;
}

.auth-header p {
  color: var(--color-text-muted);
  font-size: 14px;
}

.form-item {
  margin-bottom: 18px;
}

.form-item label {
  display: block;
  margin-bottom: 6px;
  color: var(--color-text-secondary);
  font-size: 13px;
  font-weight: 500;
}

.form-item input {
  width: 100%;
  padding: 12px 14px;
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: 15px;
  outline: none;
  transition: border-color 0.15s;
}

.form-item input:focus {
  border-color: var(--color-primary);
}

.btn-full {
  width: 100%;
  padding: 13px;
  margin-top: 8px;
}

.error {
  color: var(--color-accent);
  font-size: 13px;
  margin-bottom: 8px;
}

.footer-link {
  text-align: center;
  margin-top: 20px;
  color: var(--color-text-muted);
  font-size: 14px;
}

.footer-link a {
  color: var(--color-primary);
  font-weight: 600;
}

.hint {
  text-align: center;
  margin-top: 12px;
  font-size: 12px;
  color: var(--color-text-muted);
}
</style>
