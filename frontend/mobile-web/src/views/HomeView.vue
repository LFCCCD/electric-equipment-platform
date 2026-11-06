<template>
  <div class="page home">
    <header class="home-head">
      <div>
        <span class="eyebrow">ELECTRIC EQUIPMENT</span>
        <h1>电力设备采购</h1>
      </div>
      <div class="head-mark">EP</div>
    </header>

    <section class="hero">
      <div class="hero__copy">
        <span>企业标准设备采购</span>
        <h2>设备选型<br />一步完成</h2>
        <p>浏览参数、加入购物车并提交采购订单</p>
      </div>
      <div class="hero__visual">
        <span>10kV</span>
        <strong>EP</strong>
      </div>
    </section>

    <van-search
      v-model="keyword"
      shape="round"
      background="transparent"
      placeholder="搜索设备名称、型号或品牌"
      class="search"
    />

    <section class="package-section">
      <div class="section-title">
        <h2>标准采购套餐</h2>
        <span>组合采购更高效</span>
      </div>
      <div v-if="packages.length" class="package-scroll">
        <article
          v-for="item in filteredPackages"
          :key="item.id"
          class="package-card"
          @click="router.push(`/package/${item.id}`)"
        >
          <div class="package-card__top">
            <span>{{ item.categoryName || '设备套餐' }}</span>
            <strong>¥ {{ Number(item.price || 0).toLocaleString('zh-CN') }}</strong>
          </div>
          <h3>{{ item.name }}</h3>
          <p>{{ item.description || '标准设备组合采购方案' }}</p>
          <div class="package-card__link">查看套餐 <van-icon name="arrow" /></div>
        </article>
      </div>
      <van-empty v-else description="暂无在售套餐" image-size="76" />
    </section>

    <section class="equipment-section">
      <div class="section-title">
        <h2>设备目录</h2>
        <span>{{ filteredEquipment.length }} 项可采购设备</span>
      </div>
      <van-tabs
        v-model:active="activeCategory"
        shrink
        color="#246b84"
        title-active-color="#1f5c73"
        title-inactive-color="#87949c"
        @change="onCategoryChange"
      >
        <van-tab v-for="category in categories" :key="category.id" :title="category.name" :name="category.id" />
      </van-tabs>

      <div v-if="loading" class="loading">
        <van-skeleton title :row="3" />
        <van-skeleton title :row="3" />
      </div>
      <div v-else-if="filteredEquipment.length" class="product-grid equipment-grid">
        <article
          v-for="item in filteredEquipment"
          :key="item.id"
          class="product-card"
          @click="router.push(`/equipment/${item.id}`)"
        >
          <div class="product-card__cover">
            <img v-if="item.image" :src="item.image" alt="" />
            <strong v-else>{{ item.name?.slice(0, 1) }}</strong>
            <span class="stock-tag">库存 {{ item.stockQuantity || 0 }}</span>
          </div>
          <div class="product-card__body">
            <h3>{{ item.name }}</h3>
            <p>{{ item.brand || '示例品牌' }} · {{ item.model || '标准型号' }}</p>
            <div class="price"><small>¥</small> {{ Number(item.price || 0).toLocaleString('zh-CN') }}</div>
          </div>
        </article>
      </div>
      <van-empty v-else description="该分类暂无可售设备" image-size="80" />
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getCategories, getEquipmentList, getPackageList } from '../api'

const router = useRouter()
const keyword = ref('')
const loading = ref(false)
const categories = ref([])
const activeCategory = ref()
const equipment = ref([])
const packages = ref([])

const filteredEquipment = computed(() => {
  const word = keyword.value.trim().toLowerCase()
  if (!word) return equipment.value
  return equipment.value.filter((item) =>
    [item.name, item.code, item.brand, item.model]
      .filter(Boolean)
      .some((value) => String(value).toLowerCase().includes(word))
  )
})

const filteredPackages = computed(() => {
  const word = keyword.value.trim().toLowerCase()
  if (!word) return packages.value
  return packages.value.filter((item) =>
    [item.name, item.description].filter(Boolean).some((value) => String(value).toLowerCase().includes(word))
  )
})

async function loadEquipment(categoryId) {
  if (!categoryId) {
    equipment.value = []
    return
  }
  loading.value = true
  try {
    equipment.value = await getEquipmentList(categoryId)
  } finally {
    loading.value = false
  }
}

function onCategoryChange(categoryId) {
  loadEquipment(categoryId)
}

onMounted(async () => {
  const [equipmentCategories, packageCategories] = await Promise.all([
    getCategories(1),
    getCategories(2)
  ])
  categories.value = equipmentCategories || []
  if (categories.value.length) {
    activeCategory.value = categories.value[0].id
    await loadEquipment(activeCategory.value)
  }
  if (packageCategories?.length) {
    packages.value = await getPackageList(packageCategories[0].id)
  }
})
</script>

<style scoped>
.home {
  padding-top: 24px;
}

.home-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 2px 20px;
}

.eyebrow {
  color: #7793a1;
  font-size: 8px;
  font-weight: 700;
  letter-spacing: 1.7px;
}

.home-head h1 {
  margin: 6px 0 0;
  color: #193748;
  font-size: 23px;
}

.head-mark {
  display: grid;
  place-items: center;
  width: 43px;
  height: 43px;
  border-radius: 13px;
  background: #1b5369;
  color: #fff;
  font-weight: 900;
}

.hero {
  position: relative;
  overflow: hidden;
  display: flex;
  min-height: 188px;
  border-radius: 22px;
  background:
    radial-gradient(circle at 85% 20%, rgba(128, 211, 228, 0.3), transparent 28%),
    linear-gradient(130deg, #163c51, #27748c);
  color: #fff;
  box-shadow: 0 18px 36px rgba(24, 76, 94, 0.2);
}

.hero__copy {
  position: relative;
  z-index: 1;
  padding: 26px 24px;
}

.hero__copy > span {
  color: #82c6d7;
  font-size: 10px;
}

.hero__copy h2 {
  margin: 12px 0 13px;
  font-size: 29px;
  line-height: 1.27;
}

.hero__copy p {
  margin: 0;
  color: #a8c6d1;
  font-size: 11px;
}

.hero__visual {
  position: absolute;
  right: -10px;
  bottom: -22px;
  display: grid;
  place-items: center;
  width: 165px;
  height: 165px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 50%;
}

.hero__visual::before {
  content: "";
  position: absolute;
  width: 112px;
  height: 112px;
  border: 1px solid rgba(255, 255, 255, 0.13);
  border-radius: 50%;
}

.hero__visual span {
  position: absolute;
  top: 25px;
  color: #89bdcc;
  font-size: 12px;
}

.hero__visual strong {
  color: rgba(255, 255, 255, 0.18);
  font-size: 54px;
}

.search {
  padding: 16px 0 4px;
}

.search :deep(.van-search__content) {
  border: 1px solid #e2e9ec;
  background: #fff;
}

.package-section {
  margin-top: 8px;
}

.package-scroll {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding: 1px 0 8px;
  scrollbar-width: none;
}

.package-scroll::-webkit-scrollbar {
  display: none;
}

.package-card {
  flex: 0 0 82%;
  padding: 18px;
  border: 1px solid #e3eaed;
  border-radius: 18px;
  background:
    linear-gradient(145deg, #fff, #f2f8fa);
}

.package-card__top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.package-card__top span {
  padding: 4px 8px;
  border-radius: 6px;
  background: #e7f1f4;
  color: #3f7588;
  font-size: 9px;
}

.package-card__top strong {
  color: #b65f35;
  font-size: 15px;
}

.package-card h3 {
  margin: 18px 0 8px;
  color: #213c4d;
  font-size: 17px;
}

.package-card p {
  display: -webkit-box;
  overflow: hidden;
  margin: 0;
  color: #8a99a2;
  font-size: 11px;
  line-height: 1.7;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.package-card__link {
  margin-top: 18px;
  color: #2e748d;
  font-size: 11px;
}

.equipment-section {
  margin-top: 12px;
}

.equipment-section :deep(.van-tabs__wrap) {
  margin: 0 -4px 14px;
}

.equipment-section :deep(.van-tab) {
  justify-content: flex-start;
}

.equipment-section :deep(.van-tabs__line) {
  height: 2px;
  border-radius: 2px;
}

.equipment-grid {
  padding-bottom: 16px;
}

.product-card__cover {
  overflow: hidden;
}

.product-card__cover img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.stock-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 4px 7px;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.8);
  color: #31677b;
  font-size: 9px;
}

.loading {
  display: grid;
  gap: 14px;
  padding: 8px 0 24px;
}
</style>
