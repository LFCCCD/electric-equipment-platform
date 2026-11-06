<template>
  <div class="address-page">
    <van-nav-bar left-arrow title="交付地址" @click-left="router.back()" />

    <div v-if="loading" class="subpage">
      <van-skeleton title :row="4" />
    </div>
    <template v-else>
      <div v-if="addresses.length" class="address-list">
        <article v-for="item in addresses" :key="item.id" class="address-card">
          <div class="address-head">
            <div>
              <strong>{{ item.consignee }}</strong>
              <span>{{ item.phone }}</span>
              <van-tag v-if="item.isDefault === 1" color="#e5f1f4" text-color="#35758b">默认</van-tag>
            </div>
            <span class="label">{{ item.label || '项目地址' }}</span>
          </div>
          <p>{{ fullAddress(item) }}</p>
          <div class="address-actions">
            <van-button v-if="item.isDefault !== 1" size="mini" plain @click="makeDefault(item)">设为默认</van-button>
            <van-button size="mini" plain @click="edit(item)">编辑</van-button>
            <van-button size="mini" plain type="danger" @click="remove(item)">删除</van-button>
          </div>
        </article>
      </div>
      <van-empty v-else description="还没有交付地址" image="location" />
    </template>

    <div class="add-bar">
      <van-button round block icon="plus" @click="openCreate">新增交付地址</van-button>
    </div>

    <van-popup v-model:show="popupVisible" position="bottom" round :style="{ height: '82%' }">
      <div class="address-form">
        <div class="form-head">
          <h2>{{ form.id ? '编辑地址' : '新增地址' }}</h2>
          <van-icon name="cross" @click="popupVisible = false" />
        </div>
        <van-form @submit="save">
          <van-cell-group inset>
            <van-field v-model="form.consignee" label="联系人" placeholder="请输入联系人" :rules="[{ required: true, message: '请输入联系人' }]" />
            <van-field v-model="form.phone" label="手机号" type="tel" placeholder="请输入手机号" :rules="[{ required: true, message: '请输入手机号' }]" />
            <van-field v-model="form.provinceName" label="省份" placeholder="例如 上海市" />
            <van-field v-model="form.cityName" label="城市" placeholder="例如 上海市" />
            <van-field v-model="form.districtName" label="区县" placeholder="例如 浦东新区" />
            <van-field v-model="form.detail" label="详细地址" type="textarea" rows="2" autosize placeholder="项目地址、楼栋和门牌号" :rules="[{ required: true, message: '请输入详细地址' }]" />
            <van-field v-model="form.label" label="地址标签" placeholder="例如 工程项目" />
            <van-cell title="设为默认地址" center>
              <template #right-icon>
                <van-switch v-model="form.isDefaultBoolean" size="20px" active-color="#2d738b" />
              </template>
            </van-cell>
          </van-cell-group>
          <div class="form-submit">
            <van-button round block native-type="submit" :loading="saving">保存地址</van-button>
          </div>
        </van-form>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog, showSuccessToast } from 'vant'
import {
  deleteAddress,
  getAddresses,
  saveAddress,
  setDefaultAddress,
  updateAddress
} from '../api'

const router = useRouter()
const loading = ref(false)
const saving = ref(false)
const popupVisible = ref(false)
const addresses = ref([])

const emptyForm = () => ({
  id: undefined,
  consignee: '',
  phone: '',
  provinceName: '',
  cityName: '',
  districtName: '',
  detail: '',
  label: '项目地址',
  isDefault: 0,
  isDefaultBoolean: false
})
const form = reactive(emptyForm())

function fullAddress(item) {
  return [item.provinceName, item.cityName, item.districtName, item.detail].filter(Boolean).join('')
}

async function load() {
  loading.value = true
  try {
    addresses.value = await getAddresses()
  } finally {
    loading.value = false
  }
}

function openCreate() {
  Object.assign(form, emptyForm())
  popupVisible.value = true
}

function edit(item) {
  Object.assign(form, emptyForm(), item, { isDefaultBoolean: item.isDefault === 1 })
  popupVisible.value = true
}

async function save() {
  saving.value = true
  try {
    const payload = { ...form, isDefault: form.isDefaultBoolean ? 1 : 0 }
    delete payload.isDefaultBoolean
    if (form.id) await updateAddress(payload)
    else await saveAddress(payload)
    popupVisible.value = false
    showSuccessToast('地址已保存')
    await load()
  } finally {
    saving.value = false
  }
}

async function makeDefault(item) {
  await setDefaultAddress(item.id)
  showSuccessToast('已设为默认地址')
  await load()
}

async function remove(item) {
  await showConfirmDialog({ title: '删除地址', message: `确定删除 ${item.consignee} 的交付地址吗？` })
  await deleteAddress(item.id)
  showSuccessToast('地址已删除')
  await load()
}

onMounted(load)
</script>

<style scoped>
.address-page {
  min-height: 100vh;
  padding: 0 12px 100px;
  background: #f4f7f8;
}

.address-page :deep(.van-nav-bar) {
  margin: 0 -12px 10px;
  background: #f4f7f8;
}

.address-list {
  display: grid;
  gap: 12px;
}

.address-card {
  padding: 18px;
  border: 1px solid #e4ebee;
  border-radius: 17px;
  background: #fff;
}

.address-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.address-head > div {
  display: flex;
  align-items: center;
  gap: 8px;
}

.address-head strong {
  color: #284250;
  font-size: 15px;
}

.address-head span {
  color: #87969e;
  font-size: 10px;
}

.address-head .label {
  flex: 0 0 auto;
  padding: 4px 7px;
  border-radius: 5px;
  background: #f2f6f7;
}

.address-card p {
  margin: 12px 0 15px;
  color: #667a85;
  font-size: 12px;
  line-height: 1.7;
}

.address-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #edf1f2;
}

.add-bar {
  position: fixed;
  left: 16px;
  right: 16px;
  bottom: calc(16px + env(safe-area-inset-bottom));
}

.add-bar :deep(.van-button) {
  height: 46px;
  border-color: #246b84;
  color: #246b84;
}

.address-form {
  padding: 22px 0 28px;
}

.form-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px 18px;
}

.form-head h2 {
  margin: 0;
  color: #223f4f;
}

.form-head .van-icon {
  color: #829098;
  font-size: 19px;
}

.form-submit {
  padding: 24px 16px 0;
}

.form-submit :deep(.van-button) {
  height: 45px;
  border: 0;
  background: #246b84;
}
</style>
