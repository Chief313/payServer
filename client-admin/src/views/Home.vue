<template>
  <div class="dashboard" v-loading="loading">
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6" v-for="card in statCards" :key="card.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">{{ card.label }}</div>
          <div class="stat-value" :style="{ color: card.color }">{{ card.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>近7日订单趋势</span></template>
          <v-chart class="chart" :option="orderTrendOption" autoresize />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>近7日销售额趋势</span></template>
          <v-chart class="chart" :option="salesTrendOption" autoresize />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header><span>商品销量 Top5</span></template>
          <v-chart class="chart chart-wide" :option="topProductsOption" autoresize />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getDashboardStats } from '../api/dashboard'

use([CanvasRenderer, LineChart, BarChart, GridComponent, TooltipComponent, LegendComponent])

const loading = ref(false)
const stats = ref(null)

const statCards = computed(() => {
  const s = stats.value
  if (!s) return []
  return [
    { label: '用户总数', value: s.totalUsers, color: '#409eff' },
    { label: '订单总数', value: s.totalOrders, color: '#67c23a' },
    { label: '销售总额', value: `¥${Number(s.totalSales || 0).toFixed(2)}`, color: '#e6a23c' },
    { label: '今日订单', value: s.todayOrders, color: '#f56c6c' },
  ]
})

const orderTrendOption = computed(() => buildLineOption('订单数', stats.value?.orderTrend))
const salesTrendOption = computed(() => buildLineOption('销售额', stats.value?.salesTrend))
const topProductsOption = computed(() => {
  const items = stats.value?.topProducts || []
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value' },
    yAxis: {
      type: 'category',
      data: items.map((i) => i.name).reverse(),
      axisLabel: { width: 120, overflow: 'truncate' },
    },
    series: [
      {
        name: '销量',
        type: 'bar',
        data: items.map((i) => Number(i.value)).reverse(),
        itemStyle: { color: '#409eff' },
      },
    ],
  }
})

function buildLineOption(name, trend) {
  const items = trend || []
  return {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: items.map((i) => i.date) },
    yAxis: { type: 'value' },
    series: [
      {
        name,
        type: 'line',
        smooth: true,
        data: items.map((i) => Number(i.value)),
        areaStyle: { opacity: 0.15 },
        itemStyle: { color: '#409eff' },
      },
    ],
  }
}

onMounted(async () => {
  loading.value = true
  try {
    stats.value = await getDashboardStats()
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.dashboard {
  padding: 4px;
}

.stat-row {
  margin-bottom: 16px;
}

.stat-card {
  text-align: center;
}

.stat-label {
  color: #909399;
  font-size: 14px;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
}

.chart-row {
  margin-bottom: 16px;
}

.chart {
  height: 320px;
}

.chart-wide {
  height: 360px;
}
</style>
