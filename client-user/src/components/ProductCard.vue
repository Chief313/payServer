<template>
  <router-link :to="`/product/${product.id}`" class="product-card">
    <div class="cover-wrap">
      <img :src="coverSrc" :alt="product.name" loading="lazy" @error="onImgError" />
      <span v-if="product.status === 1" class="badge">热卖</span>
    </div>
    <div class="info">
      <h3 class="name">{{ product.name }}</h3>
      <p class="desc">{{ product.description }}</p>
      <div class="footer">
        <span class="price">¥{{ product.price }}</span>
        <span class="buy-hint">查看详情 →</span>
      </div>
    </div>
  </router-link>
</template>

<script setup>
import { ref, computed } from 'vue'
import { productPlaceholder } from '../utils/placeholder'

const props = defineProps({
  product: { type: Object, required: true },
})

const fallback = productPlaceholder(props.product.id)

const coverSrc = computed(() => {
  const url = props.product.coverUrl
  if (!url || url.includes('/pic/demo/')) return fallback
  return url
})

function onImgError(e) {
  e.target.src = fallback
}
</script>

<style scoped>
.product-card {
  display: flex;
  flex-direction: column;
  background: var(--color-surface);
  border-radius: var(--radius-md);
  overflow: hidden;
  border: 1px solid var(--color-border);
  transition: transform 0.25s, box-shadow 0.25s, border-color 0.25s;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: transparent;
}

.cover-wrap {
  position: relative;
  height: 220px;
  background: linear-gradient(145deg, #f1f5f9, #e2e8f0);
  overflow: hidden;
}

.cover-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.35s;
}

.product-card:hover .cover-wrap img {
  transform: scale(1.05);
}

.badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: linear-gradient(135deg, #f43f5e, #e11d48);
  color: #fff;
  font-size: 11px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: var(--radius-full);
}

.info {
  padding: 16px 18px 18px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.name {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 6px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.desc {
  font-size: 13px;
  color: var(--color-text-muted);
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.price {
  color: var(--color-accent);
  font-size: 20px;
  font-weight: 700;
}

.price::before {
  content: '';
}

.buy-hint {
  font-size: 12px;
  color: var(--color-primary);
  opacity: 0;
  transition: opacity 0.2s;
}

.product-card:hover .buy-hint {
  opacity: 1;
}
</style>
