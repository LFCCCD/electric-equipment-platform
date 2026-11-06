<template>
  <div class="page profile-page">
    <section class="profile-hero">
      <div class="avatar">{{ userName.slice(0, 1) }}</div>
      <div>
        <span>PROCUREMENT ACCOUNT</span>
        <h1>{{ userName }}</h1>
        <p>{{ buyerUser.username || 'buyer' }}</p>
      </div>
    </section>

    <section class="profile-stats">
      <div>
        <strong>{{ orderCount }}</strong>
        <span>采购订单</span>
      </div>
      <div>
        <strong>{{ addressCount }}</strong>
        <span>交付地址</span>
      </div>
      <div>
        <strong>V1.0</strong>
        <span>账号版本</span>
      </div>
    </section>

    <van-cell-group inset class="menu-card">
      <van-cell title="交付地址" label="管理项目收货地址" is-link to="/addresses" icon="location-o" />
      <van-cell title="我的订单" label="查看采购与履约进度" is-link to="/orders" icon="orders-o" />
      <van-cell title="购物车" label="继续完成未提交的设备采购" is-link to="/cart" icon="shopping-cart-o" />
    </van-cell-group>

    <section class="about-card">
      <span>电力设备采购平台</span>
      <p>Spring Boot + Vue 3 + Vant H5 演示端。SQL Server 兼容与在线支付接入列在后续版本。</p>
    </section>

    <van-button class="logout-btn" block round plain @click="logout">退出登录</van-button>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getAddresses, getOrders } from '../api'
import { clearSession, getBuyerUser } from '../utils/session'

const router = useRouter()
const buyerUser = getBuyerUser()
const userName = computed(() => buyerUser.name || '企业采购员')
const orderCount = ref(0)
const addressCount = ref(0)

function logout() {
  clearSession()
  router.replace('/login')
}

onMounted(async () => {
  const [orders, addresses] = await Promise.all([
    getOrders({ page: 1, pageSize: 1 }),
    getAddresses()
  ])
  orderCount.value = orders.total || 0
  addressCount.value = addresses.length || 0
})
</script>

<style scoped>
.profile-page {
  padding-top: 28px;
}

.profile-hero {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 26px 22px;
  border-radius: 21px;
  background:
    radial-gradient(circle at 90% 10%, rgba(126, 209, 225, 0.25), transparent 26%),
    linear-gradient(135deg, #173c50, #276d84);
  color: #fff;
}

.avatar {
  display: grid;
  place-items: center;
  width: 62px;
  height: 62px;
  border-radius: 19px;
  background: rgba(255, 255, 255, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.14);
  font-size: 24px;
  font-weight: 800;
}

.profile-hero span {
  color: #82bdcc;
  font-size: 8px;
  letter-spacing: 1.5px;
}

.profile-hero h1 {
  margin: 8px 0 5px;
  font-size: 23px;
}

.profile-hero p {
  margin: 0;
  color: #9fbdc8;
  font-size: 10px;
}

.profile-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  margin-top: 14px;
  padding: 18px 10px;
  border: 1px solid #e4ebee;
  border-radius: 17px;
  background: #fff;
}

.profile-stats div {
  text-align: center;
  border-right: 1px solid #edf1f2;
}

.profile-stats div:last-child {
  border-right: 0;
}

.profile-stats strong,
.profile-stats span {
  display: block;
}

.profile-stats strong {
  color: #284656;
  font-size: 19px;
}

.profile-stats span {
  margin-top: 6px;
  color: #8998a1;
  font-size: 9px;
}

.menu-card {
  margin-top: 14px;
  overflow: hidden;
  border-radius: 17px;
  border: 1px solid #e4ebee;
}

.menu-card :deep(.van-cell) {
  padding: 17px 16px;
}

.menu-card :deep(.van-cell__left-icon) {
  margin-right: 12px;
  color: #3b7d93;
  font-size: 19px;
}

.menu-card :deep(.van-cell__title span),
.menu-card :deep(.van-cell__title) {
  color: #2f4857;
}

.menu-card :deep(.van-cell__label) {
  color: #96a2a9;
  font-size: 9px;
}

.about-card {
  margin-top: 14px;
  padding: 20px;
  border-radius: 17px;
  background: #eef4f6;
}

.about-card span {
  color: #345b6c;
  font-size: 13px;
  font-weight: 700;
}

.about-card p {
  margin: 8px 0 0;
  color: #7d8e97;
  font-size: 10px;
  line-height: 1.8;
}

.logout-btn {
  margin-top: 18px;
  border-color: #d9e3e7;
  color: #768892;
}
</style>
