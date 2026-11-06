<template>
  <div>
    <div class="page-heading">
      <div>
        <h1>订单管理</h1>
        <p>查询采购订单，完成确认、发货和交付闭环。</p>
      </div>
      <el-button :loading="loading" @click="loadData">刷新订单</el-button>
    </div>

    <section class="page-card table-card">
      <div class="toolbar">
        <div class="toolbar__filters">
          <el-input v-model="query.number" placeholder="订单号" clearable style="width: 200px" @keyup.enter="search" />
          <el-input v-model="query.phone" placeholder="联系电话" clearable style="width: 150px" @keyup.enter="search" />
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-button type="primary" plain @click="search">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </div>
      </div>

      <el-table v-loading="loading" :data="rows" row-key="id">
        <el-table-column prop="number" label="订单号" min-width="190" />
        <el-table-column label="采购方" min-width="170">
          <template #default="{ row }">
            <div class="customer-cell">
              <strong>{{ row.userName || row.consignee || '采购用户' }}</strong>
              <small>{{ row.phone || '未留电话' }}</small>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="orderEquipments" label="设备明细" min-width="260" show-overflow-tooltip />
        <el-table-column label="金额" width="130">
          <template #default="{ row }">
            <span class="money">¥ {{ Number(row.amount || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusMeta(row.status).type" effect="light">
              {{ statusMeta(row.status).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="showDetail(row.id)">详情</el-button>
            <el-button v-if="row.status === 2" link type="success" @click="confirm(row)">确认</el-button>
            <el-button v-if="row.status === 3" link type="primary" @click="deliver(row)">发货</el-button>
            <el-button v-if="row.status === 4" link type="success" @click="complete(row)">完成</el-button>
            <el-button v-if="[1, 2, 3].includes(row.status)" link type="danger" @click="cancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </section>

    <el-drawer v-model="drawerVisible" title="订单详情" size="560px">
      <template v-if="detail">
        <div class="order-head">
          <div>
            <span>采购订单</span>
            <h3>{{ detail.number }}</h3>
          </div>
          <el-tag :type="statusMeta(detail.status).type" size="large">
            {{ statusMeta(detail.status).label }}
          </el-tag>
        </div>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="采购方">{{ detail.userName || detail.consignee || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detail.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="交付地址" :span="2">{{ detail.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ detail.orderTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">¥ {{ Number(detail.amount || 0).toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || '无' }}</el-descriptions-item>
        </el-descriptions>

        <h4>设备明细</h4>
        <div class="order-lines">
          <div v-for="item in detail.orderDetailList || []" :key="item.id" class="order-line">
            <div class="line-thumb">{{ item.name?.slice(0, 1) }}</div>
            <div class="line-main">
              <strong>{{ item.name }}</strong>
              <span>{{ item.selectedSpec || '标准规格' }}</span>
            </div>
            <div class="line-number">× {{ item.number }}</div>
            <div class="line-price">¥ {{ Number(item.amount || 0).toFixed(2) }}</div>
          </div>
        </div>

        <div v-if="detail.cancelReason || detail.rejectionReason" class="reason">
          <strong>取消或拒绝原因</strong>
          <span>{{ detail.cancelReason || detail.rejectionReason }}</span>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  cancelOrder,
  completeOrder,
  confirmOrder,
  deliverOrder,
  getOrderDetail,
  getOrders
} from '../api'

const loading = ref(false)
const drawerVisible = ref(false)
const rows = ref([])
const total = ref(0)
const detail = ref(null)
const query = reactive({ page: 1, pageSize: 10, number: '', phone: '', status: undefined })

const statusOptions = [
  { value: 1, label: '待付款', type: 'warning' },
  { value: 2, label: '待确认', type: 'warning' },
  { value: 3, label: '已确认', type: 'primary' },
  { value: 4, label: '已发货', type: 'success' },
  { value: 5, label: '已完成', type: 'success' },
  { value: 6, label: '已取消', type: 'info' }
]

function statusMeta(status) {
  return statusOptions.find((item) => item.value === status) || { label: '未知', type: 'info' }
}

async function loadData() {
  loading.value = true
  try {
    const data = await getOrders(query)
    rows.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function search() {
  query.page = 1
  loadData()
}

function resetQuery() {
  Object.assign(query, { page: 1, pageSize: 10, number: '', phone: '', status: undefined })
  loadData()
}

async function showDetail(id) {
  detail.value = await getOrderDetail(id)
  drawerVisible.value = true
}

async function confirm(row) {
  await ElMessageBox.confirm(`确认接收订单 ${row.number} 吗？`, '确认订单', { type: 'info' })
  await confirmOrder(row.id)
  ElMessage.success('订单已确认')
  loadData()
}

async function deliver(row) {
  await ElMessageBox.confirm(`确认订单 ${row.number} 已经发货吗？`, '订单发货', { type: 'info' })
  await deliverOrder(row.id)
  ElMessage.success('订单已标记为发货')
  loadData()
}

async function complete(row) {
  await ElMessageBox.confirm(`确认订单 ${row.number} 已完成交付吗？`, '完成订单', { type: 'success' })
  await completeOrder(row.id)
  ElMessage.success('订单已完成')
  loadData()
}

async function cancel(row) {
  const { value } = await ElMessageBox.prompt('请输入取消原因', '取消订单', {
    inputPattern: /\S+/,
    inputErrorMessage: '取消原因不能为空'
  })
  await cancelOrder(row.id, value)
  ElMessage.success('订单已取消')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.table-card {
  padding: 22px;
}

.customer-cell strong,
.customer-cell small {
  display: block;
}

.customer-cell strong {
  color: #294252;
}

.customer-cell small {
  margin-top: 5px;
  color: #96a3ab;
  font-size: 10px;
}

.order-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 22px;
}

.order-head span {
  color: #8c9aa3;
  font-size: 11px;
}

.order-head h3 {
  margin: 7px 0 0;
  color: #203a4a;
}

h4 {
  margin: 26px 0 12px;
  color: #304958;
}

.order-lines {
  border-top: 1px solid #e8edef;
}

.order-line {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid #e8edef;
}

.line-thumb {
  display: grid;
  place-items: center;
  width: 40px;
  height: 40px;
  border-radius: 11px;
  background: #e8f2f5;
  color: #2c6f89;
  font-weight: 800;
}

.line-main {
  flex: 1;
}

.line-main strong,
.line-main span {
  display: block;
}

.line-main strong {
  color: #314a59;
}

.line-main span {
  margin-top: 4px;
  color: #98a4ac;
  font-size: 10px;
}

.line-number {
  color: #7e8e97;
}

.line-price {
  width: 90px;
  text-align: right;
  color: #b55e32;
  font-weight: 700;
}

.reason {
  margin-top: 22px;
  padding: 14px;
  border-radius: 10px;
  background: #fff5f0;
}

.reason strong,
.reason span {
  display: block;
}

.reason strong {
  color: #a85636;
  font-size: 12px;
}

.reason span {
  margin-top: 6px;
  color: #7f6c64;
  font-size: 12px;
}
</style>
