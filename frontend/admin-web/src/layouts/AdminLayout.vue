<template>
  <div class="admin-shell">
    <aside class="sidebar">
      <div class="brand">
        <div class="brand__mark">EP</div>
        <div>
          <strong>电力设备采购</strong>
          <span>OPERATIONS CONSOLE</span>
        </div>
      </div>

      <el-menu
        :default-active="route.path"
        router
        class="sidebar-menu"
        background-color="transparent"
        text-color="#b9c9d5"
        active-text-color="#ffffff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>运营工作台</span>
        </el-menu-item>
        <el-menu-item index="/equipment">
          <el-icon><Box /></el-icon>
          <span>设备管理</span>
        </el-menu-item>
        <el-menu-item index="/packages">
          <el-icon><Collection /></el-icon>
          <span>套餐管理</span>
        </el-menu-item>
        <el-menu-item index="/orders">
          <el-icon><Tickets /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar__footer">
        <span>Spring Boot 3</span>
        <span>MySQL 8</span>
        <span>Redis 7</span>
      </div>
    </aside>

    <main class="main">
      <header class="topbar">
        <div>
          <span class="topbar__eyebrow">ELECTRIC EQUIPMENT PROCUREMENT</span>
          <h2>{{ route.meta.title }}</h2>
        </div>
        <div class="topbar__right">
          <div class="operator">
            <span class="operator__avatar">{{ avatarText }}</span>
            <div>
              <strong>{{ adminUser.name || '平台管理员' }}</strong>
              <small>{{ adminUser.userName || 'admin' }}</small>
            </div>
          </div>
          <el-button text class="logout" @click="logout">
            <el-icon><SwitchButton /></el-icon>
            退出
          </el-button>
        </div>
      </header>

      <section class="content">
        <router-view />
      </section>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Box, Collection, DataAnalysis, SwitchButton, Tickets } from '@element-plus/icons-vue'
import { clearSession, getAdminUser } from '../utils/session'

const route = useRoute()
const router = useRouter()
const adminUser = getAdminUser()
const avatarText = computed(() => (adminUser.name || '管').slice(0, 1))

function logout() {
  clearSession()
  router.replace('/login')
}
</script>

<style scoped>
.admin-shell {
  display: grid;
  grid-template-columns: 246px minmax(0, 1fr);
  min-height: 100vh;
}

.sidebar {
  position: fixed;
  inset: 0 auto 0 0;
  width: 246px;
  display: flex;
  flex-direction: column;
  padding: 28px 18px 22px;
  background:
    radial-gradient(circle at 30% 0%, rgba(82, 174, 201, 0.25), transparent 30%),
    linear-gradient(175deg, #173347 0%, #102735 100%);
  color: #fff;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 12px 26px;
}

.brand__mark {
  display: grid;
  place-items: center;
  width: 44px;
  height: 44px;
  border-radius: 13px;
  background: linear-gradient(135deg, #5eb4cf, #2b7e9e);
  font-weight: 800;
  letter-spacing: 1px;
  box-shadow: 0 10px 24px rgba(25, 108, 136, 0.35);
}

.brand strong {
  display: block;
  font-size: 15px;
}

.brand span {
  display: block;
  margin-top: 4px;
  font-size: 9px;
  letter-spacing: 1.4px;
  color: #85a8ba;
}

.sidebar-menu {
  border-right: 0;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 48px;
  margin: 6px 0;
  border-radius: 12px;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.08);
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(100deg, rgba(66, 154, 182, 0.75), rgba(43, 108, 134, 0.4));
  box-shadow: inset 3px 0 0 #84d2e5;
}

.sidebar__footer {
  margin-top: auto;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  padding: 18px 8px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.sidebar__footer span {
  padding: 4px 7px;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.06);
  color: #8fabb9;
  font-size: 10px;
}

.main {
  grid-column: 2;
  min-width: 0;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 92px;
  padding: 0 34px;
  background: rgba(248, 251, 252, 0.82);
  border-bottom: 1px solid #e6ecef;
  backdrop-filter: blur(14px);
}

.topbar__eyebrow {
  color: #8a9aa5;
  font-size: 9px;
  font-weight: 700;
  letter-spacing: 1.8px;
}

.topbar h2 {
  margin: 5px 0 0;
  color: #173045;
  font-size: 20px;
}

.topbar__right,
.operator {
  display: flex;
  align-items: center;
}

.topbar__right {
  gap: 18px;
}

.operator {
  gap: 10px;
  padding-right: 18px;
  border-right: 1px solid #dfe7eb;
}

.operator__avatar {
  display: grid;
  place-items: center;
  width: 38px;
  height: 38px;
  border-radius: 12px;
  background: #e4eff3;
  color: #245b73;
  font-weight: 800;
}

.operator strong,
.operator small {
  display: block;
}

.operator strong {
  color: #263b49;
  font-size: 13px;
}

.operator small {
  margin-top: 3px;
  color: #90a0aa;
}

.logout {
  color: #768692;
}

.content {
  padding: 28px 34px 40px;
}
</style>
