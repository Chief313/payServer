import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { public: true, hideLayout: true },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { public: true, hideLayout: true },
  },
  {
    path: '/',
    component: () => import('../layout/UserLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('../views/Home.vue') },
      { path: 'product/:id', name: 'ProductDetail', component: () => import('../views/ProductDetail.vue') },
      { path: 'cart', name: 'Cart', component: () => import('../views/Cart.vue'), meta: { requireAuth: true } },
      { path: 'orders', name: 'OrderList', component: () => import('../views/OrderList.vue'), meta: { requireAuth: true } },
      { path: 'pay/:orderNo', name: 'OrderPay', component: () => import('../views/OrderPay.vue'), meta: { requireAuth: true } },
      { path: 'chat', name: 'Chat', component: () => import('../views/Chat.vue') },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  const token = localStorage.getItem('user_token')
  if (to.meta.requireAuth && !token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if ((to.path === '/login' || to.path === '/register') && token) {
    return '/'
  }
})

export default router
