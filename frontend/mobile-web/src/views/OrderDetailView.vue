<template>
  <div class="order-detail">
    <van-nav-bar left-arrow title="订单详情" @click-left="router.back()" />
    <template v-if="order">
      <section class="status-card">
        <div>
          <span>当前状态</span>
          <h1>{{ statusMeta(order.status).label }}</h1>
          <p>{{ statusMeta(order.status).hint }}</p>
        </div>
        <van-icon :name="statusMeta(order.status).icon" />
      </section>

      <section class="step-card">
        <van-steps :active="activeStep" active-color="#29748c" inactive-color="#a4b0b7">
          <van-step>提交订单</van-step>
          <van-step>平台确认</van-step>
          <van-step>设备发货</van-step>
          <van-step>完成交付</van-step>
        </van-steps>
      </section>

      <section class="detail-card">
        <div class="card-title"><h2>订单信息</h2></div>
        <div class="info-line"><span>订单号</span><strong>{{ order.number }}</strong></div>
        <div class="info-line"><span>下单时间</span><strong>{{ order.orderTime }}</strong></div>
        <div class="info-line"><span>采购方</span><strong>{{ order.consignee || order.userName }}</strong></div>
        <div class="info-line"><span>联系电话</span><strong>{{ order.phone }}</strong></div>
        <div class="info-line address"><span>交付地址</span><strong>{{ order.address }}</strong></div>
        <div class="info-line"><span>订单备注</span><strong>{{ order.remark || '无' }}</strong></div>
      </section>

      <section class="detail-card">
        <div class="card-title">
          <h2>设备明细</h2>
          <span>{{ order.orderDetailList?.length || 0 }} 项</span>
        </div>
        <article v-for="item in order.orderDetailList" :key="item.id" class="device-line">
          <div class="device-mark">{{ item.name?.slice(0, 1) }}</div>
          <div>
            <strong>{{ item.name }}</strong>
            <span>{{ item.selectedSpec || '标准配置' }}</span>
          </div>
          <div class="device-price">
            <strong>¥ {{ Number(item.amount || 0).toFixed(2) }}</strong>
            <span>× {{ item.number }}</span>
          </div>
        </article>
        <div class="total-line">
          <span>订单合计</span>
          <strong>¥ {{ Number(order.amount || 0).toFixed(2) }}</strong>
        </div>
      </section>

      <section v-if="order.cancelReason || order.rejectionReason" class="reason-card">
        <strong>取消原因</strong>
        <p>{{ order.cancelReason || order.rejectionReason }}</p>
      </section>

      <div v-if="order.status === 4" class="confirm-receipt">
        <van-button round block :loading="completing" @click="confirmReceipt">确认收货</van-button>
      </div>
    </template>
    <van-skeleton v-else title :row="10" class="subpage" />
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showConfirmDialog, showSuccessToast } from 'vant'
import { completeOrder, getOrderDetail } from '../api'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const completing = ref(false)

const statusMap = {
  1: { label: '待付款', hint: '订单已提交，等待完成付款。', icon: 'clock-o', step: 0 },
  2: { label: '待确认', hint: '平台正在核对价格与库存。', icon: 'todo-list-o', step: 1 },
  3: { label: '已确认', hint: '订单已确认，准备安排发货。', icon: 'passed', step: 1 },
  4: { label: '已发货', hint: '设备已经发出，请关注交付信息。', icon: 'logistics', step: 2 },
  5: { label: '已完成', hint: '本次采购已完成。', icon: 'checked', step: 3 },
  6: { label: '已取消', hint: '订单已经取消。', icon: 'close', step: 0 }
}

function statusMeta(status) {
  return statusMap[status] || { label: '未知状态', hint: '', icon: 'question-o', step: 0 }
}

const activeStep = computed(() => statusMeta(order.value?.status).step)

onMounted(async () => {
  order.value = await getOrderDetail(route.params.id)
})

async function confirmReceipt() {
  await showConfirmDialog({ title: '确认收货', message: '请确认设备已交付并核对无误。' })
  completing.value = true
  try {
    await completeOrder(order.value.id)
    order.value = await getOrderDetail(route.params.id)
    showSuccessToast('已确认收货')
  } finally {
    completing.value = false
  }
}
</script>

<style scoped>
.order-detail {
  min-height: 100vh;
  padding: 0 12px 30px;
  background: #f4f7f8;
}

.order-detail :deep(.van-nav-bar) {
  margin: 0 -12px 8px;
  background: #f4f7f8;
}

.status-card {
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  padding: 26px 22px;
  border-radius: 20px;
  background: linear-gradient(135deg, #183d51, #28738b);
  color: #fff;
}

.status-card::after {
  content: "";
  position: absolute;
  width: 150px;
  height: 150px;
  right: -80px;
  bottom: -85px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 50%;
}

.status-card span {
  color: #8dc1d0;
  font-size: 9px;
}

.status-card h1 {
  margin: 8px 0;
  font-size: 27px;
}

.status-card p {
  margin: 0;
  color: #aac4ce;
  font-size: 10px;
}

.status-card > .van-icon {
  align-self: center;
  color: rgba(255, 255, 255, 0.28);
  font-size: 56px;
}

.step-card,
.detail-card,
.reason-card {
  margin-top: 12px;
  padding: 18px;
  border: 1px solid #e4ebee;
  border-radius: 17px;
  background: #fff;
}

.step-card {
  padding: 19px 8px 13px;
}

.step-card :deep(.van-step__title) {
  font-size: 9px;
}

.card-title {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 12px;
}

.card-title h2 {
  margin: 0;
  color: #2a4352;
  font-size: 16px;
}

.card-title span {
  color: #96a3aa;
  font-size: 9px;
}

.info-line {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  padding: 9px 0;
  font-size: 11px;
}

.info-line span {
  flex: 0 0 auto;
  color: #8b99a1;
}

.info-line strong {
  color: #3b5260;
  font-weight: 500;
  text-align: right;
}

.info-line.address strong {
  line-height: 1.6;
}

.device-line {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 13px 0;
  border-bottom: 1px solid #edf1f2;
}

.device-mark {
  display: grid;
  place-items: center;
  width: 43px;
  height: 43px;
  border-radius: 12px;
  background: #e6f1f4;
  color: #34758c;
  font-weight: 800;
}

.device-line > div:nth-child(2) {
  flex: 1;
  min-width: 0;
}

.device-line strong,
.device-line span {
  display: block;
}

.device-line strong {
  overflow: hidden;
  color: #344d5b;
  font-size: 12px;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.device-line span {
  margin-top: 5px;
  color: #96a3aa;
  font-size: 9px;
}

.device-price {
  text-align: right;
}

.device-price strong {
  color: #b35e35;
}

.total-line {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  padding-top: 16px;
}

.total-line span {
  color: #6f818b;
  font-size: 12px;
}

.total-line strong {
  color: #b35e35;
  font-size: 21px;
}

.reason-card strong {
  color: #a85a39;
  font-size: 12px;
}

.reason-card p {
  margin: 8px 0 0;
  color: #7b6e68;
  font-size: 11px;
}

.confirm-receipt {
  position: sticky;
  bottom: 16px;
  margin-top: 18px;
}

.confirm-receipt :deep(.van-button) {
  height: 46px;
  border: 0;
  background: #246b84;
}
</style>
