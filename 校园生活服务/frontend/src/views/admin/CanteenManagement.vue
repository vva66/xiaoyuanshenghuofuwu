<template>
  <div class="canteen-management-container">
    <div class="page-header">
      <div class="header-title">
        <h2 class="page-title">食堂管理</h2>
        <p class="page-subtitle">管理校园食堂信息</p>
      </div>
      <el-button type="success" @click="handleAdd" class="add-btn">
        <el-icon><Plus /></el-icon>
        新增食堂
      </el-button>
    </div>
    
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item class="search-item">
          <div class="search-input-wrapper">
            <el-icon class="search-icon"><Search /></el-icon>
            <el-input v-model="queryForm.name" placeholder="请输入食堂名称" clearable />
          </div>
        </el-form-item>
        <el-form-item class="search-item">
          <el-button type="primary" @click="loadCanteens" class="search-btn">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset" class="reset-btn">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="table-card">
      <el-table :data="canteens" class="canteen-table" :striped="true">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="食堂名称" min-width="150" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="openingHours" label="营业时间" width="150" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="rating" label="评分" width="120">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled show-score text-color="#f59e0b" score-template="{value}" class="rating" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" class="status-tag">
              {{ row.status === 1 ? '营业中' : '已关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="handleEdit(row)" class="action-btn edit-btn">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)" class="action-btn delete-btn">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-if="total > 0"
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          @current-change="loadCanteens"
          layout="total, prev, pager, next"
          class="pagination"
        />
      </div>
    </el-card>

    <el-dialog v-model="showDialog" :title="isEdit ? '编辑食堂' : '新增食堂'" width="600px" class="edit-dialog">
      <div class="edit-form-content">
        <el-form :model="editForm" class="edit-form">
          <el-form-item class="form-item">
            <div class="form-label">
              <span class="label-text">食堂名称</span>
              <span class="label-required">*</span>
            </div>
            <el-input v-model="editForm.name" placeholder="请输入食堂名称" class="form-input" />
          </el-form-item>
          <el-form-item class="form-item">
            <div class="form-label">
              <span class="label-text">地址</span>
            </div>
            <el-input v-model="editForm.address" placeholder="请输入地址" class="form-input" />
          </el-form-item>
          <el-form-item class="form-item">
            <div class="form-label">
              <span class="label-text">营业时间</span>
            </div>
            <el-input v-model="editForm.openingHours" placeholder="如：06:30-21:00" class="form-input" />
          </el-form-item>
          <el-form-item class="form-item">
            <div class="form-label">
              <span class="label-text">联系电话</span>
            </div>
            <el-input v-model="editForm.contactPhone" placeholder="请输入联系电话" class="form-input" />
          </el-form-item>
          <el-form-item class="form-item">
            <div class="form-label">
              <span class="label-text">评分</span>
            </div>
            <el-rate v-model="editForm.rating" class="form-rate" />
          </el-form-item>
          <el-form-item class="form-item">
            <div class="form-label">
              <span class="label-text">状态</span>
            </div>
            <el-switch v-model="editForm.status" :active-value="1" :inactive-value="0" active-text="营业中" inactive-text="已关闭" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button class="cancel-btn" @click="showDialog = false">取消</el-button>
        <el-button type="primary" class="save-btn" @click="handleSave">
          <el-icon><Check /></el-icon>
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Edit, Delete, Check } from '@element-plus/icons-vue'
import { getCanteenList } from '@/api/canteen'
import request from '@/utils/request'

const canteens = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const showDialog = ref(false)
const isEdit = ref(false)

const queryForm = reactive({
  name: ''
})

const editForm = reactive({
  id: null,
  name: '',
  address: '',
  openingHours: '',
  contactPhone: '',
  rating: 5,
  status: 1
})

const loadCanteens = async () => {
  try {
    const res = await getCanteenList({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    canteens.value = res.records || []
    total.value = res.total || 0
  } catch (e) {
    canteens.value = []
    total.value = 0
  }
}

const handleReset = () => {
  queryForm.name = ''
  pageNum.value = 1
  loadCanteens()
}

const handleAdd = () => {
  isEdit.value = false
  editForm.id = null
  editForm.name = ''
  editForm.address = ''
  editForm.openingHours = ''
  editForm.contactPhone = ''
  editForm.rating = 5
  editForm.status = 1
  showDialog.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(editForm, row)
  showDialog.value = true
}

const handleSave = async () => {
  try {
    if (isEdit.value) {
      await request({
        url: '/admin/canteens',
        method: 'put',
        data: {
          id: editForm.id,
          name: editForm.name,
          address: editForm.address,
          openingHours: editForm.openingHours,
          contactPhone: editForm.contactPhone,
          rating: editForm.rating,
          status: editForm.status
        }
      })
    } else {
      await request({
        url: '/admin/canteens',
        method: 'post',
        data: {
          name: editForm.name,
          address: editForm.address,
          openingHours: editForm.openingHours,
          contactPhone: editForm.contactPhone,
          rating: editForm.rating,
          status: editForm.status
        }
      })
    }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    showDialog.value = false
    loadCanteens()
  } catch (e) {
    ElMessage.error(isEdit.value ? '编辑失败' : '新增失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该食堂吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await request({
      url: `/admin/canteens/${row.id}`,
      method: 'delete'
    })
    ElMessage.success('删除成功')
    loadCanteens()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadCanteens()
})
</script>

<style scoped>
.canteen-management-container {
  min-height: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 24px 32px;
  background: linear-gradient(135deg, #374151 0%, #1f2937 100%);
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
}

.header-title {
  color: white;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px;
}

.page-subtitle {
  font-size: 14px;
  opacity: 0.8;
  margin: 0;
}

.add-btn :deep(.el-button) {
  padding: 10px 24px;
  border-radius: 10px;
  font-weight: 500;
  background: #059669;
  border: none;
}

.search-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: none;
  margin-bottom: 24px;
}

.search-form {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 24px;
}

.search-item {
  margin: 0;
}

.search-input-wrapper {
  position: relative;
  width: 300px;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
}

.search-input-wrapper :deep(.el-input) {
  padding-left: 36px;
}

.search-input-wrapper :deep(.el-input__wrapper) {
  border-radius: 10px;
}

.search-btn :deep(.el-button),
.reset-btn :deep(.el-button) {
  padding: 8px 20px;
  border-radius: 8px;
}

.table-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: none;
}

.canteen-table :deep(.el-table) {
  border-radius: 12px;
}

.canteen-table :deep(.el-table__header) {
  background: #f8fafc;
}

.canteen-table :deep(.el-table__header th) {
  background: #f8fafc;
  color: #64748b;
  font-weight: 600;
  padding: 16px;
}

.canteen-table :deep(.el-table__body tr:hover) {
  background: #f8fafc;
}

.rating :deep(.el-rate__text) {
  color: #f59e0b;
}

.status-tag {
  padding: 4px 12px;
  border-radius: 20px;
}

.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

.action-btn {
  margin-right: 0;
  padding: 4px 10px;
  border-radius: 6px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.edit-btn :deep(.el-button) {
  background: #3b82f6;
  border: none;
}

.delete-btn :deep(.el-button) {
  background: #dc2626;
  border: none;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0 10px;
}

.pagination :deep(.el-pager li) {
  border-radius: 8px;
  margin: 0 2px;
}

.pagination :deep(.el-pagination button) {
  border-radius: 8px;
}

.edit-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
  margin: 0;
}

.edit-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.edit-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.edit-form-content {
  max-height: 500px;
  overflow-y: auto;
}

.edit-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item {
  margin: 0;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 8px;
}

.label-text {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.label-required {
  color: #dc2626;
  font-size: 14px;
}

.form-input :deep(.el-input__wrapper) {
  border-radius: 8px;
}

.form-rate :deep(.el-rate__icon) {
  font-size: 20px;
}

.cancel-btn {
  padding: 8px 24px;
  border-radius: 8px;
}

.save-btn {
  padding: 8px 24px;
  border-radius: 8px;
}
</style>
