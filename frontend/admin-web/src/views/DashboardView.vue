<template>
  <div class="dashboard">
    <div class="page-heading">
      <div>
        <h1>运营工作台</h1>
        <p>设备目录、套餐状态与今日订单集中展示。</p>
      </div>
      <el-button :loading="loading" @click="loadDashboard">刷新数据</el-button>
    </div>

    <section class="hero page-card">
      <div>
        <span class="hero__eyebrow">TODAY'S OPERATIONS</span>
        <h2>{{ greeting }}，{{ adminUser.name || '平台管理员' }}</h2>
        <p>今天需要优先处理 {{ orderOverview.waitingOrders || 0 }} 笔待确认订单，并关注设备库存和套餐状态。</p>
      </div>
      <div class="hero__date">
        <strong>{{ today }}</strong>
        <span>数据按今日统计</span>
      </div>
    </section>

    <section class="metric-grid">
      <article class="metric page-card">
        <span class="metric__icon blue"><el-icon><Money /></el-icon></span>
        <div>
          <p>今日成交金额</p>
          <strong>¥ {{ money(businessData.turnover) }}</strong>
          <small>有效订单 {{ businessData.validOrderCount || 0 }} 笔</small>
        </div>
      </article>
      <article class="metric page-card">
        <span class="metric__icon amber"><el-icon><Tickets /></el-icon></span>
        <div>
          <p>待确认订单</p>
          <strong>{{ orderOverview.waitingOrders || 0 }}</strong>
          <small>共 {{ orderOverview.allOrders || 0 }} 笔订单</small>
        </div>
      </article>
      <article class="metric page-card">
        <span class="metric__icon green"><el-icon><Box /></el-icon></span>
        <div>
          <p>在售设备</p>
          <strong>{{ equipmentOverview.sold || 0 }}</strong>
          <small>下架 {{ equipmentOverview.discontinued || 0 }} 台</small>
        </div>
      </article>
      <article class="metric page-card">
        <span class="metric__icon violet"><el-icon><Collection /></el-icon></span>
        <div>
          <p>在售套餐</p>
          <strong>{{ packageOverview.sold || 0 }}</strong>
          <small>下架 {{ packageOverview.discontinued || 0 }} 套</small>
        </div>
      </article>
    </section>

    <section class="dashboard-grid">
      <article class="page-card panel">
        <div class="panel__heading">
          <div>
            <span>订单状态</span>
            <h3>履约概况</h3>
          </div>
          <el-button text @click="router.push('/orders')">查看订单</el-button>
        </div>
        <div class="order-progress">
          <div>
            <span>待确认</span>
            <strong>{{ orderOverview.waitingOrders || 0 }}</strong>
          </div>
          <div>
            <span>待发货</span>
            <strong>{{ orderOverview.deliveredOrders || 0 }}</strong>
          </div>
          <div>
            <span>已完成</span>
            <strong>{{ orderOverview.completedOrders || 0 }}</strong>
          </div>
          <div>
            <span>已取消</span>
            <strong>{{ orderOverview.cancelledOrders || 0 }}</strong>
          </div>
        </div>
        <div class="finish-rate">
          <div>
            <span>订单完成率</span>
            <strong>{{ percent(businessData.orderCompletionRate) }}</strong>
          </div>
          <el-progress
            :percentage="Math.min(100, Math.round((businessData.orderCompletionRate || 0) * 100))"
            :show-text="false"
            :stroke-width="9"
            color="#3c8ca6"
          />
        </div>
      </article>

      <article class="page-card panel quick-panel">
        <div class="panel__heading">
          <div>
            <span>快捷入口</span>
            <h3>常用操作</h3>
          </div>
        </div>
        <div class="quick-grid">
          <button @click="router.push('/equipment')">
            <el-icon><Plus /></el-icon>
            <span>维护设备</span>
          </button>
          <button @click="router.push('/packages')">
            <el-icon><Goods /></el-icon>
            <span>调整套餐</span>
          </button>
          <button @click="router.push('/orders')">
            <el-icon><Van /></el-icon>
            <span>处理发货</span>
          </button>
          <button @click="loadDashboard">
            <el-icon><Refresh /></el-icon>
            <span>刷新概览</span>
          </button>
        </div>
      </article>

      <article class="page-card panel insight">
        <div class="panel__heading">
          <div>
            <span>今日经营</span>
            <h3>客单与用户</h3>
          </div>
        </div>
        <div class="insight__row">
          <div>
            <span>平均客单价</span>
            <strong>¥ {{ money(businessData.unitPrice) }}</strong>
          </div>
          <div>
            <span>新增采购用户</span>
            <strong>{{ businessData.newUsers || 0 }}</strong>
          </div>
        </div>
        <p>数据用于演示后台运营视角。接入正式业务后，应补充趋势图、库存预警和交付时效统计。</p>
      </article>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Box, Collection, Goods, Money, Plus, Refresh, Tickets, Van } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getDashboard } from '../api'
import { getAdminUser } from '../utils/session'

const router = useRouter()
const adminUser = getAdminUser()
const loading = ref(false)
const businessData = reactive({})
const orderOverview = reactive({})
const equipmentOverview = reactive({})
const packageOverview = reactive({})

const today = new Intl.DateTimeFormat('zh-CN', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  weekday: 'long'
}).format(new Date())

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 12) return '上午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

function money(value) {
  return Number(value || 0).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function percent(value) {
  return `${(Number(value || 0) * 100).toFixed(1)}%`
}

async function loadDashboard() {
  loading.value = true
  try {
    const [business, orders, equipment, packages] = await getDashboard()
    Object.assign(businessData, business || {})
    Object.assign(orderOverview, orders || {})
    Object.assign(equipmentOverview, equipment || {})
    Object.assign(packageOverview, packages || {})
  } catch {
    ElMessage.warning('部分概览数据加载失败，请检查后端与数据库连接')
  } finally {
    loading.value = false
  }
}

onMounted(loadDashboard)
</script>

<style scoped>
.hero {
  display: flex;
  justify-content: space-between;
  min-height: 150px;
  padding: 30px 32px;
  color: #fff;
  background:
    radial-gradient(circle at 88% 25%, rgba(122, 213, 229, 0.22), transparent 22%),
    linear-gradient(115deg, #17394e, #1f6179);
}

.hero__eyebrow {
  color: #8cd2e3;
  font-size: 9px;
  font-weight: 800;
  letter-spacing: 2px;
}

.hero h2 {
  margin: 11px 0 8px;
  font-size: 25px;
}

.hero p {
  margin: 0;
  color: #b7d0da;
  font-size: 13px;
}

.hero__date {
  align-self: flex-end;
  text-align: right;
}

.hero__date strong,
.hero__date span {
  display: block;
}

.hero__date strong {
  font-size: 15px;
}

.hero__date span {
  margin-top: 6px;
  color: #96b8c6;
  font-size: 11px;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-top: 18px;
}

.metric {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 22px;
}

.metric__icon {
  display: grid;
  place-items: center;
  width: 48px;
  height: 48px;
  border-radius: 14px;
  font-size: 21px;
}

.blue { background: #e5f1f5; color: #2e7893; }
.amber { background: #fff1df; color: #b16c24; }
.green { background: #e8f4ed; color: #3b8160; }
.violet { background: #eeeaf8; color: #6a58a3; }

.metric p,
.metric strong,
.metric small {
  display: block;
}

.metric p {
  margin: 0 0 6px;
  color: #7b8b96;
  font-size: 12px;
}

.metric strong {
  color: #1c3545;
  font-size: 24px;
}

.metric small {
  margin-top: 6px;
  color: #a0adb5;
  font-size: 10px;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1.35fr 0.8fr 0.85fr;
  gap: 16px;
  margin-top: 18px;
}

.panel {
  padding: 24px;
}

.panel__heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 20px;
}

.panel__heading span {
  color: #91a0a9;
  font-size: 10px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.panel__heading h3 {
  margin: 5px 0 0;
  color: #203847;
  font-size: 17px;
}

.order-progress {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.order-progress > div {
  padding: 16px;
  border-radius: 12px;
  background: #f5f8f9;
}

.order-progress span,
.order-progress strong {
  display: block;
}

.order-progress span {
  color: #84949e;
  font-size: 11px;
}

.order-progress strong {
  margin-top: 10px;
  color: #284456;
  font-size: 24px;
}

.finish-rate {
  margin-top: 24px;
}

.finish-rate > div {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  color: #70818c;
  font-size: 12px;
}

.quick-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.quick-grid button {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 14px;
  padding: 18px;
  border: 1px solid #e5ecef;
  border-radius: 12px;
  background: #f8fafb;
  color: #3e6274;
  cursor: pointer;
  transition: 0.2s ease;
}

.quick-grid button:hover {
  transform: translateY(-2px);
  border-color: #9fc8d4;
  background: #eff7f9;
}

.quick-grid .el-icon {
  font-size: 22px;
}

.quick-grid span {
  font-size: 12px;
}

.insight__row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.insight__row > div {
  padding: 17px;
  border-radius: 12px;
  background: #f5f8f9;
}

.insight__row span,
.insight__row strong {
  display: block;
}

.insight__row span {
  color: #8796a0;
  font-size: 11px;
}

.insight__row strong {
  margin-top: 10px;
  color: #244456;
  font-size: 21px;
}

.insight p {
  margin: 22px 0 0;
  color: #84939c;
  font-size: 11px;
  line-height: 1.8;
}
</style>
