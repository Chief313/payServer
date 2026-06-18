<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="aside">
      <div class="logo">PayServer 管理后台</div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#001529"
        text-color="#ffffffa6"
        active-text-color="#fff"
      >
        <el-menu-item index="/home">首页概览</el-menu-item>
        <el-menu-item index="/categories">分类管理</el-menu-item>
        <el-menu-item index="/products">商品管理</el-menu-item>
        <el-menu-item index="/orders">订单管理</el-menu-item>
        <el-menu-item index="/users">用户管理</el-menu-item>
        <el-menu-item index="/knowledge">知识库管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="header-title">{{ pageTitle }}</span>
        <div class="header-right">
          <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
          <el-button type="danger" link @click="userStore.logout()">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'

const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const titles = {
  '/home': '首页概览',
  '/categories': '分类管理',
  '/products': '商品管理',
  '/orders': '订单管理',
  '/users': '用户管理',
  '/knowledge': '知识库管理',
}

const pageTitle = computed(() => titles[route.path] || '管理后台')
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}

.aside {
  background: #001529;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-weight: 600;
  font-size: 15px;
  border-bottom: 1px solid #ffffff1a;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #eee;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username {
  color: #666;
}

.main {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}
</style>
