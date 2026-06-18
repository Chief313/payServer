<template>
  <div class="user-layout">
    <header class="navbar">
      <div class="nav-inner">
        <router-link to="/" class="brand">
          <span class="brand-icon">🛍️</span>
          <span class="brand-text">PayServer</span>
        </router-link>
        <nav class="nav-links">
          <router-link to="/">
            <span class="nav-icon">🏠</span>首页
          </router-link>
          <router-link to="/cart">
            <span class="nav-icon">🛒</span>购物车
          </router-link>
          <router-link to="/orders">
            <span class="nav-icon">📋</span>订单
          </router-link>
          <router-link to="/chat">
            <span class="nav-icon">💬</span>客服
          </router-link>
        </nav>

        <!-- 搜索栏（与导航合并，固定宽度） -->
        <div class="search-wrap">
          <div class="search-field">
            <span class="search-icon">🔍</span>
            <input
              v-model="searchInput"
              type="text"
              placeholder="搜索商品..."
              @keyup.enter="handleSearch"
            />
            <button v-if="searchInput" class="clear-btn" type="button" @click="clearSearch">×</button>
          </div>
          <button class="search-submit" type="button" @click="handleSearch">搜索</button>
        </div>
        <div class="nav-right">
          <template v-if="userStore.isLoggedIn">
            <div class="user-badge">
              <span class="avatar">{{ avatarLetter }}</span>
              <span class="user-name">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
            </div>
            <button class="btn-ghost" @click="userStore.logout()">退出</button>
          </template>
          <template v-else>
            <router-link to="/login" class="btn-ghost">登录</router-link>
            <router-link to="/register" class="btn-primary-sm">注册</router-link>
          </template>
        </div>
      </div>
    </header>

    <main class="content">
      <router-view />
    </main>
    <footer class="site-footer">
      <p>© 2025 PayServer 商城 · AI 智能客服驱动</p>
    </footer>
    <ChatWidget />
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useSearchStore } from '../stores/search'
import ChatWidget from '../components/ChatWidget.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const searchStore = useSearchStore()

const searchInput = ref(searchStore.keyword)

const avatarLetter = computed(() => {
  const name = userStore.userInfo?.nickname || userStore.userInfo?.username || '?'
  return name.charAt(0).toUpperCase()
})

/** 执行搜索，非首页时先跳转首页 */
async function handleSearch() {
  searchStore.applySearch(searchInput.value)
  if (route.path !== '/') {
    await router.push('/')
  }
}

/** 清空搜索 */
async function clearSearch() {
  searchInput.value = ''
  searchStore.clear()
  if (route.path !== '/') {
    await router.push('/')
  }
}

watch(
  () => searchStore.keyword,
  (val) => {
    searchInput.value = val
  },
)
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--color-bg);
}

.navbar {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--color-border);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 56px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 6px;
  text-decoration: none;
  flex-shrink: 0;
}

.brand-icon {
  font-size: 20px;
}

.brand-text {
  font-size: 17px;
  font-weight: 800;
  background: linear-gradient(135deg, var(--color-primary), #a855f7);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.search-wrap {
  display: flex;
  align-items: center;
  gap: 6px;
  width: 200px;
  flex-shrink: 0;
}

.search-field {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 6px;
  background: #f1f5f9;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-full);
  padding: 0 10px;
  height: 32px;
  min-width: 0;
  transition: border-color 0.15s, box-shadow 0.15s;
}

.search-field:focus-within {
  border-color: var(--color-primary-light);
  box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.1);
  background: #fff;
}

.search-icon {
  font-size: 12px;
  opacity: 0.5;
  flex-shrink: 0;
}

.search-field input {
  flex: 1;
  border: none;
  background: transparent;
  outline: none;
  font-size: 13px;
  color: var(--color-text);
  min-width: 0;
  width: 0;
}

.search-field input::placeholder {
  color: var(--color-text-muted);
}

.clear-btn {
  border: none;
  background: #cbd5e1;
  color: #fff;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 12px;
  line-height: 1;
  flex-shrink: 0;
}

.search-submit {
  height: 32px;
  padding: 0 10px;
  border: none;
  border-radius: var(--radius-full);
  background: var(--color-primary);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
  flex-shrink: 0;
  transition: background 0.15s;
}

.search-submit:hover {
  background: var(--color-primary-dark);
}

.nav-links {
  display: flex;
  gap: 4px;
  flex: 1;
  justify-content: flex-start;
  min-width: 0;
}

.nav-links a {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--color-text-secondary);
  text-decoration: none;
  font-size: 13px;
  font-weight: 500;
  padding: 6px 10px;
  border-radius: var(--radius-sm);
  transition: background 0.15s, color 0.15s;
  white-space: nowrap;
}

.nav-links a:hover {
  background: #f1f5f9;
  color: var(--color-text);
}

.nav-links a.router-link-active {
  background: #eef2ff;
  color: var(--color-primary);
  font-weight: 600;
}

.nav-icon {
  font-size: 14px;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.user-badge {
  display: flex;
  align-items: center;
  gap: 6px;
}

.avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary), #a855f7);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
}

.user-name {
  color: var(--color-text-secondary);
  font-size: 13px;
}

.btn-ghost {
  color: var(--color-text-secondary);
  background: none;
  border: none;
  cursor: pointer;
  font-size: 13px;
  padding: 6px 8px;
  border-radius: var(--radius-sm);
  text-decoration: none;
  transition: background 0.15s;
}

.btn-ghost:hover {
  background: #f1f5f9;
}

.btn-primary-sm {
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  color: #fff;
  padding: 6px 14px;
  border-radius: var(--radius-sm);
  text-decoration: none;
  font-size: 13px;
  font-weight: 600;
}

.content {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 20px 20px 40px;
}

.site-footer {
  text-align: center;
  padding: 20px;
  color: var(--color-text-muted);
  font-size: 13px;
  border-top: 1px solid var(--color-border);
  background: var(--color-surface);
}

@media (max-width: 900px) {
  .search-wrap {
    width: 160px;
  }
  .search-submit {
    display: none;
  }
}

@media (max-width: 768px) {
  .brand-text {
    display: none;
  }
  .search-wrap {
    flex: 1;
    width: auto;
    max-width: 160px;
  }
  .user-name {
    display: none;
  }
  .nav-links a span:not(.nav-icon) {
    display: none;
  }
}
</style>
