<template>
  <div class="user-management-container">
    <div class="page-header">
      <div class="header-title">
        <h2 class="page-title">用户管理</h2>
        <p class="page-subtitle">管理系统用户信息</p>
      </div>
    </div>
    
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item class="search-item">
          <div class="search-input-wrapper">
            <el-icon class="search-icon"><Search /></el-icon>
            <el-input v-model="queryForm.username" placeholder="请输入用户名" clearable />
          </div>
        </el-form-item>
        <el-form-item class="search-item">
          <el-input v-model="queryForm.studentNo" placeholder="请输入学号" clearable class="search-input" />
        </el-form-item>
        <el-form-item class="search-item">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable class="search-select">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item class="search-item">
          <el-button type="primary" @click="loadUsers" class="search-btn">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset" class="reset-btn">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="table-card">
      <el-table :data="users" class="user-table" :striped="true">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            <span class="gender-text">{{ row.gender === 1 ? '男' : '女' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="college" label="学院" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" class="status-tag">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="handleEdit(row)" class="action-btn edit-btn">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button
                :type="row.status === 1 ? 'danger' : 'success'"
                size="small"
                @click="handleToggleStatus(row)"
                class="action-btn toggle-btn"
              >
                <el-icon><component :is="row.status === 1 ? Lock : Unlock" /></el-icon>
                {{ row.status === 1 ? '禁用' : '启用' }}
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
          @current-change="loadUsers"
          layout="total, prev, pager, next"
          class="pagination"
        />
      </div>
    </el-card>
    
    <el-dialog v-model="showEditDialog" title="编辑用户" width="600px" class="edit-dialog">
      <div class="edit-form-content">
        <el-form :model="editForm" class="edit-form">
          <el-form-item class="form-item">
            <div class="form-label">
              <span class="label-text">用户名</span>
            </div>
            <el-input v-model="editForm.username" disabled class="form-input" />
          </el-form-item>
          <el-form-item class="form-item">
            <div class="form-label">
              <span class="label-text">真实姓名</span>
              <span class="label-required">*</span>
            </div>
            <el-input v-model="editForm.realName" placeholder="请输入真实姓名" class="form-input" />
          </el-form-item>
          <el-form-item class="form-item">
            <div class="form-label">
              <span class="label-text">手机号</span>
            </div>
            <el-input v-model="editForm.phone" placeholder="请输入手机号" class="form-input" />
          </el-form-item>
          <el-form-item class="form-item">
            <div class="form-label">
              <span class="label-text">邮箱</span>
            </div>
            <el-input v-model="editForm.email" placeholder="请输入邮箱" class="form-input" />
          </el-form-item>
          <el-form-item class="form-item">
            <div class="form-label">
              <span class="label-text">学院</span>
            </div>
            <el-input v-model="editForm.college" placeholder="请输入学院" class="form-input" />
          </el-form-item>
          <el-form-item class="form-item">
            <div class="form-label">
              <span class="label-text">专业</span>
            </div>
            <el-input v-model="editForm.major" placeholder="请输入专业" class="form-input" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button class="cancel-btn" @click="showEditDialog = false">取消</el-button>
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
import { Search, Edit, Lock, Unlock, Check } from '@element-plus/icons-vue'
import { getUserList, updateUser, updateUserStatus } from '@/api/admin'

const loading = ref(false)
const users = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const showEditDialog = ref(false)

const queryForm = reactive({
  username: '',
  studentNo: '',
  status: ''
})

const editForm = reactive({
  id: null,
  username: '',
  realName: '',
  phone: '',
  email: '',
  college: '',
  major: ''
})

const loadUsers = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (queryForm.username) params.username = queryForm.username
    if (queryForm.studentNo) params.studentNo = queryForm.studentNo
    if (queryForm.status !== '' && queryForm.status !== null) params.status = queryForm.status

    const res = await getUserList(params)
    users.value = res.records || []
    total.value = res.total || 0
  } catch (e) {
    users.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryForm.username = ''
  queryForm.studentNo = ''
  queryForm.status = ''
  pageNum.value = 1
  loadUsers()
}

const handleEdit = (row) => {
  Object.assign(editForm, row)
  showEditDialog.value = true
}

const handleSave = async () => {
  try {
    await updateUser({
      id: editForm.id,
      realName: editForm.realName,
      phone: editForm.phone,
      email: editForm.email,
      college: editForm.college,
      major: editForm.major
    })
    ElMessage.success('保存成功')
    showEditDialog.value = false
    loadUsers()
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const handleToggleStatus = async (row) => {
  const action = row.status === 1 ? '禁用' : '启用'
  const newStatus = row.status === 1 ? 0 : 1

  try {
    await ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await updateUserStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadUsers()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-management-container {
  min-height: 100%;
}

.page-header {
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
  width: 200px;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
}

.search-input-wrapper :deep(.el-input),
.search-input :deep(.el-input) {
  padding-left: 36px;
}

.search-input-wrapper :deep(.el-input__wrapper),
.search-input :deep(.el-input__wrapper) {
  border-radius: 10px;
}

.search-select :deep(.el-select) {
  width: 140px;
}

.search-select :deep(.el-select__wrapper) {
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

.user-table :deep(.el-table) {
  border-radius: 12px;
}

.user-table :deep(.el-table__header) {
  background: #f8fafc;
}

.user-table :deep(.el-table__header th) {
  background: #f8fafc;
  color: #64748b;
  font-weight: 600;
  padding: 16px;
}

.user-table :deep(.el-table__body tr:hover) {
  background: #f8fafc;
}

.gender-text {
  color: #64748b;
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

.toggle-btn :deep(.el-button) {
  border: none;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.pagination :deep(.el-pagination) {
  display: flex;
  align-items: center;
}

.edit-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

.edit-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #374151 0%, #1f2937 100%);
  padding: 20px 24px;
}

.edit-dialog :deep(.el-dialog__title) {
  color: white;
  font-size: 20px;
  font-weight: 600;
}

.edit-dialog :deep(.el-dialog__close) {
  color: white;
}

.edit-form-content {
  padding: 24px;
}

.edit-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-item {
  margin: 0;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 10px;
}

.label-text {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.label-required {
  color: #ef4444;
}

.form-input :deep(.el-input) {
  width: 100%;
}

.form-input :deep(.el-input__wrapper) {
  border-radius: 8px;
}

.cancel-btn :deep(.el-button),
.save-btn :deep(.el-button) {
  padding: 10px 24px;
  border-radius: 8px;
}
</style>
