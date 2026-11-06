<template>
  <div class="checkout-page">
    <van-nav-bar left-arrow title="确认订单" @click-left="router.back()" />

    <section class="address-card" v-if="selectedAddress">
      <div class="address-icon"><van-icon name="location-o" /></div>
      <div class="address-main">
        <div>
          <strong>{{ selectedAddress.consignee }}</strong>
          <span>{{ selectedAddress.phone }}</span>
          <van-tag v-if="selectedAddress.isDefault === 1" color="#e6f2f5" text-color="#3d7487">默认</van-tag>
        </div>
        <p>{{ addressText(selectedAddress) }}</p>
      </div>
      <van-icon name="arrow" color="#a3afb6" @click="router.push('/addresses')" />
    </section>
    <section v-else class="address-empty" @click="router.push('/addresses')">
      <van-icon name="location-o" />
      <span>请先添加交付地址</span>
      <van-icon name="arrow" />
    </section>

    <section class="order-card">
      <div class="card-title">
        <h2>采购设备</h2>
        <span>{{ cart.length }} 项</span>
      </div>
      <article v-for="item in cart" :key="item.id" class="order-line">
        <div class="line-mark">{{ item.name?.slice(0, 1) }}</div>
        <div class="line-main">
          <strong>{{ item.name }}</strong>
          <span>{{ item.selectedSpec || '标准配置' }}</span>
        </div>
        <div class="line-meta">
          <b>¥ {{ Number(item.amount || 0).toFixed(2) }}</b>
          <span>× {{ item.number }}</span>
        </div>
      </article>
    </section>

    <section class="order-card service-card">
      <van-cell title="需要安装服务" center>
        <template #right-icon>
          <van-switch v-model="needInstallation" size="21px" active-color="#2e748c" />
        </template>
      </van-cell>
      <van-cell v-if="needInstallation" title="安装数量" center>
        <template #right-icon>
          <van-stepper v-model="installationQuantity" :min="1" :max="99" integer theme="round" />
        </template>
      </van-cell>
    </section>

    <section class="order-card">
      <div class="card-title">
        <h2>采购备注</h2>
        <span>选填</span>
      </div>
      <van-field
        v-model="remark"
        type="textarea"
        maxlength="300"
        rows="3"
        autosize
        placeholder="填写交付要求、项目名称或其他说明"
      />
    </section>

    <section class="order-card amount-card">
      <div><span>设备金额</span><strong>¥ {{ total.toFixed(2) }}</strong></div>
      <div><span>服务费</span><strong>¥ 0.00</strong></div>
      <div class="amount-total"><span>应付合计</span><strong>¥ {{ total.toFixed(2) }}</strong></div>
    </section>

    <div class="bottom-action">
      <div class="bottom-action__main">
        <span>共 {{ cart.length }} 项</span>
        <strong>¥ {{ total.toFixed(2) }}</strong>
      </div>
      <van-button round :loading="submitting" @click="submit">提交并模拟支付</van-button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showDialog, showToast } from 'vant'
import { getAddresses, getCart, payOrder, submitOrder } from '../api'

const router = useRouter()
const cart = ref([])
const addresses = ref([])
const selectedAddress = ref(null)
const needInstallation = ref(false)
const installationQuantity = ref(1)
const remark = ref('')
const submitting = ref(false)

const total = computed(() => cart.value.reduce((sum, item) => sum + Number(item.amount || 0) * Number(item.number || 0), 0))

function addressText(address) {
  return [address.provinceName, address.cityName, address.districtName, address.detail].filter(Boolean).join('')
}

async function submit() {
  if (!selectedAddress.value) {
    showToast('请先选择交付地址')
    return
  }
  if (!cart.value.length) {
    showToast('购物车为空')
    return
  }
  submitting.value = true
  try {
    const result = await submitOrder({
      deliveryAddressId: selectedAddress.value.id,
      payMethod: 1,
      remark: remark.value,
      deliveryStatus: 1,
      installationQuantity: needInstallation.value ? installationQuantity.value : 0,
      installationStatus: needInstallation.value ? 1 : 0,
      serviceFee: 0,
      amount: Number(total.value.toFixed(2))
    })
    try {
      await payOrder(result.orderNumber)
    } catch {
      showToast('订单已提交，模拟支付状态更新失败')
    }
    await showDialog({
      title: '订单提交成功',
      message: `订单号：${result.orderNumber}\n请等待平台运营确认。`
    })
    router.replace('/orders')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  const [cartData, addressData] = await Promise.all([getCart(), getAddresses()])
  cart.value = cartData || []
  addresses.value = addressData || []
  selectedAddress.value = addresses.value.find((item) => item.isDefault === 1) || addresses.value[0] || null
})
</script>

<style scoped>
.checkout-page {
  min-height: 100vh;
  padding: 0 12px 108px;
  background: #f4f7f8;
}

.checkout-page :deep(.van-nav-bar) {
  margin: 0 -12px 8px;
  background: #f4f7f8;
}

.address-card,
.address-empty {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 8px;
  padding: 18px;
  border-radius: 17px;
  background: #fff;
  border: 1px solid #e4ebee;
}

.address-icon {
  display: grid;
  place-items: center;
  flex: 0 0 auto;
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: #e5f1f4;
  color: #307188;
  font-size: 20px;
}

.address-main {
  flex: 1;
  min-width: 0;
}

.address-main > div {
  display: flex;
  align-items: center;
  gap: 8px;
}

.address-main strong {
  color: #263f4e;
}

.address-main span {
  color: #81929b;
  font-size: 10px;
}

.address-main p {
  margin: 8px 0 0;
  color: #72838c;
  font-size: 11px;
  line-height: 1.6;
}

.address-empty {
  justify-content: center;
  color: #748891;
  font-size: 12px;
}

.address-empty .van-icon:first-child {
  font-size: 18px;
}

.address-empty .van-icon:last-child {
  margin-left: auto;
}

.order-card {
  margin-top: 12px;
  padding: 18px;
  border-radius: 17px;
  background: #fff;
  border: 1px solid #e4ebee;
}

.card-title {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 12px;
}

.card-title h2 {
  margin: 0;
  color: #283f4e;
  font-size: 16px;
}

.card-title span {
  color: #96a3aa;
  font-size: 9px;
}

.order-line {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 13px 0;
  border-bottom: 1px solid #edf1f2;
}

.line-mark {
  display: grid;
  place-items: center;
  width: 43px;
  height: 43px;
  border-radius: 12px;
  background: #e7f1f4;
  color: #33758c;
  font-weight: 800;
}

.line-main {
  flex: 1;
  min-width: 0;
}

.line-main strong,
.line-main span,
.line-meta b,
.line-meta span {
  display: block;
}

.line-main strong {
  overflow: hidden;
  color: #304a58;
  font-size: 12px;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.line-main span {
  overflow: hidden;
  margin-top: 5px;
  color: #98a4ab;
  font-size: 9px;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.line-meta {
  text-align: right;
}

.line-meta b {
  color: #b35e35;
  font-size: 12px;
}

.line-meta span {
  margin-top: 5px;
  color: #8b9aa2;
  font-size: 9px;
}

.service-card {
  padding: 4px 0;
}

.service-card :deep(.van-cell) {
  padding: 15px 18px;
}

.order-card :deep(.van-field) {
  padding: 0;
  background: transparent;
}

.amount-card > div {
  display: flex;
  justify-content: space-between;
  padding: 7px 0;
  color: #6c7f89;
  font-size: 12px;
}

.amount-card strong {
  color: #3b5260;
}

.amount-card .amount-total {
  margin-top: 8px;
  padding-top: 14px;
  border-top: 1px solid #e9eef0;
  color: #263f4e;
  font-weight: 700;
}

.amount-card .amount-total strong {
  color: #b35e35;
  font-size: 18px;
}

.bottom-action span {
  display: block;
  color: #8f9da5;
  font-size: 9px;
}

.bottom-action strong {
  display: block;
  margin-top: 4px;
  color: #b35e35;
  font-size: 19px;
}
</style>
