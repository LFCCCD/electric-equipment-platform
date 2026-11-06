<template>
  <div class="page orders-page">
    <header class="orders-head">
      <span>PURCHASE ORDERS</span>
      <h1>我的订单</h1>
    </header>

    <van-tabs
      v-model:active="activeStatus"
      shrink
      color="#246b84"
      title-active-color="#1f5c73"
      title-inactive-color="#8d9ba3"
      @change="loadOrders"
    >
      <van-tab v-for="tab in tabs" :key="tab.value" :title="tab.label" :name="tab.value" />
    </van-tabs>

    <div v-if="loading" class="loading">
      <van-skeleton title :row="4" />
      <van-skeleton title :row="4" />
    </div>
    <div v-else-if="orders.length" class="order-list">
      <article
        v-for="order in orders"
        :key="order.id"
        class="order-card"
        @click="router.push(`/orders/${order.id}`)"
      >
        <div class="order-top">
          <div>
            <span>订单号</span>
            <strong>{{ order.number }}</strong>
          </div>
          <van-tag :type="statusMeta(order.status).tag" plain>
            {{ statusMeta(order.status).label }}
          </van-tag>
        </div>
        <div class="order-equipment">
          <div class="equipment-icon">{{ order.orderEquipments?.slice(0, 1) || '设' }}</div>
          <div>
            <strong>{{ order.orderEquipments?.replaceAll(';', ' · ') || '采购设备' }}</strong>
            <span>{{ order.orderTime }}</span>
          </div>
        </div>
        <div class="order-bottom">
          <span>共 {{ order.orderDetailList?.length || 1 }} 项</span>
          <div>
            <small>订单金额</small>
            <strong>¥ {{ Number(order.amount || 0).toFixed(2) }}</strong>
          </div>
        </div>
      </article>
    </div>
    <van-empty v-else description="暂无相关订单" image="search" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getOrders } from '../api'

const router = useRouter()
const loading = ref(false)
const orders = ref([])
const activeStatus = ref(0)
const tabs = [
  { value: 0, label: '全部' },
  { value: 1, label: '待付款' },
  { value: 2, label: '待确认' },
  { value: 3, label: '已确认' },
  { value: 4, label: '已发货' },
  { value: 5, label: '已完成' }
]

const statusMap = {
  1: { label: '待付款', tag: 'warning' },
  2: { label: '待确认', tag: 'warning' },
  3: { label: '已确认', tag: 'primary' },
  4: { label: '已发货', tag: 'success' },
  5: { label: '已完成', tag: 'success' },
  6: { label: '已取消', tag: 'default' }
}

function statusMeta(status) {
  return statusMap[status] || { label: '未知状态', tag: 'default' }
}

async function loadOrders() {
  loading.value = true
  try {
    const data = await getOrders({
      page: 1,
      pageSize: 50,
      status: activeStatus.value || undefined
    })
    orders.value = data.records || []
  } finally {
    loading.value = false
  }
}

onMounted(loadOrders)
</script>

<style scoped>
.orders-page {
  padding-top: 24px;
}

.orders-head span {
  color: #8498a3;
  font-size: 8px;
  font-weight: 700;
  letter-spacing: 1.8px;
}

.orders-head h1 {
  margin: 6px 0 18px;
  color: #1b394a;
  font-size: 26px;
}

.orders-page :deep(.van-tabs__wrap) {
  margin: 0 -4px 14px;
}

.orders-page :deep(.van-tab) {
  justify-content: flex-start;
}

.order-list {
  display: grid;
  gap: 12px;
  padding-bottom: 10px;
}

.order-card {
  padding: 17px;
  border: 1px solid #e4ebee;
  border-radius: 17px;
  background: #fff;
  box-shadow: 0 7px 18px rgba(37, 67, 81, 0.045);
}

.order-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.order-top span,
.order-top strong {
  display: block;
}

.order-top span {
  color: #94a1a8;
  font-size: 8px;
}

.order-top strong {
  margin-top: 5px;
  color: #2d4654;
  font-size: 12px;
}

.order-equipment {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 15px 0;
  padding: 13px;
  border-radius: 13px;
  background: #f5f8f9;
}

.equipment-icon {
  display: grid;
  place-items: center;
  flex: 0 0 auto;
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: #dcecf1;
  color: #34748b;
  font-weight: 800;
}

.order-equipment > div:last-child {
  min-width: 0;
}

.order-equipment strong {
  display: block;
  overflow: hidden;
  color: #3b5361;
  font-size: 12px;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.order-equipment span {
  display: block;
  margin-top: 6px;
  color: #9aa6ad;
  font-size: 9px;
}

.order-bottom {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
}

.order-bottom > span {
  color: #8d9ba3;
  font-size: 9px;
}

.order-bottom > div {
  text-align: right;
}

.order-bottom small,
.order-bottom strong {
  display: block;
}

.order-bottom small {
  color: #97a3aa;
  font-size: 8px;
}

.order-bottom strong {
  margin-top: 4px;
  color: #b35e35;
  font-size: 17px;
}

.loading {
  display: grid;
  gap: 16px;
  padding-top: 10px;
}
</style>
