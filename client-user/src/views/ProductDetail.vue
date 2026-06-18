<template>
  <div class="detail-page">
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>
    <div v-else-if="detail" class="detail-card">
      <div class="cover-section">
        <img :src="coverSrc" :alt="detail.product.name" @error="onImgError" />
      </div>
      <div class="info-section">
        <h1>{{ detail.product.name }}</h1>
        <p class="desc">{{ detail.product.description }}</p>
        <div class="price-box">
          <span class="label">售价</span>
          <span class="price">¥{{ selectedSku?.price || detail.product.price }}</span>
        </div>

        <div v-if="detail.skus?.length" class="sku-section">
          <label>选择规格</label>
          <div class="sku-list">
            <button
              v-for="sku in detail.skus"
              :key="sku.id"
              :class="['sku-btn', { active: selectedSkuId === sku.id, disabled: sku.stock <= 0 }]"
              :disabled="sku.stock <= 0"
              @click="selectedSkuId = sku.id"
            >
              {{ sku.skuName }}
              <span class="stock">{{ sku.stock <= 0 ? '缺货' : `库存 ${sku.stock}` }}</span>
            </button>
          </div>
        </div>

        <div class="quantity-row">
          <label>数量</label>
          <div class="stepper">
            <button @click="quantity = Math.max(1, quantity - 1)">−</button>
            <span>{{ quantity }}</span>
            <button @click="quantity++">+</button>
          </div>
        </div>

        <button class="btn-primary btn-cart" :disabled="!canAdd" @click="handleAddCart">
          🛒 加入购物车
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetail } from '../api/product'
import { addToCart } from '../api/cart'
import { useUserStore } from '../stores/user'
import { productPlaceholder } from '../utils/placeholder'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const detail = ref(null)
const selectedSkuId = ref(null)
const quantity = ref(1)

const coverSrc = computed(() => {
  const url = detail.value?.product?.coverUrl
  if (!url || url.includes('/pic/demo/')) {
    return productPlaceholder(detail.value?.product?.id)
  }
  return url
})

const selectedSku = computed(() => detail.value?.skus?.find((s) => s.id === selectedSkuId.value))

const canAdd = computed(() => {
  if (!detail.value?.skus?.length) return false
  const sku = selectedSku.value
  return sku && sku.stock >= quantity.value
})

function onImgError(e) {
  e.target.src = productPlaceholder(detail.value?.product?.id)
}

async function fetchDetail() {
  loading.value = true
  try {
    detail.value = await getProductDetail(route.params.id)
    const firstAvailable = detail.value.skus?.find((s) => s.stock > 0)
    selectedSkuId.value = firstAvailable?.id || detail.value.skus?.[0]?.id
  } finally {
    loading.value = false
  }
}

async function handleAddCart() {
  if (!userStore.isLoggedIn) {
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  if (!selectedSkuId.value) {
    alert('请选择规格')
    return
  }
  await addToCart({ skuId: selectedSkuId.value, quantity: quantity.value })
  alert('已加入购物车')
}

onMounted(fetchDetail)
</script>

<style scoped>
.detail-page {
  padding-top: 8px;
}

.detail-card {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  background: var(--color-surface);
  padding: 36px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-border);
}

.cover-section {
  background: linear-gradient(145deg, #f8fafc, #f1f5f9);
  border-radius: var(--radius-md);
  overflow: hidden;
  min-height: 420px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-section img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  min-height: 420px;
}

.info-section h1 {
  font-size: 26px;
  font-weight: 700;
  margin-bottom: 12px;
  line-height: 1.3;
}

.desc {
  color: var(--color-text-secondary);
  line-height: 1.7;
  margin-bottom: 24px;
}

.price-box {
  background: var(--color-accent-soft);
  padding: 16px 20px;
  border-radius: var(--radius-sm);
  margin-bottom: 28px;
}

.price-box .label {
  font-size: 13px;
  color: var(--color-text-muted);
  display: block;
  margin-bottom: 4px;
}

.price {
  color: var(--color-accent);
  font-size: 32px;
  font-weight: 800;
}

.sku-section {
  margin-bottom: 24px;
}

.sku-section > label,
.quantity-row > label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 10px;
}

.sku-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.sku-btn {
  padding: 10px 16px;
  border: 1.5px solid var(--color-border);
  background: var(--color-surface);
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 14px;
  text-align: left;
  transition: all 0.15s;
}

.sku-btn .stock {
  display: block;
  font-size: 11px;
  color: var(--color-text-muted);
  margin-top: 2px;
}

.sku-btn.active {
  border-color: var(--color-primary);
  background: #eef2ff;
  color: var(--color-primary-dark);
}

.sku-btn.disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.quantity-row {
  margin-bottom: 28px;
}

.stepper {
  display: inline-flex;
  align-items: center;
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-sm);
  overflow: hidden;
}

.stepper button {
  width: 40px;
  height: 40px;
  border: none;
  background: #f8fafc;
  cursor: pointer;
  font-size: 18px;
  color: var(--color-text-secondary);
}

.stepper button:hover {
  background: #eef2ff;
  color: var(--color-primary);
}

.stepper span {
  width: 48px;
  text-align: center;
  font-weight: 600;
}

.btn-cart {
  width: 100%;
  padding: 14px;
  font-size: 16px;
}

.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid var(--color-border);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
  margin: 0 auto 12px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .detail-card {
    grid-template-columns: 1fr;
    padding: 20px;
  }
}
</style>
