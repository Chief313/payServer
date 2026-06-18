<template>
  <div class="page">
    <el-card shadow="never">
      <div class="toolbar">
        <el-select v-model="statusFilter" placeholder="订单状态" clearable style="width: 160px" @change="fetchList">
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" min-width="180" />
        <el-table-column prop="userId" label="用户ID" width="90" />
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="payAmount" label="实付金额" width="120">
          <template #default="{ row }">¥{{ row.payAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'PAID'"
              link
              type="primary"
              @click="handleShip(row)"
            >
              发货
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrders, shipOrder } from '../api/order'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const statusFilter = ref('')

const statusOptions = [
  { value: 'PENDING_PAY', label: '待支付' },
  { value: 'PAID', label: '已支付' },
  { value: 'SHIPPED', label: '已发货' },
  { value: 'COMPLETED', label: '已完成' },
  { value: 'CANCELLED', label: '已取消' },
]

const statusMap = Object.fromEntries(statusOptions.map((s) => [s.value, s.label]))

function statusLabel(status) {
  return statusMap[status] || status
}

function statusTagType(status) {
  const map = {
    PENDING_PAY: 'warning',
    PAID: 'success',
    SHIPPED: '',
    COMPLETED: 'success',
    CANCELLED: 'info',
  }
  return map[status] || 'info'
}

async function fetchList() {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (statusFilter.value) params.status = statusFilter.value
    const res = await getOrders(params)
    tableData.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

async function handleShip(row) {
  await ElMessageBox.confirm(`确定对订单 ${row.orderNo} 发货吗？`, '发货确认', { type: 'info' })
  await shipOrder(row.id)
  ElMessage.success('发货成功')
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.page {
  padding: 4px;
}

.toolbar {
  margin-bottom: 16px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
