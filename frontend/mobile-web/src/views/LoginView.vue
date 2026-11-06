<template>
  <div class="login-page">
    <div class="login-bg">
      <span>10 kV</span>
      <span>PROCUREMENT</span>
    </div>

    <section class="brand">
      <div class="brand__mark">EP</div>
      <p>ELECTRIC EQUIPMENT</p>
      <h1>电力设备<br />采购平台</h1>
      <span>设备选型、套餐采购与订单跟踪</span>
    </section>

    <section class="login-panel">
      <div class="panel-title">
        <span>采购人员登录</span>
        <strong>开始企业采购</strong>
      </div>
      <van-form @submit="submit">
        <van-cell-group inset>
          <van-field
            v-model="form.username"
            name="username"
            label="账号"
            placeholder="请输入采购账号"
            :rules="[{ required: true, message: '请输入账号' }]"
          />
          <van-field
            v-model="form.password"
            type="password"
            name="password"
            label="密码"
            placeholder="请输入密码"
            :rules="[{ required: true, message: '请输入密码' }]"
          />
        </van-cell-group>
        <div class="submit-wrap">
          <van-button round block type="primary" native-type="submit" :loading="loading">
            登录采购端
          </van-button>
        </div>
      </van-form>
      <p class="demo">演示账号：buyer / 123456</p>
      <p class="hint">当前为 H5 演示登录，正式小程序可继续使用微信授权登录。</p>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showSuccessToast } from 'vant'
import { login } from '../api'
import { saveSession } from '../utils/session'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const form = reactive({
  username: 'buyer',
  password: '123456'
})

async function submit() {
  loading.value = true
  try {
    const data = await login(form)
    saveSession(data)
    showSuccessToast('登录成功')
    router.replace(route.query.redirect || '/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  position: relative;
  overflow: hidden;
  min-height: 100vh;
  padding: 56px 22px 32px;
  background:
    linear-gradient(160deg, rgba(20, 56, 75, 0.98), rgba(9, 28, 40, 0.98)),
    #14384b;
  color: #fff;
}

.login-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.login-bg::before,
.login-bg::after {
  content: "";
  position: absolute;
  border: 1px solid rgba(111, 196, 215, 0.18);
  border-radius: 50%;
}

.login-bg::before {
  width: 330px;
  height: 330px;
  right: -170px;
  top: -40px;
}

.login-bg::after {
  width: 210px;
  height: 210px;
  left: -120px;
  bottom: 160px;
}

.login-bg span {
  position: absolute;
  color: rgba(255, 255, 255, 0.025);
  font-weight: 900;
  letter-spacing: -4px;
}

.login-bg span:first-child {
  right: -8px;
  top: 72px;
  font-size: 84px;
}

.login-bg span:last-child {
  left: -36px;
  bottom: 52px;
  font-size: 33px;
  letter-spacing: 2px;
}

.brand {
  position: relative;
  z-index: 1;
}

.brand__mark {
  display: grid;
  place-items: center;
  width: 46px;
  height: 46px;
  border-radius: 14px;
  background: #4197b2;
  font-weight: 900;
  box-shadow: 0 10px 24px rgba(48, 139, 162, 0.3);
}

.brand p {
  margin: 28px 0 8px;
  color: #76b8cb;
  font-size: 9px;
  font-weight: 700;
  letter-spacing: 2px;
}

.brand h1 {
  margin: 0;
  font-size: 38px;
  line-height: 1.22;
  letter-spacing: -1px;
}

.brand > span {
  display: block;
  margin-top: 14px;
  color: #9db9c5;
  font-size: 13px;
}

.login-panel {
  position: relative;
  z-index: 1;
  margin-top: 64px;
  padding: 24px 0 0;
}

.panel-title span,
.panel-title strong {
  display: block;
}

.panel-title span {
  color: #7fb7c8;
  font-size: 11px;
}

.panel-title strong {
  margin-top: 6px;
  font-size: 22px;
}

.login-panel :deep(.van-cell-group) {
  margin: 20px 0 18px;
  overflow: hidden;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.96);
}

.login-panel :deep(.van-field__label) {
  width: 44px;
  color: #405562;
}

.submit-wrap {
  padding-top: 2px;
}

.submit-wrap :deep(.van-button) {
  height: 46px;
  border: 0;
  background: #3e91a9;
}

.demo {
  margin: 20px 0 0;
  color: #d1e1e7;
  text-align: center;
  font-size: 12px;
}

.hint {
  margin: 10px 10px 0;
  color: #7393a0;
  text-align: center;
  font-size: 10px;
  line-height: 1.7;
}
</style>
