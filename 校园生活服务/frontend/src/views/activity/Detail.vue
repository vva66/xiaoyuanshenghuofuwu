<template>
  <div class="activity-detail-container">
    <el-page-header @back="$router.back()" :title="'返回'" />
    
    <el-skeleton v-if="loading" :rows="10" animated />
    
    <div v-else class="detail-content">
      <h2 class="activity-title">{{ activity.title }}</h2>
      
      <div class="activity-meta">
        <el-tag>{{ activity.category }}</el-tag>
        <span><el-icon><Clock /></el-icon> {{ formatTime(activity.startTime) }} - {{ formatTime(activity.endTime) }}</span>
        <span><el-icon><Location /></el-icon> {{ activity.location }}</span>
        <span><el-icon><User /></el-icon> {{ activity.currentParticipants }}/{{ activity.maxParticipants }} 人参与</span>
      </div>
      
      <img v-if="activity.coverImage" :src="activity.coverImage" class="cover-image" />
      
      <el-divider />
      
      <div class="activity-desc" v-html="activity.content"></div>
      
      <el-divider />
      
      <div class="publisher-info">
        <h4>发布者信息</h4>
        <p>发布人：{{ activity.publisherName }}</p>
      </div>
      
      <div class="action-bar">
        <el-button type="primary" size="large" @click="handleRegister">立即报名</el-button>
        <el-button size="large" @click="handleShare">分享</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getActivityDetail } from '@/api/activity'
import dayjs from 'dayjs'

const route = useRoute()
const loading = ref(true)
const activity = ref({})

const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

const loadDetail = async () => {
  try {
    loading.value = true
    activity.value = await getActivityDetail(route.params.id)
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleRegister = () => {
  ElMessage.success('报名成功')
}

const handleShare = () => {
  ElMessage.success('分享功能开发中')
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.activity-detail-container {
  padding: 20px;
  background: white;
  border-radius: 8px;
}

.activity-title {
  margin: 20px 0;
  color: #303133;
  font-size: 24px;
}

.activity-meta {
  display: flex;
  gap: 20px;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.activity-meta span {
  color: #909399;
  display: flex;
  align-items: center;
  gap: 5px;
}

.cover-image {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
  border-radius: 8px;
  margin: 20px 0;
}

.activity-desc {
  line-height: 1.8;
  color: #606266;
}

.publisher-info {
  margin: 20px 0;
}

.publisher-info h4 {
  margin-bottom: 10px;
  color: #303133;
}

.action-bar {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}
</style>
