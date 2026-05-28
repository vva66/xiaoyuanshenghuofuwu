<template>
  <div class="post-moderation-container">
    <div class="page-header">
      <div class="header-title">
        <h2 class="page-title">帖子审核</h2>
        <p class="page-subtitle">审核互助广场帖子</p>
      </div>
    </div>
    
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item class="search-item">
          <div class="search-input-wrapper">
            <el-icon class="search-icon"><Search /></el-icon>
            <el-input v-model="queryForm.title" placeholder="请输入标题" clearable />
          </div>
        </el-form-item>
        <el-form-item class="search-item">
          <el-select v-model="queryForm.category" placeholder="请选择分类" clearable class="search-select">
            <el-option label="失物招领" value="失物招领" />
            <el-option label="学习求助" value="学习求助" />
            <el-option label="二手交易" value="二手交易" />
            <el-option label="活动组织" value="活动组织" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item class="search-item">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable class="search-select">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item class="search-item">
          <el-button type="primary" @click="loadPosts" class="search-btn">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset" class="reset-btn">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="table-card">
      <el-table :data="posts" class="post-table" :striped="true">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="150">
          <template #default="{ row }">
            <el-tag class="category-tag">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布者" width="120" />
        <el-table-column prop="viewCount" label="浏览量" width="100">
          <template #default="{ row }">
            <span class="view-count">{{ row.viewCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="replyCount" label="回复数" width="100">
          <template #default="{ row }">
            <span class="reply-count">{{ row.replyCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 0 ? 'warning' : 'danger'" class="status-tag">
              {{ row.status === 1 ? '已通过' : row.status === 0 ? '待审核' : '已拒绝' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                v-if="row.status === 0"
                type="success"
                size="small"
                @click="handleApprove(row)"
                class="action-btn approve-btn"
              >
                <el-icon><CircleCheck /></el-icon>
                通过
              </el-button>
              <el-button
                v-if="row.status === 0"
                type="danger"
                size="small"
                @click="handleReject(row)"
                class="action-btn reject-btn"
              >
                <el-icon><CircleClose /></el-icon>
                拒绝
              </el-button>
              <el-button type="primary" size="small" @click="handleView(row)" class="action-btn view-btn">
                <el-icon><View /></el-icon>
                查看
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
          @current-change="loadPosts"
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
import { Search, CircleCheck, CircleClose, View } from '@element-plus/icons-vue'
import { getPostList, approvePost, rejectPost } from '@/api/admin'

const posts = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const queryForm = reactive({
  title: '',
  category: '',
  status: ''
})

const loadPosts = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (queryForm.title) params.keyword = queryForm.title
    if (queryForm.category) params.category = queryForm.category
    if (queryForm.status !== '' && queryForm.status !== null) params.status = queryForm.status

    const res = await getPostList(params)
    posts.value = res.records || []
    total.value = res.total || 0
  } catch (e) {
    posts.value = []
    total.value = 0
  }
}

const handleReset = () => {
  queryForm.title = ''
  queryForm.category = ''
  queryForm.status = ''
  pageNum.value = 1
  loadPosts()
}

const handleApprove = async (row) => {
  try {
    await ElMessageBox.confirm('确定要通过该帖子吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })

    await approvePost(row.id)
    ElMessage.success('审核通过')
    loadPosts()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('审核失败')
    }
  }
}

const handleReject = async (row) => {
  try {
    await ElMessageBox.confirm('确定要拒绝该帖子吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await rejectPost(row.id)
    ElMessage.success('已拒绝')
    loadPosts()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('拒绝失败')
    }
  }
}

const handleView = (row) => {
  ElMessage.info('查看功能开发中')
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.post-moderation-container {
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
  width: 250px;
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

.post-table :deep(.el-table) {
  border-radius: 12px;
}

.post-table :deep(.el-table__header) {
  background: #f8fafc;
}

.post-table :deep(.el-table__header th) {
  background: #f8fafc;
  color: #64748b;
  font-weight: 600;
  padding: 16px;
}

.post-table :deep(.el-table__body tr:hover) {
  background: #f8fafc;
}

.category-tag {
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
  border: none;
  padding: 4px 12px;
  white-space: nowrap;
  color: #fff;
  font-weight: 500;
}

.view-count,
.reply-count {
  color: #64748b;
  font-weight: 500;
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

.approve-btn :deep(.el-button) {
  background: #059669;
  border: none;
}

.reject-btn :deep(.el-button) {
  background: #dc2626;
  border: none;
}

.view-btn :deep(.el-button) {
  background: #3b82f6;
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
