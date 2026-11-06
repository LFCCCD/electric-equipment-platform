<template>
  <div class="login-page">
    <section class="login-story">
      <div class="story__top">
        <div class="story__mark">EP</div>
        <span>Electric Equipment Procurement</span>
      </div>
      <div class="story__content">
        <p class="kicker">OPERATIONS CONSOLE</p>
        <h1>把设备目录、套餐和履约订单放在同一个工作台</h1>
        <p class="intro">
          面向平台运营团队，维护设备参数、管理可售状态，并跟踪订单从确认到发货的完整过程。
        </p>
        <div class="feature-grid">
          <div>
            <strong>设备中心</strong>
            <span>统一型号、品牌、库存与规格</span>
          </div>
          <div>
            <strong>订单流转</strong>
            <span>确认、发货、取消和完成留痕</span>
          </div>
          <div>
            <strong>运营概览</strong>
            <span>设备、套餐与订单数据集中呈现</span>
          </div>
        </div>
      </div>
      <div class="story__bottom">Spring Boot 3 · Vue 3 · MySQL · Redis</div>
    </section>

    <section class="login-panel">
      <div class="login-card">
        <span class="panel-eyebrow">平台运营后台</span>
        <h2>账号登录</h2>
        <p class="panel-intro">使用运营人员账号进入采购平台工作台。</p>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          size="large"
          @keyup.enter="submit"
        >
          <el-form-item label="账号" prop="username">
            <el-input v-model="form.username" placeholder="请输入运营账号" autocomplete="username" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              show-password
              placeholder="请输入密码"
              autocomplete="current-password"
            />
          </el-form-item>
          <el-button class="submit" type="primary" :loading="loading" @click="submit">
            进入运营后台
          </el-button>
        </el-form>

        <div class="demo-account">
          <span>演示账号</span>
          <code>admin / 123456</code>
        </div>
      </div>
      <p class="copyright">电力设备采购平台 V1.0 · 内部项目文档</p>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api'
import { saveSession } from '../utils/session'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: 'admin',
  password: '123456'
})
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    const data = await login(form)
    saveSession(data)
    ElMessage.success('登录成功')
    router.replace(route.query.redirect || '/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: grid;
  grid-template-columns: minmax(540px, 1.08fr) minmax(480px, 0.92fr);
  min-height: 100vh;
  background: #f6f9fa;
}

.login-story {
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  padding: 46px 58px;
  color: #fff;
  background:
    linear-gradient(135deg, rgba(29, 72, 93, 0.96), rgba(10, 35, 50, 0.98)),
    repeating-linear-gradient(120deg, transparent 0 26px, rgba(255, 255, 255, 0.03) 26px 27px);
}

.login-story::before,
.login-story::after {
  content: "";
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(117, 201, 222, 0.18);
}

.login-story::before {
  width: 520px;
  height: 520px;
  right: -250px;
  top: 90px;
}

.login-story::after {
  width: 340px;
  height: 340px;
  right: -130px;
  top: 180px;
}

.story__top {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #9bc3d2;
  font-size: 12px;
  letter-spacing: 1px;
}

.story__mark {
  display: grid;
  place-items: center;
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: #4593ad;
  color: #fff;
  font-weight: 800;
  letter-spacing: 1px;
}

.story__content {
  position: relative;
  z-index: 1;
  max-width: 700px;
  margin: auto 0;
}

.kicker,
.panel-eyebrow {
  color: #77bed2;
  font-size: 10px;
  font-weight: 800;
  letter-spacing: 2px;
}

.story__content h1 {
  max-width: 660px;
  margin: 18px 0 22px;
  font-size: 45px;
  line-height: 1.28;
  letter-spacing: -1px;
}

.intro {
  max-width: 590px;
  color: #b9cdd6;
  font-size: 15px;
  line-height: 1.9;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-top: 48px;
}

.feature-grid > div {
  padding: 18px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.035);
}

.feature-grid strong,
.feature-grid span {
  display: block;
}

.feature-grid strong {
  font-size: 14px;
}

.feature-grid span {
  margin-top: 8px;
  color: #91aebc;
  font-size: 11px;
  line-height: 1.6;
}

.story__bottom {
  position: relative;
  z-index: 1;
  color: #648594;
  font-size: 10px;
  letter-spacing: 1px;
}

.login-panel {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px;
}

.login-card {
  width: 100%;
  max-width: 430px;
}

.panel-eyebrow {
  color: #3b839c;
}

.login-card h2 {
  margin: 10px 0 8px;
  color: #183142;
  font-size: 32px;
}

.panel-intro {
  margin: 0 0 34px;
  color: #7b8c98;
  font-size: 14px;
}

.login-card :deep(.el-form-item) {
  margin-bottom: 22px;
}

.login-card :deep(.el-form-item__label) {
  color: #455a68;
  font-weight: 600;
}

.login-card :deep(.el-input__wrapper) {
  min-height: 48px;
  border-radius: 10px;
  box-shadow: 0 0 0 1px #d8e2e7 inset;
}

.submit {
  width: 100%;
  height: 48px;
  margin-top: 8px;
  border: 0;
  border-radius: 10px;
  background: #1f607a;
  box-shadow: 0 12px 24px rgba(31, 96, 122, 0.2);
}

.demo-account {
  display: flex;
  justify-content: space-between;
  margin-top: 22px;
  padding: 13px 15px;
  border-radius: 10px;
  background: #eef4f6;
  color: #6c7d88;
  font-size: 12px;
}

.demo-account code {
  color: #265a72;
  font-weight: 700;
}

.copyright {
  position: absolute;
  bottom: 24px;
  color: #9ba8b0;
  font-size: 10px;
}
</style>
