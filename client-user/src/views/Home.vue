<template>
  <div class="home">
    <!-- 滚动广告轮播 -->
    <AdBanner />

    <div class="main-layout">
      <!-- 左侧分类 -->
      <aside class="category-sidebar">
        <h3 class="sidebar-title">商品分类</h3>
        <ul class="category-tree">
          <li
            :class="{ active: selectedCategoryId === null }"
            @click="selectCategory(null)"
          >
            <span class="cat-icon">🏪</span>
            <span>全部商品</span>
          </li>
          <li
            v-for="parent in parentCategories"
            :key="parent.id"
            class="parent-item"
          >
            <div
              :class="['parent-row', { active: selectedCategoryId === parent.id, expanded: expandedIds.has(parent.id) }]"
              @click="toggleParent(parent)"
            >
              <span class="cat-icon">{{ getCategoryIcon(parent.name) }}</span>
              <span class="cat-name">{{ parent.name }}</span>
              <span v-if="getChildren(parent.id).length" class="arrow">›</span>
            </div>
            <ul v-if="expandedIds.has(parent.id)" class="child-list">
              <li
                v-for="child in getChildren(parent.id)"
                :key="child.id"
                :class="{ active: selectedCategoryId === child.id }"
                @click.stop="selectCategory(child.id)"
              >
                {{ child.name }}
              </li>
            </ul>
          </li>
        </ul>
      </aside>

      <!-- 右侧商品区 -->
      <section class="product-area">
        <div class="area-header">
          <h2>{{ currentCategoryName }}</h2>
          <span class="count">共 {{ total }} 件商品</span>
        </div>

        <!-- 全部分类时的快捷入口 -->
        <div v-if="selectedCategoryId === null && parentCategories.length" class="category-chips">
          <button
            :class="['chip', { active: selectedCategoryId === null }]"
            @click="selectCategory(null)"
          >
            全部
          </button>
          <button
            v-for="parent in parentCategories"
            :key="parent.id"
            :class="['chip', { active: selectedCategoryId === parent.id }]"
            @click="selectCategory(parent.id)"
          >
            {{ getCategoryIcon(parent.name) }} {{ parent.name }}
          </button>
        </div>

        <!-- 选中某一级分类后的子分类标签 -->
        <div v-else-if="activeParent" class="category-chips">
          <button
            :class="['chip', { active: selectedCategoryId === activeParent.id }]"
            @click="selectCategory(activeParent.id)"
          >
            全部{{ activeParent.name }}
          </button>
          <button
            v-for="child in getChildren(activeParent.id)"
            :key="child.id"
            :class="['chip', { active: selectedCategoryId === child.id }]"
            @click="selectCategory(child.id)"
          >
            {{ child.name }}
          </button>
        </div>

        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <p>加载中...</p>
        </div>
        <div v-else-if="filteredProducts.length === 0" class="empty-state">
          <p>暂无相关商品</p>
          <button class="btn-outline" @click="selectCategory(null)">查看全部</button>
        </div>
        <div v-else class="product-grid">
          <ProductCard
            v-for="item in filteredProducts"
            :key="item.id"
            :product="item"
          />
        </div>

        <div v-if="total > size" class="pagination-bar">
          <button :disabled="page <= 1" @click="changePage(page - 1)">上一页</button>
          <span>{{ page }} / {{ totalPages }}</span>
          <button :disabled="page >= totalPages" @click="changePage(page + 1)">下一页</button>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { getProducts, getCategories } from '../api/product'
import { getCategoryIcon } from '../utils/placeholder'
import { useSearchStore } from '../stores/search'
import ProductCard from '../components/ProductCard.vue'
import AdBanner from '../components/AdBanner.vue'

const searchStore = useSearchStore()

const loading = ref(false)
const products = ref([])
const categories = ref([])
const page = ref(1)
const size = ref(24)
const total = ref(0)
const selectedCategoryId = ref(null)
const expandedIds = ref(new Set())
const keyword = ref(searchStore.keyword)

const totalPages = computed(() => Math.ceil(total.value / size.value) || 1)

const parentCategories = computed(() =>
  categories.value.filter((c) => !c.parentId || Number(c.parentId) === 0),
)

const activeParent = computed(() => {
  if (selectedCategoryId.value === null) return null
  const cat = categories.value.find((c) => c.id === selectedCategoryId.value)
  if (!cat) return null
  if (!cat.parentId || Number(cat.parentId) === 0) return cat
  return categories.value.find((c) => c.id === cat.parentId) || null
})

const currentCategoryName = computed(() => {
  if (selectedCategoryId.value === null) return '全部商品'
  const cat = categories.value.find((c) => c.id === selectedCategoryId.value)
  return cat?.name || '全部商品'
})

/** 前端关键词过滤（在当前页结果上） */
const filteredProducts = computed(() => {
  if (!keyword.value.trim()) return products.value
  const kw = keyword.value.trim().toLowerCase()
  return products.value.filter(
    (p) => p.name?.toLowerCase().includes(kw) || p.description?.toLowerCase().includes(kw),
  )
})

function getChildren(parentId) {
  return categories.value.filter((c) => Number(c.parentId) === Number(parentId))
}

function toggleParent(parent) {
  const children = getChildren(parent.id)
  const next = new Set(expandedIds.value)
  if (children.length) {
    if (next.has(parent.id)) next.delete(parent.id)
    else next.add(parent.id)
    expandedIds.value = next
  }
  selectCategory(parent.id)
}

function selectCategory(id) {
  selectedCategoryId.value = id
  page.value = 1
  keyword.value = ''
  searchStore.resetKeyword()
  fetchProducts()
}

function applyKeywordFromStore() {
  keyword.value = searchStore.keyword
  page.value = 1
  fetchProducts()
}

async function fetchCategories() {
  try {
    categories.value = await getCategories()
    // 默认展开所有一级分类，便于看到子分类
    if (parentCategories.value.length) {
      expandedIds.value = new Set(parentCategories.value.map((c) => c.id))
    }
  } catch (e) {
    console.error('加载分类失败', e)
    categories.value = []
  }
}

async function fetchProducts() {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (selectedCategoryId.value !== null) {
      params.categoryId = selectedCategoryId.value
    }
    const res = await getProducts(params)
    products.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function changePage(p) {
  page.value = p
  fetchProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(async () => {
  keyword.value = searchStore.keyword
  await fetchCategories()
  await fetchProducts()
})

watch(() => searchStore.tick, applyKeywordFromStore)
</script>

<style scoped>
.home {
  width: 100%;
}

.main-layout {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 0 40px;
  display: grid;
  grid-template-columns: 220px 1fr;
  gap: 24px;
}

.category-sidebar {
  background: var(--color-surface);
  border-radius: var(--radius-md);
  padding: 16px 0;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--color-border);
  height: fit-content;
  position: sticky;
  top: 72px;
}

.sidebar-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--color-text-secondary);
  padding: 0 16px 12px;
  border-bottom: 1px solid var(--color-border);
  margin-bottom: 8px;
}

.category-tree {
  list-style: none;
}

.category-tree > li {
  cursor: pointer;
  font-size: 14px;
  color: var(--color-text-secondary);
  padding: 10px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: background 0.15s, color 0.15s;
}

.category-tree > li:hover,
.category-tree > li.active {
  background: #eef2ff;
  color: var(--color-primary);
  font-weight: 600;
}

.parent-item {
  flex-direction: column;
  align-items: stretch !important;
  padding: 0 !important;
}

.parent-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  transition: background 0.15s;
}

.parent-row:hover,
.parent-row.active {
  background: #eef2ff;
  color: var(--color-primary);
  font-weight: 600;
}

.cat-icon {
  font-size: 16px;
}

.cat-name {
  flex: 1;
}

.arrow {
  transition: transform 0.2s;
  color: var(--color-text-muted);
}

.parent-row.expanded .arrow {
  transform: rotate(90deg);
}

.child-list {
  list-style: none;
  background: #f8fafc;
  padding: 4px 0;
}

.child-list li {
  padding: 8px 16px 8px 40px;
  font-size: 13px;
  color: var(--color-text-secondary);
  transition: background 0.15s, color 0.15s;
}

.child-list li:hover,
.child-list li.active {
  background: #e0e7ff;
  color: var(--color-primary-dark);
  font-weight: 500;
}

.product-area {
  min-width: 0;
}

.area-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 16px;
}

.area-header h2 {
  font-size: 20px;
  font-weight: 700;
}

.count {
  font-size: 13px;
  color: var(--color-text-muted);
}

.category-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.chip {
  padding: 6px 16px;
  border-radius: var(--radius-full);
  border: 1px solid var(--color-border);
  background: var(--color-surface);
  font-size: 13px;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.15s;
}

.chip:hover {
  border-color: var(--color-primary-light);
  color: var(--color-primary);
}

.chip.active {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: #fff;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 18px;
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
  .main-layout {
    grid-template-columns: 1fr;
  }
  .category-sidebar {
    position: static;
  }
  .category-chips {
    overflow-x: auto;
    flex-wrap: nowrap;
    padding-bottom: 4px;
  }
}
</style>
