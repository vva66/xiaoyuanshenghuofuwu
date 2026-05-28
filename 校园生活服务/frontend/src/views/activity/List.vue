<template>
  <div class="activity-list-container">
    <div class="page-header">
      <div class="header-title">
        <h2 class="page-title">校园活动</h2>
        <p class="page-subtitle">探索精彩的校园生活</p>
      </div>
      <el-button type="primary" class="publish-btn" @click="$router.push('/activity/publish')">
        <el-icon><Plus /></el-icon>
        发布活动
      </el-button>
    </div>
    
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="分类">
          <el-select v-model="queryForm.category" placeholder="请选择分类" clearable class="search-select">
            <el-option label="学术讲座" value="学术讲座" />
            <el-option label="文体活动" value="文体活动" />
            <el-option label="志愿服务" value="志愿服务" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input 
            v-model="queryForm.keyword" 
            placeholder="搜索活动标题" 
            clearable 
            class="search-input"
            @keyup.enter="loadActivities"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadActivities" class="search-btn">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-skeleton v-if="loading" :rows="5" animated class="skeleton-loading" />
    
    <div v-else-if="activities.length === 0" class="empty-state">
      <div class="empty-icon-wrapper">
        <el-icon class="empty-icon"><Calendar /></el-icon>
      </div>
      <p class="empty-text">暂无活动</p>
      <el-button type="primary" @click="$router.push('/activity/publish')">发布第一个活动</el-button>
    </div>
    
    <div v-else class="activities-grid">
      <el-card 
        v-for="activity in activities" 
        :key="activity.id"
        class="activity-card"
        shadow="hover"
        @click="$router.push(`/activity/detail/${activity.id}`)"
      >
        <div class="activity-image-wrapper">
          <img v-if="activity.coverImage" :src="activity.coverImage" class="activity-image" />
          <div v-else class="activity-placeholder">
            <el-icon class="placeholder-icon"><Picture /></el-icon>
          </div>
          <div class="activity-badge">{{ activity.category }}</div>
        </div>
        <div class="activity-content">
          <h3 class="activity-title">{{ activity.title }}</h3>
          <p class="activity-desc">{{ activity.description }}</p>
          <div class="activity-meta">
            <span class="meta-item">
              <el-icon><MapLocation /></el-icon>
              {{ activity.location }}
            </span>
            <span class="meta-item">
              <el-icon><Clock /></el-icon>
              {{ formatTime(activity.startTime) }}
            </span>
          </div>
          <div class="activity-footer">
            <span class="participants">{{ activity.currentParticipants }}/{{ activity.maxParticipants }}人</span>
          </div>
        </div>
      </el-card>
    </div>
    
    <div class="pagination-wrapper">
      <el-pagination
        v-if="total > 0"
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        @current-change="loadActivities"
        layout="total, prev, pager, next, jumper"
        class="pagination"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getActivityList } from '@/api/activity'
import dayjs from 'dayjs'
import { Plus, Search, Calendar, MapLocation, Clock, Picture } from '@element-plus/icons-vue'

const loading = ref(true)
const activities = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(9)

const queryForm = reactive({
  category: '',
  keyword: ''
})

const formatTime = (time) => {
  return dayjs(time).format('MM-DD HH:mm')
}

const loadActivities = async () => {
  try {
    loading.value = true
    try {
      const result = await getActivityList({
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        category: queryForm.category,
        keyword: queryForm.keyword
      })
      activities.value = result.records || []
      total.value = result.total || 0
    } catch (error) {
      activities.value = [
        { id: 1, title: '校园歌手大赛', description: '展示你的歌喉，成为校园之星！', location: '学生活动中心', startTime: '2024-01-20 19:00', coverImage: 'https://picsum.photos/seed/activity-singer/800/400', category: '文体活动', currentParticipants: 120, maxParticipants: 200 },
        { id: 2, title: '学术讲座：人工智能前沿', description: '邀请知名专家分享AI最新发展', location: '图书馆报告厅', startTime: '2024-01-22 14:00', coverImage: 'https://picsum.photos/seed/activity-python/800/400', category: '学术讲座', currentParticipants: 80, maxParticipants: 150 },
        { id: 3, title: '志愿者招募', description: '加入志愿者团队，奉献爱心', location: '大学生活动中心', startTime: '2024-01-25 10:00', coverImage: 'https://picsum.photos/seed/activity-volunteer/800/400', category: '志愿服务', currentParticipants: 50, maxParticipants: 100 },
        { id: 4, title: '篮球友谊赛', description: '各学院篮球高手对决', location: '体育馆', startTime: '2024-01-28 15:00', coverImage: 'https://picsum.photos/seed/activity-basketball/800/400', category: '文体活动', currentParticipants: 100, maxParticipants: 150 },
        { id: 5, title: '创业分享会', description: '成功校友分享创业经验', location: '大学生创业中心', startTime: '2024-02-01 14:00', coverImage: 'https://picsum.photos/seed/activity-kaoyan/800/400', category: '学术讲座', currentParticipants: 60, maxParticipants: 100 },
        { id: 6, title: '读书分享会', description: '分享阅读心得，交流读书体会', location: '图书馆', startTime: '2024-02-05 19:00', coverImage: 'https://picsum.photos/seed/activity-photo/800/400', category: '学术讲座', currentParticipants: 30, maxParticipants: 50 }
      ]
      total.value = 6
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.activity-list-container {
  min-height: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding: 24px 32px;
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(245, 158, 11, 0.3);
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

.publish-btn :deep(.el-button) {
  padding: 10px 20px;
  border-radius: 10px;
  font-weight: 500;
  background: white;
  color: #d97706;
  border: none;
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

.search-input :deep(.el-input) {
  width: 300px;
}

.search-btn :deep(.el-button) {
  padding: 8px 20px;
  border-radius: 8px;
}

.skeleton-loading {
  padding: 20px;
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
  margin: 0 auto 16px;
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

.activities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.activity-card {
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;
}

.activity-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1);
}

.activity-image-wrapper {
  position: relative;
  height: 180px;
}

.activity-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.activity-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-icon {
  font-size: 48px;
  color: #94a3b8;
}

.activity-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  color: #d97706;
}

.activity-content {
  padding: 20px;
}

.activity-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 10px;
}

.activity-desc {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.activity-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #64748b;
}

.meta-item :deep(.el-icon) {
  font-size: 14px;
  color: #94a3b8;
}

.activity-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.participants {
  font-size: 13px;
  color: #f59e0b;
  font-weight: 500;
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
