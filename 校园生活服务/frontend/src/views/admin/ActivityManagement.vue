<template>
  <div class="activity-management-container">
    <div class="page-header">
      <div class="header-title">
        <h2 class="page-title">活动管理</h2>
        <p class="page-subtitle">管理校园活动信息</p>
      </div>
    </div>
    
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item class="search-item">
          <div class="search-input-wrapper">
            <el-icon class="search-icon"><Search /></el-icon>
            <el-input v-model="queryForm.title" placeholder="请输入活动名称" clearable />
          </div>
        </el-form-item>
        <el-form-item class="search-item">
          <el-select v-model="queryForm.category" placeholder="请选择分类" clearable class="search-select">
            <el-option label="学术讲座" value="学术讲座" />
            <el-option label="文体活动" value="文体活动" />
            <el-option label="志愿服务" value="志愿服务" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item class="search-item">
          <el-button type="primary" @click="loadActivities" class="search-btn">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset" class="reset-btn">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="table-card">
      <el-table :data="activities" class="activity-table" :striped="true">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="活动名称" min-width="150" />
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            <el-tag class="category-tag">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="地点" width="150" />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="currentParticipants" label="参与人数" width="100">
          <template #default="{ row }">
            <span class="participant-count">{{ row.currentParticipants }}/{{ row.maxParticipants }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布者" width="120" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="handleView(row)" class="action-btn view-btn">
                <el-icon><View /></el-icon>
                查看
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
          @current-change="loadActivities"
          layout="total, prev, pager, next"
          class="pagination"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, View, Delete } from '@element-plus/icons-vue'
import { getActivityList, deleteActivity } from '@/api/admin'

const activities = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const queryForm = reactive({
  title: '',
  category: ''
})

const loadActivities = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (queryForm.title) params.keyword = queryForm.title
    if (queryForm.category) params.category = queryForm.category

    const res = await getActivityList(params)
    activities.value = res.records || []
    total.value = res.total || 0
  } catch (e) {
    activities.value = []
    total.value = 0
  }
}

const handleReset = () => {
  queryForm.title = ''
  queryForm.category = ''
  pageNum.value = 1
  loadActivities()
}

const handleView = (row) => {
  ElMessage.info('查看功能开发中')
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该活动吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteActivity(row.id)
    ElMessage.success('删除成功')
    loadActivities()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.activity-management-container {
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

.search-select :deep(.el-select) {
  width: 160px;
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

.activity-table :deep(.el-table) {
  border-radius: 12px;
}

.activity-table :deep(.el-table__header) {
  background: #f8fafc;
}

.activity-table :deep(.el-table__header th) {
  background: #f8fafc;
  color: #64748b;
  font-weight: 600;
  padding: 16px;
}

.activity-table :deep(.el-table__body tr:hover) {
  background: #f8fafc;
}

.category-tag {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  border: none;
  padding: 4px 12px;
  color: #fff;
  font-weight: 500;
}

.participant-count {
  color: #059669;
  font-weight: 600;
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

.view-btn :deep(.el-button) {
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
  padding: 20px;
}

.pagination :deep(.el-pagination) {
  display: flex;
  align-items: center;
}
</style>
