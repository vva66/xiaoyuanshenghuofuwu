<template>
  <div class="club-list-container">
    <div class="page-header">
      <div class="header-title">
        <h2 class="page-title">社团活动</h2>
        <p class="page-subtitle">探索校园丰富多彩的社团文化</p>
      </div>
      <div class="header-actions">
        <el-button type="success" @click="showCreateDialog = true" class="action-btn">
          <el-icon><Plus /></el-icon>
          创建社团
        </el-button>
        <el-button type="warning" @click="showMyClubs = true" class="action-btn">
          <el-icon><User /></el-icon>
          我的社团
        </el-button>
      </div>
    </div>
    
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="分类">
          <el-select v-model="queryForm.category" placeholder="请选择分类" clearable class="search-select">
            <el-option label="学术类" value="学术类" />
            <el-option label="体育类" value="体育类" />
            <el-option label="艺术类" value="艺术类" />
            <el-option label="公益类" value="公益类" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadClubs" class="search-btn">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset" class="reset-btn">
            <el-icon><RefreshLeft /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-skeleton v-if="loading" :rows="5" animated class="skeleton-loading" />
    
    <div v-else class="club-grid">
      <el-card 
        v-for="club in clubs" 
        :key="club.id" 
        class="club-card"
        shadow="hover"
        @click="viewDetail(club.id)"
      >
        <div class="club-header">
          <div class="club-icon-wrapper">
            <el-image v-if="club.logo" :src="club.logo" class="club-logo" fit="cover" @error="onLogoError($event, club)" />
            <el-icon v-else class="club-icon"><Building /></el-icon>
          </div>
          <div class="club-badge">
            <el-tag :type="getTagType(club.category)" size="small">{{ club.category }}</el-tag>
          </div>
        </div>
        <h3 class="club-name">{{ club.name }}</h3>
        <p class="club-description">{{ club.description }}</p>
        <div class="club-info">
          <div class="info-item">
            <el-icon class="info-icon"><User /></el-icon>
            <span>{{ club.memberCount }} 成员</span>
          </div>
          <div class="info-item">
            <el-icon class="info-icon"><User /></el-icon>
            <span>{{ club.presidentName || '暂无' }}</span>
          </div>
        </div>
        <div class="club-actions">
          <el-button 
            :type="isJoined(club.id) ? 'default' : 'primary'" 
            class="join-btn"
            @click.stop="handleJoinClub(club)"
            :disabled="isJoined(club.id)"
          >
            {{ isJoined(club.id) ? '已加入' : '加入社团' }}
          </el-button>
          <el-button type="default" class="detail-btn" @click.stop="viewDetail(club.id)">
            <el-icon><Eye /></el-icon>
            详情
          </el-button>
        </div>
      </el-card>
    </div>
    
    <div v-if="!loading && clubs.length === 0" class="empty-state">
      <div class="empty-icon-wrapper">
        <el-icon class="empty-icon"><Flag /></el-icon>
      </div>
      <p class="empty-text">暂无社团数据</p>
      <el-button type="primary" @click="showCreateDialog = true">创建第一个社团</el-button>
    </div>
    
    <div class="pagination-wrapper">
      <el-pagination
        v-if="total > 0"
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        @current-change="loadClubs"
        layout="total, prev, pager, next, jumper"
        class="pagination"
      />
    </div>
    
    <!-- 创建社团对话框 -->
    <el-dialog v-model="showCreateDialog" title="创建社团" width="600px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="社团名称">
          <el-input v-model="createForm.name" placeholder="请输入社团名称" />
        </el-form-item>
        <el-form-item label="社团分类">
          <el-select v-model="createForm.category" placeholder="请选择分类">
            <el-option label="学术类" value="学术类" />
            <el-option label="体育类" value="体育类" />
            <el-option label="艺术类" value="艺术类" />
            <el-option label="公益类" value="公益类" />
          </el-select>
        </el-form-item>
        <el-form-item label="社团简介">
          <el-input v-model="createForm.description" type="textarea" :rows="4" placeholder="请输入社团简介" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="createForm.contactInfo" placeholder="请输入联系方式" />
        </el-form-item>
        <el-form-item label="Logo地址">
          <el-input v-model="createForm.logo" placeholder="请输入Logo图片地址（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreateClub">确定创建</el-button>
      </template>
    </el-dialog>
    
    <!-- 我的社团对话框 -->
    <el-dialog v-model="showMyClubs" title="我加入的社团" width="800px">
      <el-table :data="myClubs" style="width: 100%">
        <el-table-column prop="name" label="社团名称" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="memberCount" label="人数" width="80" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="danger" size="small" @click="handleLeaveClub(scope.row.id)">退出</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="myClubs.length === 0" description="暂未加入任何社团" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getClubList, createClub, joinClub, leaveClub, getMyClubs } from '@/api/club'

const getTagType = (category) => {
  const typeMap = {
    '学术类': 'info',
    '体育类': 'success',
    '艺术类': 'warning',
    '公益类': 'danger'
  }
  return typeMap[category] || 'info'
}

const router = useRouter()
const loading = ref(true)
const clubs = ref([])
const myClubs = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(9)
const showCreateDialog = ref(false)
const showMyClubs = ref(false)

const queryForm = reactive({
  category: ''
})

const createForm = reactive({
  name: '',
  category: '',
  description: '',
  contactInfo: '',
  logo: ''
})

const userId = localStorage.getItem('userId')

const loadClubs = async () => {
  try {
    loading.value = true
    const result = await getClubList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      category: queryForm.category
    })
    clubs.value = result.records || []
    total.value = result.total || 0
  } catch (error) {
    console.error(error)
    ElMessage.error('加载社团列表失败')
    clubs.value = []
  } finally {
    loading.value = false
  }
}

const onLogoError = (event, club) => {
  event.target.style.display = 'none'
  club.logo = null
}

const handleReset = () => {
  queryForm.category = ''
  pageNum.value = 1
  loadClubs()
}

const handleCreateClub = async () => {
  if (!createForm.name || !createForm.category) {
    ElMessage.warning('请填写社团名称和分类')
    return
  }
  
  try {
    await createClub({
      ...createForm,
      presidentId: userId,
      presidentName: localStorage.getItem('username')
    })
    ElMessage.success('社团创建成功，等待管理员审核')
    showCreateDialog.value = false
    Object.assign(createForm, { name: '', category: '', description: '', contactInfo: '', logo: '' })
    loadClubs()
  } catch (error) {
    ElMessage.error('创建社团失败')
  }
}

const handleJoinClub = async (club) => {
  if (isJoined(club.id)) {
    ElMessage.info('您已经加入该社团')
    router.push({ path: '/club/detail', query: { id: club.id } })
    return
  }
  
  try {
    await joinClub({
      clubId: club.id,
      userId: userId,
      userName: localStorage.getItem('username')
    })
    ElMessage.success('加入社团成功')
    router.push({ path: '/club/detail', query: { id: club.id } })
  } catch (error) {
    ElMessage.error('加入社团失败')
  }
}

const handleLeaveClub = async (clubId) => {
  try {
    await ElMessageBox.confirm('确定要退出该社团吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await leaveClub({ clubId, userId })
    ElMessage.success('已退出社团')
    myClubs.value = myClubs.value.filter(c => c.id !== clubId)
    loadClubs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('退出社团失败')
    }
  }
}

const loadMyClubs = async () => {
  try {
    const result = await getMyClubs(userId)
    myClubs.value = result || []
  } catch (error) {
    console.error(error)
  }
}

const isJoined = (clubId) => {
  return myClubs.value.some(club => club.id === clubId)
}

const viewDetail = (clubId) => {
  router.push('/club/detail?id=' + clubId)
}

onMounted(() => {
  loadClubs()
  loadMyClubs()
})
</script>

<style scoped>
.club-list-container {
  min-height: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding: 24px 32px;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(79, 70, 229, 0.3);
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
  opacity: 0.9;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.action-btn :deep(.el-button) {
  padding: 10px 20px;
  border-radius: 10px;
  font-weight: 500;
}

.search-card {
  margin-bottom: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.search-card :deep(.el-card__body) {
  padding: 16px 24px;
}

.search-form {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-select :deep(.el-select) {
  width: 180px;
}

.search-btn :deep(.el-button),
.reset-btn :deep(.el-button) {
  padding: 8px 20px;
  border-radius: 8px;
}

.skeleton-loading {
  padding: 20px;
}

.club-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.club-card {
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;
}

.club-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
  border-color: #cbd5e1;
}

.club-card :deep(.el-card__body) {
  padding: 24px;
}

.club-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.club-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.club-logo {
  width: 100%;
  height: 100%;
  border-radius: 14px;
}

.club-icon {
  font-size: 28px;
  color: #6366f1;
}

.club-badge :deep(.el-tag) {
  border-radius: 6px;
  padding: 4px 12px;
}

.club-name {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 12px;
}

.club-description {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.club-info {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 10px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #64748b;
}

.info-icon {
  font-size: 16px;
  color: #94a3b8;
}

.club-actions {
  display: flex;
  gap: 12px;
}

.join-btn :deep(.el-button),
.detail-btn :deep(.el-button) {
  flex: 1;
  padding: 10px;
  border-radius: 10px;
  font-weight: 500;
}

.join-btn :deep(.el-button--primary) {
  background: linear-gradient(135deg, #4f46e5 0%, #6366f1 100%);
  border: none;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 16px;
  margin-bottom: 32px;
}

.empty-icon-wrapper {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  border-radius: 50%;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon {
  font-size: 40px;
  color: #94a3b8;
}

.empty-text {
  font-size: 16px;
  color: #94a3b8;
  margin: 0 0 20px;
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
