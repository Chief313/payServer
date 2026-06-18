import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * 全局商品搜索状态
 * 供顶部搜索栏与首页商品列表联动
 */
export const useSearchStore = defineStore('search', () => {
  const keyword = ref('')
  const tick = ref(0)

  /**
   * 触发搜索
   * @param {string} kw 关键词
   */
  function applySearch(kw) {
    keyword.value = (kw || '').trim()
    tick.value++
  }

  /** 清空搜索关键词并通知首页刷新 */
  function clear() {
    keyword.value = ''
    tick.value++
  }

  /** 仅清空关键词，不触发刷新（切换分类时使用） */
  function resetKeyword() {
    keyword.value = ''
  }

  return { keyword, tick, applySearch, clear, resetKeyword }
})
