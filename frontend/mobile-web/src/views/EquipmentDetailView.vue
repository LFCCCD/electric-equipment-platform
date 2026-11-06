<template>
  <div class="detail-page" v-if="equipment">
    <van-nav-bar left-arrow title="设备详情" @click-left="router.back()" />

    <section class="visual">
      <img v-if="equipment.image" :src="equipment.image" alt="" />
      <div v-else class="visual__mark">{{ equipment.name?.slice(0, 1) }}</div>
      <span class="visual__category">{{ equipment.categoryName || '电力设备' }}</span>
    </section>

    <section class="info-card">
      <div class="title-row">
        <div>
          <span>{{ equipment.brand || '平台认证设备' }}</span>
          <h1>{{ equipment.name }}</h1>
        </div>
        <van-tag :type="equipment.status === 1 ? 'success' : 'default'" round>
          {{ equipment.status === 1 ? '在售' : '下架' }}
        </van-tag>
      </div>
      <p class="code">{{ equipment.code }} · {{ equipment.model || '标准型号' }}</p>
      <div class="price-line">
        <div class="price"><small>¥</small> {{ Number(equipment.price || 0).toLocaleString('zh-CN') }}</div>
        <span>/ {{ equipment.unit || '台' }}</span>
        <span class="stock">库存 {{ equipment.stockQuantity || 0 }}</span>
      </div>
      <p class="description">{{ equipment.description || '暂无设备说明' }}</p>
    </section>

    <section class="block">
      <div class="block-title">
        <h2>技术规格</h2>
        <span>以供货确认参数为准</span>
      </div>
      <div v-if="equipment.specs?.length" class="spec-table">
        <div v-for="spec in equipment.specs" :key="spec.id">
          <span>{{ spec.name }}</span>
          <strong>{{ spec.value }}</strong>
        </div>
      </div>
      <van-empty v-else description="暂无规格参数" image-size="66" />
    </section>

    <section class="block purchase">
      <div class="block-title">
        <h2>采购数量</h2>
        <span>当前库存 {{ equipment.stockQuantity || 0 }} {{ equipment.unit || '台' }}</span>
      </div>
      <van-stepper
        v-model="quantity"
        :min="1"
        :max="Math.max(1, Number(equipment.stockQuantity || 1))"
        integer
        theme="round"
      />
    </section>

    <div class="bottom-action">
      <div class="bottom-action__main">
        <span>合计</span>
        <strong>¥ {{ (Number(equipment.price || 0) * quantity).toLocaleString('zh-CN') }}</strong>
      </div>
      <van-button round icon="shopping-cart-o" @click="addToCart" :loading="adding">
        加入购物车
      </van-button>
    </div>
  </div>
  <div v-else class="subpage">
    <van-skeleton title :row="8" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showSuccessToast, showToast } from 'vant'
import { addCart, getEquipmentDetail } from '../api'

const route = useRoute()
const router = useRouter()
const equipment = ref(null)
const quantity = ref(1)
const adding = ref(false)

async function addToCart() {
  if (!equipment.value || equipment.value.status !== 1) {
    showToast('设备当前不可采购')
    return
  }
  adding.value = true
  try {
    const selectedSpec = (equipment.value.specs || [])
      .map((item) => `${item.name}:${item.value}`)
      .join(' / ')
    for (let index = 0; index < quantity.value; index += 1) {
      await addCart({ equipmentId: equipment.value.id, selectedSpec })
    }
    showSuccessToast('已加入购物车')
  } finally {
    adding.value = false
  }
}

onMounted(async () => {
  equipment.value = await getEquipmentDetail(route.params.id)
})
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  padding-bottom: 92px;
}

.detail-page :deep(.van-nav-bar) {
  position: sticky;
  z-index: 10;
  top: 0;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(10px);
}

.visual {
  position: relative;
  display: grid;
  place-items: center;
  height: 250px;
  overflow: hidden;
  background:
    radial-gradient(circle at 75% 20%, rgba(255, 255, 255, 0.55), transparent 25%),
    linear-gradient(135deg, #d1e8ee, #7fb3c3);
}

.visual::after {
  content: "";
  position: absolute;
  width: 210px;
  height: 210px;
  right: -90px;
  bottom: -120px;
  border: 1px solid rgba(255, 255, 255, 0.45);
  border-radius: 50%;
}

.visual img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.visual__mark {
  color: rgba(26, 88, 108, 0.35);
  font-size: 96px;
  font-weight: 900;
}

.visual__category {
  position: absolute;
  left: 18px;
  bottom: 16px;
  padding: 5px 10px;
  border-radius: 7px;
  background: rgba(24, 85, 105, 0.78);
  color: #fff;
  font-size: 10px;
}

.info-card,
.block {
  margin: 14px 16px 0;
  padding: 20px;
  border: 1px solid #e6ecef;
  border-radius: 18px;
  background: #fff;
}

.info-card {
  margin-top: -18px;
  position: relative;
  z-index: 2;
  box-shadow: 0 12px 30px rgba(32, 67, 82, 0.1);
}

.title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.title-row span {
  color: #7b909b;
  font-size: 10px;
}

.title-row h1 {
  margin: 7px 0 0;
  color: #1d3949;
  font-size: 22px;
  line-height: 1.4;
}

.code {
  margin: 8px 0 16px;
  color: #91a0a8;
  font-size: 10px;
}

.price-line {
  display: flex;
  align-items: baseline;
  gap: 5px;
}

.price-line .price {
  font-size: 25px;
}

.price-line > span {
  color: #95a1a8;
  font-size: 10px;
}

.price-line .stock {
  margin-left: auto;
  color: #4f7d8d;
}

.description {
  margin: 16px 0 0;
  padding-top: 16px;
  border-top: 1px solid #edf1f3;
  color: #697b85;
  font-size: 12px;
  line-height: 1.8;
}

.block-title {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 14px;
}

.block-title h2 {
  margin: 0;
  color: #263f4e;
  font-size: 17px;
}

.block-title span {
  color: #96a2a9;
  font-size: 9px;
}

.spec-table > div {
  display: grid;
  grid-template-columns: 38% 62%;
  padding: 12px 0;
  border-bottom: 1px solid #eef2f3;
  font-size: 12px;
}

.spec-table span {
  color: #8d9aa2;
}

.spec-table strong {
  color: #425b68;
  font-weight: 500;
}

.purchase {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.purchase .block-title {
  display: block;
  margin: 0;
}

.purchase .block-title span {
  display: block;
  margin-top: 7px;
}

.bottom-action span {
  display: block;
  color: #8c9aa2;
  font-size: 9px;
}

.bottom-action strong {
  display: block;
  margin-top: 4px;
  color: #b45d34;
  font-size: 19px;
}
</style>
