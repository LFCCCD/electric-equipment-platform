<template>
  <div class="package-detail" v-if="packageInfo">
    <van-nav-bar left-arrow title="套餐详情" @click-left="router.back()" />

    <section class="package-hero">
      <span>{{ packageInfo.categoryName || '标准采购套餐' }}</span>
      <h1>{{ packageInfo.name }}</h1>
      <p>{{ packageInfo.description || '设备组合采购方案' }}</p>
      <div class="package-price">
        <small>套餐价</small>
        <strong>¥ {{ Number(packageInfo.price || 0).toLocaleString('zh-CN') }}</strong>
      </div>
    </section>

    <section class="package-card-list">
      <div class="section-title">
        <h2>套餐包含设备</h2>
        <span>{{ packageInfo.packageEquipments?.length || 0 }} 项</span>
      </div>
      <article v-for="item in packageInfo.packageEquipments" :key="item.id" class="package-line">
        <div class="line-mark">{{ item.name?.slice(0, 1) }}</div>
        <div>
          <strong>{{ item.name }}</strong>
          <span>参考单价 ¥ {{ Number(item.price || 0).toFixed(2) }}</span>
        </div>
        <b>× {{ item.quantity }}</b>
      </article>
      <van-empty v-if="!packageInfo.packageEquipments?.length" description="暂无套餐明细" image-size="70" />
    </section>

    <section class="notice">
      <van-icon name="info-o" />
      <p>套餐价格以提交订单时确认结果为准，实际供货清单以运营人员确认为准。</p>
    </section>

    <div class="bottom-action">
      <div class="bottom-action__main">
        <span>套餐合计</span>
        <strong>¥ {{ Number(packageInfo.price || 0).toLocaleString('zh-CN') }}</strong>
      </div>
      <van-button round @click="addPackage" :loading="adding">加入购物车</van-button>
    </div>
  </div>
  <div v-else class="subpage">
    <van-skeleton title :row="8" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showSuccessToast } from 'vant'
import { addCart, getPackageDetail } from '../api'

const route = useRoute()
const router = useRouter()
const packageInfo = ref(null)
const adding = ref(false)

async function addPackage() {
  adding.value = true
  try {
    await addCart({ packageId: packageInfo.value.id })
    showSuccessToast('套餐已加入购物车')
  } finally {
    adding.value = false
  }
}

onMounted(async () => {
  packageInfo.value = await getPackageDetail(route.params.id)
})
</script>

<style scoped>
.package-detail {
  min-height: 100vh;
  padding-bottom: 96px;
  background: #f4f7f8;
}

.package-detail :deep(.van-nav-bar) {
  background: #f4f7f8;
}

.package-hero {
  position: relative;
  overflow: hidden;
  margin: 8px 16px 0;
  padding: 30px 24px;
  border-radius: 22px;
  background:
    radial-gradient(circle at 90% 10%, rgba(130, 215, 231, 0.3), transparent 28%),
    linear-gradient(135deg, #183c50, #286f87);
  color: #fff;
}

.package-hero::after {
  content: "EP";
  position: absolute;
  right: 18px;
  bottom: -24px;
  color: rgba(255, 255, 255, 0.08);
  font-size: 100px;
  font-weight: 900;
}

.package-hero > span {
  color: #82c4d4;
  font-size: 9px;
}

.package-hero h1 {
  position: relative;
  z-index: 1;
  margin: 12px 0 10px;
  font-size: 27px;
  line-height: 1.35;
}

.package-hero p {
  position: relative;
  z-index: 1;
  max-width: 82%;
  margin: 0;
  color: #a7c1cb;
  font-size: 11px;
  line-height: 1.7;
}

.package-price {
  position: relative;
  z-index: 1;
  margin-top: 30px;
}

.package-price small {
  display: block;
  margin-bottom: 4px;
  color: #a8c6d1;
  font-size: 9px;
}

.package-price strong {
  color: #fff1de;
  font-size: 27px;
}

.package-card-list {
  margin: 16px;
  padding: 20px;
  border: 1px solid #e4ebee;
  border-radius: 18px;
  background: #fff;
}

.package-line {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid #edf1f2;
}

.line-mark {
  display: grid;
  place-items: center;
  flex: 0 0 auto;
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: #e5f1f4;
  color: #2e7087;
  font-weight: 800;
}

.package-line > div:nth-child(2) {
  flex: 1;
}

.package-line strong,
.package-line span {
  display: block;
}

.package-line strong {
  color: #304a59;
  font-size: 13px;
}

.package-line span {
  margin-top: 5px;
  color: #95a2a9;
  font-size: 9px;
}

.package-line b {
  color: #566d78;
  font-size: 12px;
}

.notice {
  display: flex;
  gap: 10px;
  margin: 0 20px;
  color: #85949d;
  font-size: 10px;
  line-height: 1.7;
}

.notice p {
  margin: 0;
}

.bottom-action span {
  display: block;
  color: #8e9ba3;
  font-size: 9px;
}

.bottom-action strong {
  display: block;
  margin-top: 4px;
  color: #b45e34;
  font-size: 19px;
}
</style>
