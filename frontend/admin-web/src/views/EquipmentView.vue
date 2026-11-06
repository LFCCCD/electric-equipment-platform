<template>
  <div>
    <div class="page-heading">
      <div>
        <h1>设备管理</h1>
        <p>维护设备 SKU、品牌、型号、库存、价格与技术规格。</p>
      </div>
      <el-button type="primary" @click="openCreate">
        <el-icon><Plus /></el-icon>
        新增设备
      </el-button>
    </div>

    <section class="page-card table-card">
      <div class="toolbar">
        <div class="toolbar__filters">
          <el-input
            v-model="query.name"
            placeholder="设备名称"
            clearable
            style="width: 190px"
            @keyup.enter="search"
          />
          <el-input
            v-model="query.code"
            placeholder="设备编码"
            clearable
            style="width: 160px"
            @keyup.enter="search"
          />
          <el-select v-model="query.categoryId" placeholder="全部分类" clearable style="width: 150px">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 130px">
            <el-option label="已上架" :value="1" />
            <el-option label="已下架" :value="0" />
          </el-select>
          <el-button type="primary" plain @click="search">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </div>
        <el-button :disabled="!selectedIds.length" type="danger" plain @click="removeSelected">
          删除所选
        </el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="rows"
        row-key="id"
        @selection-change="(rows) => selectedIds = rows.map((row) => row.id)"
      >
        <el-table-column type="selection" width="46" />
        <el-table-column label="设备" min-width="240">
          <template #default="{ row }">
            <div class="equipment-cell">
              <div class="product-thumb">
                <img v-if="row.image" :src="row.image" alt="" />
                <span v-else>{{ row.name?.slice(0, 1) }}</span>
              </div>
              <div>
                <strong>{{ row.name }}</strong>
                <small>{{ row.code }} · {{ row.brand || '未填品牌' }}</small>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="model" label="型号" min-width="120" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column label="价格" width="120">
          <template #default="{ row }">
            <span class="money">¥ {{ Number(row.price || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="库存" width="100">
          <template #default="{ row }">
            <span :class="{ lowStock: Number(row.stockQuantity) < 10 }">{{ row.stockQuantity ?? 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="light">
              {{ row.status === 1 ? '已上架' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="210" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row.id)">编辑</el-button>
            <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button link type="danger" @click="removeOne(row)">删除</el-button>
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

    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? '编辑设备' : '新增设备'"
      width="760px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="form-grid">
          <el-form-item label="设备编码" prop="code">
            <el-input v-model="form.code" placeholder="例如 EQ-DG-001" />
          </el-form-item>
          <el-form-item label="设备名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入设备名称" />
          </el-form-item>
          <el-form-item label="设备分类" prop="categoryId">
            <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%">
              <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="品牌">
            <el-input v-model="form.brand" placeholder="请输入品牌" />
          </el-form-item>
          <el-form-item label="型号">
            <el-input v-model="form.model" placeholder="请输入型号" />
          </el-form-item>
          <el-form-item label="计价单位">
            <el-input v-model="form.unit" placeholder="台、套、米" />
          </el-form-item>
          <el-form-item label="销售单价" prop="price">
            <el-input-number v-model="form.price" :min="0" :precision="2" :step="100" style="width: 100%" />
          </el-form-item>
          <el-form-item label="可售库存" prop="stockQuantity">
            <el-input-number v-model="form.stockQuantity" :min="0" :step="1" style="width: 100%" />
          </el-form-item>
        </div>
        <el-form-item label="图片地址">
          <el-input v-model="form.image" placeholder="可填写 OSS 地址或上传接口返回的 /media 地址" />
        </el-form-item>
        <el-form-item label="设备描述">
          <el-input v-model="form.description" type="textarea" :rows="3" maxlength="1000" show-word-limit />
        </el-form-item>
        <el-form-item label="规格参数">
          <div class="spec-list">
            <div v-for="(spec, index) in form.specs" :key="index" class="spec-row">
              <el-input v-model="spec.name" placeholder="参数名，如 额定电压" />
              <el-input v-model="spec.value" placeholder="参数值，如 10kV" />
              <el-button text type="danger" @click="form.specs.splice(index, 1)">删除</el-button>
            </div>
            <el-button text type="primary" @click="form.specs.push({ name: '', value: '' })">
              + 添加规格
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="上架状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteEquipment,
  getCategories,
  getEquipmentDetail,
  getEquipmentPage,
  saveEquipment,
  setEquipmentStatus,
  updateEquipment
} from '../api'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const rows = ref([])
const total = ref(0)
const categories = ref([])
const selectedIds = ref([])
const query = reactive({ page: 1, pageSize: 10, name: '', code: '', categoryId: undefined, status: undefined })

const emptyForm = () => ({
  id: undefined,
  code: '',
  name: '',
  categoryId: undefined,
  brand: '',
  model: '',
  unit: '台',
  price: 0,
  stockQuantity: 0,
  image: '',
  description: '',
  status: 1,
  specs: []
})
const form = reactive(emptyForm())
const rules = {
  code: [{ required: true, message: '请输入设备编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择设备分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入销售价格', trigger: 'change' }],
  stockQuantity: [{ required: true, message: '请输入可售库存', trigger: 'change' }]
}

async function loadData() {
  loading.value = true
  try {
    const data = await getEquipmentPage(query)
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
  Object.assign(query, { page: 1, pageSize: 10, name: '', code: '', categoryId: undefined, status: undefined })
  loadData()
}

function openCreate() {
  Object.assign(form, emptyForm())
  dialogVisible.value = true
}

async function openEdit(id) {
  const data = await getEquipmentDetail(id)
  Object.assign(form, emptyForm(), data, { specs: data.specs || [] })
  dialogVisible.value = true
}

async function save() {
  await formRef.value.validate()
  saving.value = true
  try {
    const payload = { ...form, specs: form.specs.filter((item) => item.name && item.value) }
    if (form.id) {
      await updateEquipment(payload)
    } else {
      await saveEquipment(payload)
    }
    ElMessage.success('设备信息已保存')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

async function toggleStatus(row) {
  const nextStatus = row.status === 1 ? 0 : 1
  await setEquipmentStatus(row.id, nextStatus)
  ElMessage.success(nextStatus === 1 ? '设备已上架' : '设备已下架')
  loadData()
}

async function removeOne(row) {
  await ElMessageBox.confirm(`确定删除设备“${row.name}”吗？`, '删除设备', { type: 'warning' })
  await deleteEquipment([row.id])
  ElMessage.success('设备已删除')
  loadData()
}

async function removeSelected() {
  await ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 个设备吗？`, '批量删除', { type: 'warning' })
  await deleteEquipment(selectedIds.value)
  ElMessage.success('设备已删除')
  loadData()
}

onMounted(async () => {
  categories.value = await getCategories(1)
  await loadData()
})
</script>

<style scoped>
.table-card {
  padding: 22px;
}

.equipment-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.equipment-cell strong,
.equipment-cell small {
  display: block;
}

.equipment-cell strong {
  color: #263d4d;
}

.equipment-cell small {
  margin-top: 5px;
  color: #94a1aa;
  font-size: 10px;
}

.lowStock {
  color: #c35b35;
  font-weight: 700;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0 18px;
}

.spec-list {
  width: 100%;
}

.spec-row {
  display: grid;
  grid-template-columns: 1fr 1.4fr auto;
  gap: 8px;
  margin-bottom: 9px;
}
</style>
