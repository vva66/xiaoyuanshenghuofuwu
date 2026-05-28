<template>
  <div class="club-management-container">
    <div class="page-header">
      <div class="header-title">
        <h2 class="page-title">社团管理</h2>
        <p class="page-subtitle">管理社团信息与审核</p>
      </div>
    </div>
    
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item class="search-item">
          <div class="search-input-wrapper">
            <el-icon class="search-icon"><Search /></el-icon>
            <el-input v-model="queryForm.name" placeholder="请输入社团名称" clearable />
          </div>
        </el-form-item>
        <el-form-item class="search-item">
          <el-select v-model="queryForm.category" placeholder="请选择分类" clearable class="search-select">
            <el-option label="学术类" value="学术类" />
            <el-option label="体育类" value="体育类" />
            <el-option label="艺术类" value="艺术类" />
            <el-option label="公益类" value="公益类" />
          </el-select>
        </el-form-item>
        <el-form-item class="search-item">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable class="search-select">
            <el-option label="待审核" :value="0" />
            <el-option label="正常" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item class="search-item">
          <el-button type="primary" @click="loadClubs" class="search-btn">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset" class="reset-btn">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="table-card">
      <el-table :data="clubs" class="club-table" :striped="true">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="社团名称" min-width="150" />
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            <el-tag class="category-tag">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="presidentName" label="社长" width="120" />
        <el-table-column prop="memberCount" label="成员数" width="100">
          <template #default="{ row }">
            <span class="member-count">{{ row.memberCount }} 人</span>
          </template>
        </el-table-column>
        <el-table-column prop="establishedDate" label="成立日期" width="150" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" class="status-tag">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="handleView(row)" class="action-btn view-btn">
                <el-icon><View /></el-icon>
                查看
              </el-button>
              <el-button v-if="row.status === 0" type="success" size="small" @click="handleApprove(row)" class="action-btn approve-btn">
                <el-icon><CircleCheck /></el-icon>
                通过
              </el-button>
              <el-button v-if="row.status === 0" type="warning" size="small" @click="handleReject(row)" class="action-btn reject-btn">
                <el-icon><CircleClose /></el-icon>
                拒绝
              </el-button>
              <el-button v-if="row.status === 1" type="danger" size="small" @click="handleDelete(row)" class="action-btn delete-btn">
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
          @current-change="loadClubs"
          layout="total, prev, pager, next"
          class="pagination"
        />
      </div>
    </el-card>

    <el-dialog v-model="showDetail" title="社团详情" width="600px" class="detail-dialog">
      <div class="detail-content">
        <div class="detail-header">
          <h3 class="detail-title">{{ detailClub.name }}</h3>
          <el-tag :type="getStatusType(detailClub.status)" class="detail-status">
            {{ getStatusText(detailClub.status) }}
          </el-tag>
        </div>
        
        <el-descriptions :column="2" class="detail-desc">
          <el-descriptions-item label="分类">
            <el-tag class="desc-tag">{{ detailClub.category }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="社长">
            <span class="desc-value">{{ detailClub.presidentName }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="成员数">
            <span class="desc-value">{{ detailClub.memberCount }} 人</span>
          </el-descriptions-item>
          <el-descriptions-item label="成立日期">
            <span class="desc-value">{{ detailClub.establishedDate }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="联系方式">
            <span class="desc-value">{{ detailClub.contactInfo || '暂无' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="简介" :span="2">
            <span class="desc-value desc-long">{{ detailClub.description || '暂无' }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllClubs, approveClub, rejectClub, deleteClub, getClubDetail } from '@/api/club'
import { Search, View, CircleCheck, CircleClose, Delete } from '@element-plus/icons-vue'

const clubs = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const showDetail = ref(false)
const detailClub = ref({})

const queryForm = reactive({
  name: '',
  category: '',
  status: ''
})

const getStatusType = (status) => {
  switch (status) {
    case 0: return 'warning'
    case 1: return 'success'
    case 2: return 'danger'
    default: return 'info'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 0: return '待审核'
    case 1: return '正常'
    case 2: return '已拒绝'
    default: return '未知'
  }
}

const loadClubs = async () => {
  try {
    const result = await getAllClubs({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      category: queryForm.category || undefined,
      status: queryForm.status !== '' ? Number(queryForm.status) : undefined
    })
    clubs.value = result.records || []
    total.value = result.total || 0
  } catch (error) {
    console.error(error)
    ElMessage.error('加载社团列表失败')
  }
}

const handleReset = () => {
  queryForm.name = ''
  queryForm.category = ''
  queryForm.status = ''
  pageNum.value = 1
  loadClubs()
}

const handleView = async (row) => {
  try {
    const result = await getClubDetail(row.id)
    detailClub.value = result
    showDetail.value = true
  } catch (error) {
    ElMessage.error('获取社团详情失败')
  }
}

const handleApprove = async (row) => {
  try {
    await ElMessageBox.confirm('确定要审核通过该社团吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    await approveClub(row.id)
    ElMessage.success('审核通过成功')
    row.status = 1
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('审核失败')
    }
  }
}

const handleReject = async (row) => {
  try {
    await ElMessageBox.confirm('确定要拒绝该社团申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await rejectClub(row.id)
    ElMessage.success('已拒绝申请')
    row.status = 2
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该社团吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteClub(row.id)
    ElMessage.success('删除成功')
    loadClubs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadClubs()
})
</script>

<style scoped>
.club-management-container {
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

.club-table :deep(.el-table) {
  border-radius: 12px;
}

.club-table :deep(.el-table__header) {
  background: #f8fafc;
}

.club-table :deep(.el-table__header th) {
  background: #f8fafc;
  color: #64748b;
  font-weight: 600;
  padding: 16px;
}

.club-table :deep(.el-table__body tr) {
  transition: background 0.2s ease;
}

.club-table :deep(.el-table__body tr:hover) {
  background: #f8fafc;
}

.category-tag {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
  padding: 4px 12px;
  color: #fff;
  font-weight: 500;
}

.member-count {
  color: #059669;
  font-weight: 600;
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

.view-btn :deep(.el-button) {
  background: #3b82f6;
  border: none;
}

.approve-btn :deep(.el-button) {
  background: #059669;
  border: none;
}

.reject-btn :deep(.el-button) {
  background: #f59e0b;
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

.detail-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

.detail-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #374151 0%, #1f2937 100%);
  padding: 20px 24px;
}

.detail-dialog :deep(.el-dialog__title) {
  color: white;
  font-size: 20px;
  font-weight: 600;
}

.detail-dialog :deep(.el-dialog__close) {
  color: white;
}

.detail-content {
  padding: 24px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.detail-title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.detail-status {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
}

.detail-desc :deep(.el-descriptions__item) {
  padding: 16px;
  background: #f8fafc;
}

.detail-desc :deep(.el-descriptions__label) {
  color: #64748b;
  font-weight: 600;
}

.desc-tag {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
}

.desc-value {
  color: #1e293b;
  font-weight: 500;
}

.desc-long {
  display: block;
  white-space: pre-wrap;
  line-height: 1.6;
}
</style>