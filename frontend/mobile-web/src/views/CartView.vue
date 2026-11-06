<template>
  <div class="page cart-page">
    <header class="cart-head">
      <div>
        <span>PURCHASE CART</span>
        <h1>购物车</h1>
      </div>
      <button v-if="cart.length" @click="clearAll">清空</button>
    </header>

    <div v-if="loading" class="loading">
      <van-skeleton title :row="3" />
      <van-skeleton title :row="3" />
    </div>

    <template v-else-if="cart.length">
      <section class="cart-summary">
        <div>
          <span>已选设备</span>
          <strong>{{ cart.length }}</strong>
        </div>
        <div>
          <span>采购合计</span>
          <strong>¥ {{ total.toLocaleString('zh-CN', { minimumFractionDigits: 2 }) }}</strong>
        </div>
      </section>

      <section class="cart-list">
        <article v-for="item in cart" :key="item.id" class="cart-item">
          <div class="cart-thumb">{{ item.name?.slice(0, 1) }}</div>
          <div class="cart-main">
            <div class="cart-title">
              <strong>{{ item.name }}</strong>
              <span>{{ item.packageId ? '设备套餐' : '标准设备' }}</span>
            </div>
            <p>{{ item.selectedSpec || '标准配置' }}</p>
            <div class="cart-foot">
              <div class="price"><small>¥</small> {{ Number(item.amount || 0).toFixed(2) }}</div>
              <van-stepper
                :model-value="item.number"
                :min="1"
                integer
                theme="round"
                button-size="25"
                @plus="addOne(item)"
                @minus="subOne(item)"
              />
            </div>
          </div>
        </article>
      </section>
    </template>

    <van-empty v-else description="购物车还是空的" image="search">
      <van-button round type="primary" size="small" @click="router.push('/')">去选设备</van-button>
    </van-empty>

    <div v-if="cart.length" class="checkout-bar">
      <div>
        <span>合计</span>
        <strong>¥ {{ total.toLocaleString('zh-CN', { minimumFractionDigits: 2 }) }}</strong>
      </div>
      <van-button round @click="router.push('/checkout')">去结算</van-button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog, showSuccessToast } from 'vant'
import { addCart, cleanCart, getCart, subtractCart } from '../api'

const router = useRouter()
const loading = ref(false)
const cart = ref([])
const total = computed(() => cart.value.reduce((sum, item) => sum + Number(item.amount || 0) * Number(item.number || 0), 0))

function payload(item) {
  return {
    equipmentId: item.equipmentId,
    packageId: item.packageId,
    selectedSpec: item.selectedSpec
  }
}

async function load() {
  loading.value = true
  try {
    cart.value = await getCart()
  } finally {
    loading.value = false
  }
}

async function addOne(item) {
  await addCart(payload(item))
  await load()
}

async function subOne(item) {
  await subtractCart(payload(item))
  await load()
}

async function clearAll() {
  await showConfirmDialog({ title: '清空购物车', message: '确定移除全部采购设备吗？' })
  await cleanCart()
  cart.value = []
  showSuccessToast('购物车已清空')
}

onMounted(load)
</script>

<style scoped>
.cart-page {
  padding-top: 24px;
  padding-bottom: 150px;
}

.cart-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
}

.cart-head span {
  color: #8498a3;
  font-size: 8px;
  font-weight: 700;
  letter-spacing: 1.8px;
}

.cart-head h1 {
  margin: 6px 0 0;
  color: #1c394a;
  font-size: 26px;
}

.cart-head button {
  border: 0;
  background: transparent;
  color: #8b989f;
  font-size: 11px;
}

.cart-summary {
  display: grid;
  grid-template-columns: 1fr 1.4fr;
  gap: 10px;
  margin-top: 20px;
}

.cart-summary > div {
  padding: 16px;
  border-radius: 14px;
  background: #fff;
  border: 1px solid #e7edef;
}

.cart-summary span,
.cart-summary strong {
  display: block;
}

.cart-summary span {
  color: #8d9ba4;
  font-size: 9px;
}

.cart-summary strong {
  margin-top: 7px;
  color: #2b4a5b;
  font-size: 19px;
}

.cart-summary > div:last-child strong {
  color: #b55f35;
}

.cart-list {
  display: grid;
  gap: 12px;
  margin-top: 16px;
}

.cart-item {
  display: flex;
  gap: 13px;
  padding: 15px;
  border: 1px solid #e7edef;
  border-radius: 17px;
  background: #fff;
}

.cart-thumb {
  display: grid;
  place-items: center;
  flex: 0 0 auto;
  width: 78px;
  height: 78px;
  border-radius: 15px;
  background:
    radial-gradient(circle at 75% 20%, rgba(255, 255, 255, 0.65), transparent 26%),
    linear-gradient(135deg, #d4e8ee, #8ebac8);
  color: rgba(30, 88, 106, 0.5);
  font-size: 30px;
  font-weight: 800;
}

.cart-main {
  flex: 1;
  min-width: 0;
}

.cart-title {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
}

.cart-title strong {
  overflow: hidden;
  color: #294452;
  font-size: 14px;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.cart-title span {
  flex: 0 0 auto;
  padding: 3px 6px;
  border-radius: 5px;
  background: #edf4f6;
  color: #5b7b88;
  font-size: 8px;
}

.cart-main p {
  overflow: hidden;
  margin: 7px 0 13px;
  color: #91a0a8;
  font-size: 9px;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.cart-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.checkout-bar {
  position: fixed;
  z-index: 15;
  left: 12px;
  right: 12px;
  bottom: calc(64px + env(safe-area-inset-bottom));
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 14px;
  border: 1px solid rgba(224, 233, 236, 0.9);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.97);
  box-shadow: 0 10px 26px rgba(25, 58, 73, 0.13);
}

.checkout-bar > div {
  flex: 1;
}

.checkout-bar span,
.checkout-bar strong {
  display: block;
}

.checkout-bar span {
  color: #8d9aa2;
  font-size: 9px;
}

.checkout-bar strong {
  margin-top: 3px;
  color: #b45e35;
  font-size: 19px;
}

.checkout-bar :deep(.van-button) {
  min-width: 104px;
  border: 0;
  background: #1f657e;
}

.loading {
  display: grid;
  gap: 16px;
  margin-top: 24px;
}
</style>
