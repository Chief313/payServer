<template>
  <section class="ad-banner">
    <div class="ad-track" :style="{ transform: `translateX(-${currentIndex * 100}%)` }">
      <div
        v-for="(slide, index) in slides"
        :key="index"
        class="ad-slide"
        :style="{ background: slide.bg }"
      >
        <div class="slide-inner">
          <span class="slide-tag">{{ slide.tag }}</span>
          <h2 class="slide-title">{{ slide.title }}</h2>
          <p class="slide-desc">{{ slide.desc }}</p>
        </div>
        <span class="slide-deco">{{ slide.emoji }}</span>
      </div>
    </div>
    <div class="ad-dots">
      <button
        v-for="(_, index) in slides"
        :key="index"
        :class="['dot', { active: currentIndex === index }]"
        @click="goTo(index)"
      />
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

/** 轮播广告数据 */
const slides = [
  {
    tag: '品质精选',
    title: '发现品质好物',
    desc: '精选全品类商品，满足你的每一次心动',
    emoji: '🛍️',
    bg: 'linear-gradient(120deg, #4f46e5 0%, #7c3aed 50%, #a855f7 100%)',
  },
  {
    tag: '智能服务',
    title: 'AI 客服随时在线',
    desc: '7×24 小时智能解答，购物咨询一键搞定',
    emoji: '💬',
    bg: 'linear-gradient(120deg, #0ea5e9 0%, #6366f1 50%, #8b5cf6 100%)',
  },
  {
    tag: '限时福利',
    title: '新品上市 · 限时优惠',
    desc: '数码家电爆款直降，每日上新不容错过',
    emoji: '🔥',
    bg: 'linear-gradient(120deg, #f43f5e 0%, #ec4899 50%, #a855f7 100%)',
  },
  {
    tag: '安心购物',
    title: '正品保障 · 放心选购',
    desc: '支付宝安全支付，订单全程可追踪',
    emoji: '✨',
    bg: 'linear-gradient(120deg, #059669 0%, #0d9488 50%, #0891b2 100%)',
  },
]

const currentIndex = ref(0)
let timer = null

/** 切换到指定广告页 */
function goTo(index) {
  currentIndex.value = index
}

/** 自动切换下一页 */
function next() {
  currentIndex.value = (currentIndex.value + 1) % slides.length
}

onMounted(() => {
  timer = setInterval(next, 4500)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.ad-banner {
  position: relative;
  overflow: hidden;
  border-radius: var(--radius-md);
  margin-bottom: 24px;
  box-shadow: var(--shadow-md);
}

.ad-track {
  display: flex;
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.ad-slide {
  min-width: 100%;
  height: 160px;
  position: relative;
  display: flex;
  align-items: center;
  overflow: hidden;
}

.slide-inner {
  position: relative;
  z-index: 1;
  padding: 0 32px;
  color: #fff;
}

.slide-tag {
  display: inline-block;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.05em;
  background: rgba(255, 255, 255, 0.2);
  padding: 3px 10px;
  border-radius: var(--radius-full);
  margin-bottom: 10px;
}

.slide-title {
  font-size: 26px;
  font-weight: 800;
  margin-bottom: 6px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.slide-desc {
  font-size: 14px;
  opacity: 0.9;
}

.slide-deco {
  position: absolute;
  right: 24px;
  font-size: 72px;
  opacity: 0.25;
  transform: rotate(-12deg);
}

.ad-dots {
  position: absolute;
  bottom: 12px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 6px;
  z-index: 2;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.4);
  cursor: pointer;
  padding: 0;
  transition: background 0.2s, transform 0.2s;
}

.dot.active {
  background: #fff;
  transform: scale(1.2);
}

@media (max-width: 768px) {
  .ad-slide {
    height: 140px;
  }
  .slide-title {
    font-size: 20px;
  }
  .slide-deco {
    font-size: 48px;
    right: 12px;
  }
}
</style>
