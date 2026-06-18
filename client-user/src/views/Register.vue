<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>用户注册</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-item">
          <label>用户名</label>
          <input v-model="form.username" placeholder="请输入用户名" required />
        </div>
        <div class="form-item">
          <label>密码</label>
          <input v-model="form.password" type="password" placeholder="请输入密码" required />
        </div>
        <div class="form-item">
          <label>昵称</label>
          <input v-model="form.nickname" placeholder="选填" />
        </div>
        <div class="form-item">
          <label>手机号</label>
          <input v-model="form.phone" placeholder="选填" />
        </div>
        <p v-if="error" class="error">{{ error }}</p>
        <button type="submit" class="btn-primary" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>
      <p class="footer-link">
        已有账号？<router-link to="/login">去登录</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const error = ref('')
const form = reactive({ username: '', password: '', nickname: '', phone: '' })

async function handleRegister() {
  loading.value = true
  error.value = ''
  try {
    await userStore.register(form)
    router.push('/')
  } catch (e) {
    error.value = e.message || '注册失败'
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
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
}

.auth-card {
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  width: 400px;
}

.auth-card h2 {
  text-align: center;
  margin-bottom: 24px;
}

.form-item {
  margin-bottom: 16px;
}

.form-item label {
  display: block;
  margin-bottom: 6px;
  color: #666;
  font-size: 14px;
}

.form-item input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.btn-primary {
  width: 100%;
  padding: 12px;
  background: #e4393c;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  margin-top: 8px;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error {
  color: #e4393c;
  font-size: 13px;
  margin-bottom: 8px;
}

.footer-link {
  text-align: center;
  margin-top: 16px;
  color: #999;
  font-size: 14px;
}

.footer-link a {
  color: #e4393c;
}
</style>
