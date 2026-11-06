import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '../utils/session'
import AdminLayout from '../layouts/AdminLayout.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { public: true, title: '运营登录' }
    },
    {
      path: '/',
      component: AdminLayout,
      redirect: '/dashboard',
      children: [
        { path: 'dashboard', name: 'dashboard', component: () => import('../views/DashboardView.vue'), meta: { title: '运营工作台' } },
        { path: 'equipment', name: 'equipment', component: () => import('../views/EquipmentView.vue'), meta: { title: '设备管理' } },
        { path: 'packages', name: 'packages', component: () => import('../views/PackageView.vue'), meta: { title: '套餐管理' } },
        { path: 'orders', name: 'orders', component: () => import('../views/OrderView.vue'), meta: { title: '订单管理' } }
      ]
    },
    { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
  ]
})

router.beforeEach((to) => {
  document.title = `${to.meta.title || '运营后台'} - 电力设备采购平台`
  if (!to.meta.public && !getToken()) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.name === 'login' && getToken()) {
    return { name: 'dashboard' }
  }
  return true
})

export default router
