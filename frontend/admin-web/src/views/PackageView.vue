<template>
  <div>
    <div class="page-heading">
      <div>
        <h1>套餐管理</h1>
        <p>把常用设备组合为标准采购方案，控制套餐售价和上下架状态。</p>
      </div>
      <el-button type="primary" @click="openCreate">
        <el-icon><Plus /></el-icon>
        新增套餐
      </el-button>
    </div>

    <section class="page-card table-card">
      <div class="toolbar">
        <div class="toolbar__filters">
          <el-input v-model="query.name" placeholder="套餐名称" clearable style="width: 220px" @keyup.enter="search" />
          <el-select v-model="query.categoryId" placeholder="全部分类" clearable style="width: 160px">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 130px">
            <el-option label="已上架" :value="1" />
            <el-option label="已下架" :value="0" />
          </el-select>
          <el-button type="primary" plain @click="search">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </div>
      </div>

      <el-table v-loading="loading" :data="rows" row-key="id">
        <el-table-column label="套餐" min-width="280">
          <template #default="{ row }">
            <div class="package-cell">
              <div class="package-mark">{{ row.name?.slice(0, 1) }}</div>
              <div>
                <strong>{{ row.name }}</strong>
                <small>{{ row.categoryName || '未分类' }} · {{ row.packageEquipments?.length || '查看详情' }} 项设备</small>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="套餐价格" width="140">
          <template #default="{ row }">
            <span class="money">¥ {{ Number(row.price || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="说明" min-width="240" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="light">
              {{ row.status === 1 ? '已上架' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="230" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetails(row.id)">详情</el-button>
            <el-button link type="primary" @click="openEdit(row.id)">编辑</el-button>
            <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑套餐' : '新增套餐'" width="760px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="form-grid">
          <el-form-item label="套餐名称" prop="name">
            <el-input v-model="form.name" placeholder="例如 小型配电工程标准包" />
          </el-form-item>
          <el-form-item label="套餐分类" prop="categoryId">
            <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%">
              <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="套餐价格" prop="price">
            <el-input-number v-model="form.price" :min="0" :precision="2" :step="100" style="width: 100%" />
          </el-form-item>
          <el-form-item label="上架状态">
            <el-radio-group v-model="form.status">
              <el-radio :value="1">上架</el-radio>
              <el-radio :value="0">下架</el-radio>
            </el-radio-group>
          </el-form-item>
        </div>
        <el-form-item label="图片地址">
          <el-input v-model="form.image" placeholder="可填写图片 URL" />
        </el-form-item>
        <el-form-item label="套餐说明">
          <el-input v-model="form.description" type="textarea" :rows="3" maxlength="1000" show-word-limit />
        </el-form-item>
        <el-form-item label="套餐设备">
          <div class="equipment-list">
            <div v-for="(item, index) in form.packageEquipments" :key="index" class="equipment-row">
              <el-select
                v-model="item.equipmentId"
                filterable
                placeholder="选择设备"
                style="flex: 1"
                @change="(id) => chooseEquipment(item, id)"
              >
                <el-option
                  v-for="equipment in equipmentOptions"
                  :key="equipment.id"
                  :label="`${equipment.name} · ${equipment.code}`"
                  :value="equipment.id"
                />
              </el-select>
              <el-input-number v-model="item.quantity" :min="1" :max="999" />
              <el-input-number v-model="item.price" :min="0" :precision="2" />
              <el-button text type="danger" @click="form.packageEquipments.splice(index, 1)">删除</el-button>
            </div>
            <el-button text type="primary" @click="form.packageEquipments.push({ equipmentId: undefined, quantity: 1, price: 0 })">
              + 添加设备
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存套餐</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="drawerVisible" title="套餐详情" size="430px">
      <template v-if="detail">
        <div class="detail-head">
          <div class="package-mark large">{{ detail.name?.slice(0, 1) }}</div>
          <div>
            <h3>{{ detail.name }}</h3>
            <p>{{ detail.categoryName }}</p>
          </div>
          <span class="detail-price">¥ {{ Number(detail.price || 0).toFixed(2) }}</span>
        </div>
        <p class="detail-description">{{ detail.description || '暂无套餐说明' }}</p>
        <h4>包含设备</h4>
        <div class="detail-item" v-for="item in detail.packageEquipments" :key="item.id">
          <span>{{ item.name }}</span>
          <strong>× {{ item.quantity }}</strong>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deletePackage,
  getCategories,
  getEquipmentPage,
  getPackageDetail,
  getPackagePage,
  savePackage,
  setPackageStatus,
  updatePackage
} from '../api'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const drawerVisible = ref(false)
const formRef = ref()
const rows = ref([])
const total = ref(0)
const categories = ref([])
const equipmentOptions = ref([])
const detail = ref(null)
const query = reactive({ page: 1, pageSize: 10, name: '', categoryId: undefined, status: undefined })

const emptyForm = () => ({
  id: undefined,
  name: '',
  categoryId: undefined,
  price: 0,
  status: 1,
  description: '',
  image: '',
  packageEquipments: []
})
const form = reactive(emptyForm())
const rules = {
  name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择套餐分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入套餐价格', trigger: 'change' }]
}

async function loadData() {
  loading.value = true
  try {
    const data = await getPackagePage(query)
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
  Object.assign(query, { page: 1, pageSize: 10, name: '', categoryId: undefined, status: undefined })
  loadData()
}

function openCreate() {
  Object.assign(form, emptyForm())
  form.packageEquipments = []
  dialogVisible.value = true
}

async function openEdit(id) {
  const data = await getPackageDetail(id)
  Object.assign(form, emptyForm(), data, { packageEquipments: data.packageEquipments || [] })
  dialogVisible.value = true
}

async function openDetails(id) {
  detail.value = await getPackageDetail(id)
  drawerVisible.value = true
}

function chooseEquipment(item, id) {
  const target = equipmentOptions.value.find((equipment) => equipment.id === id)
  if (!target) return
  item.name = target.name
  item.price = Number(target.price || 0)
}

async function save() {
  await formRef.value.validate()
  saving.value = true
  try {
    const payload = {
      ...form,
      packageEquipments: form.packageEquipments.filter((item) => item.equipmentId)
    }
    if (form.id) await updatePackage(payload)
    else await savePackage(payload)
    ElMessage.success('套餐信息已保存')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

async function toggleStatus(row) {
  const nextStatus = row.status === 1 ? 0 : 1
  await setPackageStatus(row.id, nextStatus)
  ElMessage.success(nextStatus === 1 ? '套餐已上架' : '套餐已下架')
  loadData()
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除套餐“${row.name}”吗？`, '删除套餐', { type: 'warning' })
  await deletePackage([row.id])
  ElMessage.success('套餐已删除')
  loadData()
}

onMounted(async () => {
  const [packageCategories, equipmentPage] = await Promise.all([
    getCategories(2),
    getEquipmentPage({ page: 1, pageSize: 200 })
  ])
  categories.value = packageCategories
  equipmentOptions.value = equipmentPage.records || []
  await loadData()
})
</script>

<style scoped>
.table-card {
  padding: 22px;
}

.package-cell,
.detail-head {
  display: flex;
  align-items: center;
  gap: 12px;
}

.package-mark {
  display: grid;
  place-items: center;
  flex: 0 0 auto;
  width: 44px;
  height: 44px;
  border-radius: 13px;
  background: linear-gradient(135deg, #2e6e86, #7cb8c8);
  color: #fff;
  font-weight: 800;
}

.package-mark.large {
  width: 58px;
  height: 58px;
  border-radius: 16px;
  font-size: 22px;
}

.package-cell strong,
.package-cell small {
  display: block;
}

.package-cell strong {
  color: #263d4d;
}

.package-cell small {
  margin-top: 5px;
  color: #94a1aa;
  font-size: 10px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0 18px;
}

.equipment-list {
  width: 100%;
}

.equipment-row {
  display: flex;
  align-items: center;
  gap: 9px;
  margin-bottom: 10px;
}

.equipment-row :deep(.el-input-number) {
  width: 110px;
}

.detail-head {
  padding-bottom: 18px;
  border-bottom: 1px solid #e7edf0;
}

.detail-head h3 {
  margin: 0;
  color: #203b4c;
}

.detail-head p {
  margin: 6px 0 0;
  color: #8b99a2;
  font-size: 12px;
}

.detail-price {
  margin-left: auto;
  color: #b65f30;
  font-size: 20px;
  font-weight: 800;
}

.detail-description {
  color: #6f808a;
  font-size: 13px;
  line-height: 1.8;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 14px 0;
  border-bottom: 1px solid #edf1f3;
  color: #526773;
}
</style>
