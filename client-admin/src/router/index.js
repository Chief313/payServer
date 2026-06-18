import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { public: true },
  },
  {
    path: '/',
    component: () => import('../layout/AdminLayout.vue'),
    redirect: '/home',
    children: [
      { path: 'home', name: 'Home', component: () => import('../views/Home.vue') },
      { path: 'categories', name: 'Categories', component: () => import('../views/CategoryManage.vue') },
      { path: 'products', name: 'Products', component: () => import('../views/ProductManage.vue') },
      { path: 'orders', name: 'Orders', component: () => import('../views/OrderManage.vue') },
      { path: 'users', name: 'Users', component: () => import('../views/UserManage.vue') },
      { path: 'knowledge', name: 'Knowledge', component: () => import('../views/KnowledgeManage.vue') },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  const token = localStorage.getItem('admin_token')
  if (!to.meta.public && !token) {
    return '/login'
  }
  if (to.path === '/login' && token) {
    return '/home'
  }
})

export default router
