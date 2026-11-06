import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '../utils/session'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: () => import('../views/LoginView.vue'), meta: { public: true, title: '登录' } },
    { path: '/', component: () => import('../views/HomeView.vue'), meta: { title: '采购首页', showTab: true } },
    { path: '/equipment/:id', component: () => import('../views/EquipmentDetailView.vue'), meta: { title: '设备详情' } },
    { path: '/package/:id', component: () => import('../views/PackageDetailView.vue'), meta: { title: '套餐详情' } },
    { path: '/cart', component: () => import('../views/CartView.vue'), meta: { title: '购物车', showTab: true } },
    { path: '/checkout', component: () => import('../views/CheckoutView.vue'), meta: { title: '确认订单' } },
    { path: '/orders', component: () => import('../views/OrdersView.vue'), meta: { title: '我的订单', showTab: true } },
    { path: '/orders/:id', component: () => import('../views/OrderDetailView.vue'), meta: { title: '订单详情' } },
    { path: '/addresses', component: () => import('../views/AddressView.vue'), meta: { title: '交付地址' } },
    { path: '/profile', component: () => import('../views/ProfileView.vue'), meta: { title: '我的', showTab: true } },
    { path: '/:pathMatch(.*)*', redirect: '/' }
  ]
})

router.beforeEach((to) => {
  document.title = `${to.meta.title || '电力设备采购'} - 电力设备采购平台`
  if (!to.meta.public && !getToken()) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.path === '/login' && getToken()) {
    return '/'
  }
  return true
})

export default router
