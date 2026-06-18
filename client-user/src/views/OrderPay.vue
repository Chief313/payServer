<template>
  <div class="pay-page">
    <div class="pay-card">
      <h2>订单支付</h2>
      <p class="order-no">订单号：{{ orderNo }}</p>
      <div v-if="loading" class="loading">正在生成支付表单...</div>
      <div v-else-if="error" class="error">{{ error }}</div>
      <p v-else class="hint">正在跳转至支付宝，请稍候...</p>
    </div>
    <div ref="formContainer" style="display: none"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { createAlipay } from '../api/payment'

const route = useRoute()
const orderNo = route.params.orderNo
const loading = ref(true)
const error = ref('')
const formContainer = ref(null)

onMounted(async () => {
  try {
    const res = await createAlipay(orderNo)
    const html = res.payForm
    if (!html) {
      error.value = '支付表单生成失败'
      return
    }
    const div = formContainer.value
    div.innerHTML = html
    const form = div.querySelector('form')
    if (form) {
      form.setAttribute('target', '_blank')
      form.submit()
    } else {
      error.value = '支付表单格式异常'
    }
  } catch (e) {
    error.value = e.message || '支付请求失败'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.pay-page {
  display: flex;
  justify-content: center;
  padding: 60px 20px;
}

.pay-card {
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  text-align: center;
  min-width: 400px;
}

.pay-card h2 {
  margin-bottom: 16px;
}

.order-no {
  color: #666;
  margin-bottom: 24px;
}

.loading, .error, .hint {
  color: #999;
}

.error {
  color: #e4393c;
}
</style>
