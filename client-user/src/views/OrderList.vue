<template>
  <div class="orders-page">
    <h2 class="page-title">我的订单</h2>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="orders.length === 0" class="empty">暂无订单</div>
    <div v-else class="order-list">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <span>订单号：{{ order.orderNo }}</span>
          <span class="status">{{ statusLabel(order.status) }}</span>
        </div>
        <div class="order-body">
          <p>金额：¥{{ order.payAmount || order.totalAmount }}</p>
          <p class="time">创建时间：{{ order.createTime }}</p>
        </div>
        <div class="order-footer">
          <button
            v-if="order.status === 'PENDING_PAY'"
            class="btn-pay"
            @click="router.push(`/pay/${order.orderNo}`)"
          >
            去支付
          </button>
        </div>
      </div>
    </div>
    <div v-if="total > size" class="pagination">
      <button :disabled="page <= 1" @click="changePage(page - 1)">上一页</button>
      <span>{{ page }} / {{ totalPages }}</span>
      <button :disabled="page >= totalPages" @click="changePage(page + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrders } from '../api/order'

const router = useRouter()
const loading = ref(false)
const orders = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

const totalPages = computed(() => Math.ceil(total.value / size.value) || 1)

const statusMap = {
  PENDING_PAY: '待支付',
  PAID: '已支付',
  SHIPPED: '已发货',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
}

function statusLabel(s) {
  return statusMap[s] || s
}

async function fetchOrders() {
  loading.value = true
  try {
    const res = await getOrders({ page: page.value, size: size.value })
    orders.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function changePage(p) {
  page.value = p
  fetchOrders()
}

onMounted(fetchOrders)
</script>

<style scoped>
.page-title {
  margin-bottom: 20px;
  font-size: 22px;
}

.loading, .empty {
  text-align: center;
  padding: 60px;
  color: #999;
}

.order-card {
  background: #fff;
  border-radius: 8px;
  margin-bottom: 12px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  padding: 12px 20px;
  background: #fafafa;
  font-size: 14px;
}

.status {
  color: #e4393c;
  font-weight: 600;
}

.order-body {
  padding: 16px 20px;
  color: #666;
}

.time {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}

.order-footer {
  padding: 0 20px 16px;
  text-align: right;
}

.btn-pay {
  background: #e4393c;
  color: #fff;
  border: none;
  padding: 8px 24px;
  border-radius: 4px;
  cursor: pointer;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
}

.pagination button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
