<template>
  <div class="club-detail-container">
    <div class="breadcrumb-wrapper">
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item>
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-breadcrumb-item>
        <el-breadcrumb-item @click="goBack">社团活动</el-breadcrumb-item>
        <el-breadcrumb-item>{{ club.name }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div v-if="loading" class="loading-wrapper">
      <el-loading text="加载中..." class="loading" />
    </div>
    
    <div v-else class="club-detail">
      <el-card class="club-header-card">
        <div class="club-banner">
          <div class="banner-overlay"></div>
          <div class="banner-content">
            <div class="club-icon-wrapper">
              <el-image v-if="club.logo" :src="club.logo" class="club-logo" fit="cover" @error="onLogoError" />
              <el-icon v-else class="club-icon"><OfficeBuilding /></el-icon>
            </div>
            <div class="club-info">
              <h1 class="club-title">{{ club.name }}</h1>
              <el-tag :type="getTagType(club.category)" size="large" class="category-tag">{{ club.category }}</el-tag>
            </div>
          </div>
        </div>
        
        <div class="club-stats-row">
          <div class="stat-item">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <span class="stat-value">{{ club.memberCount }}</span>
              <span class="stat-label">成员数</span>
            </div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <span class="stat-value">{{ club.presidentName || '暂无' }}</span>
              <span class="stat-label">社长</span>
            </div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <div class="stat-icon">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-content">
              <span class="stat-value">{{ formatDate(club.establishedDate) }}</span>
              <span class="stat-label">成立时间</span>
            </div>
          </div>
        </div>
      </el-card>

      <div class="content-grid">
        <el-card class="info-card">
          <h3 class="card-title">
            <el-icon><InfoFilled /></el-icon>
            社团简介
          </h3>
          <p class="club-description">{{ club.description }}</p>
        </el-card>

        <el-card class="contact-card">
          <h3 class="card-title">
            <el-icon><Phone /></el-icon>
            联系方式
          </h3>
          <p class="contact-info">{{ club.contactInfo || '暂无联系方式' }}</p>
        </el-card>
      </div>

      <div class="actions-card">
        <div class="action-buttons">
          <el-button 
            :type="isJoined ? 'default' : 'primary'" 
            size="large" 
            class="action-btn join-btn"
            :disabled="isJoined"
            @click="handleJoin"
          >
            <el-icon><CirclePlus /></el-icon>
            {{ isJoined ? '已加入' : '加入社团' }}
          </el-button>
          <el-button v-if="isPresident" type="warning" size="large" class="action-btn manage-btn" @click="goToManage">
            <el-icon><Setting /></el-icon>
            社团管理
          </el-button>
          <el-button size="large" class="action-btn back-btn" @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回列表
          </el-button>
        </div>
      </div>

      <el-card class="members-card">
        <h3 class="card-title">
          <el-icon><User /></el-icon>
          社团成员 ({{ members.length }}人)
        </h3>
        <div class="members-list">
          <div v-for="member in members.slice(0, 12)" :key="member.userId" class="member-item">
            <el-avatar :size="40">{{ member.userName?.charAt(0) }}</el-avatar>
            <div class="member-info">
              <span class="member-name">{{ member.userName }}</span>
              <span class="member-role" v-if="member.userId === club.presidentId">社长</span>
            </div>
          </div>
          <div v-if="members.length > 12" class="member-more">
            <el-button link @click="goToManage">查看全部 {{ members.length }} 人</el-button>
          </div>
        </div>
      </el-card>

      <el-card class="activities-card">
        <div class="card-header-row">
          <h3 class="card-title">
            <el-icon><Calendar /></el-icon>
            社团活动
          </h3>
          <el-button 
            v-if="isPresident" 
            type="primary" 
            size="small" 
            class="add-activity-btn"
            @click="showCreateActivity = true"
          >
            <el-icon><Plus /></el-icon>
            发布活动
          </el-button>
        </div>
        
        <div v-if="activities.length === 0" class="empty-activities">
          <div class="empty-icon-wrapper">
            <el-icon class="empty-icon"><Calendar /></el-icon>
          </div>
          <p class="empty-text">该社团暂无活动</p>
        </div>
        
        <div v-else class="activities-list">
          <el-card 
            v-for="activity in activities" 
            :key="activity.id" 
            class="activity-card"
          >
            <div class="activity-header">
              <h4 class="activity-title">{{ activity.title }}</h4>
              <el-tag size="small" class="activity-status">
                {{ isActivityRegistered(activity.id) ? '已报名' : '报名中' }}
              </el-tag>
            </div>
            <p class="activity-description">{{ activity.description }}</p>
            <div class="activity-info">
              <div class="info-row">
                <el-icon class="info-icon"><Clock /></el-icon>
                <span>{{ formatDateTime(activity.startTime) }} - {{ formatTime(activity.endTime) }}</span>
              </div>
              <div class="info-row">
                <el-icon class="info-icon"><MapLocation /></el-icon>
                <span>{{ activity.location }}</span>
              </div>
              <div class="info-row">
                <el-icon class="info-icon"><User /></el-icon>
                <span>{{ activity.currentParticipants }} / {{ activity.maxParticipants }} 人</span>
              </div>
            </div>
            <div class="activity-action">
              <el-button 
                :type="isActivityRegistered(activity.id) ? 'default' : 'primary'" 
                size="small"
                :disabled="isActivityRegistered(activity.id) || activity.currentParticipants >= activity.maxParticipants"
                @click="handleRegisterActivity(activity)"
              >
                {{ activity.currentParticipants >= activity.maxParticipants ? '已满员' : (isActivityRegistered(activity.id) ? '已报名' : '立即报名') }}
              </el-button>
            </div>
          </el-card>
        </div>
      </el-card>
    </div>

    <el-dialog v-model="showCreateActivity" title="发布活动" width="600px">
      <el-form :model="activityForm" label-width="100px">
        <el-form-item label="活动标题">
          <el-input v-model="activityForm.title" placeholder="请输入活动标题" />
        </el-form-item>
        <el-form-item label="活动简介">
          <el-input v-model="activityForm.description" type="textarea" :rows="4" placeholder="请输入活动简介" />
        </el-form-item>
        <el-form-item label="活动地点">
          <el-input v-model="activityForm.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="activityForm.startTime" type="datetime" placeholder="选择开始时间" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="activityForm.endTime" type="datetime" placeholder="选择结束时间" />
        </el-form-item>
        <el-form-item label="最大人数">
          <el-input v-model.number="activityForm.maxParticipants" type="number" placeholder="请输入最大参与人数" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateActivity = false">取消</el-button>
        <el-button type="primary" @click="handleCreateActivity">发布活动</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Calendar, HomeFilled, InfoFilled, Phone, Clock, MapLocation, CirclePlus, ArrowLeft, Plus, Setting } from '@element-plus/icons-vue'
import { getClubDetail, joinClub, getMyClubs, getClubActivityList, registerClubActivity, createClubActivity, isPresident as checkIsPresident, getClubMembers } from '@/api/club'

const getTagType = (category) => {
  const typeMap = {
    '学术类': 'info',
    '体育类': 'success',
    '艺术类': 'warning',
    '公益类': 'danger'
  }
  return typeMap[category] || 'info'
}

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const club = ref({})
const myClubs = ref([])
const activities = ref([])
const registeredActivities = ref([])
const showCreateActivity = ref(false)
const isPresident = ref(false)
const members = ref([])

const activityForm = reactive({
  title: '',
  description: '',
  location: '',
  startTime: '',
  endTime: '',
  maxParticipants: 50
})

const userId = localStorage.getItem('userId')

const isJoined = computed(() => {
  return myClubs.value.some(c => c.id === club.value.id)
})

const onLogoError = (event) => {
  event.target.style.display = 'none'
  club.value.logo = null
}

const loadClubDetail = async () => {
  try {
    loading.value = true
    const result = await getClubDetail(route.query.id)
    club.value = result
    await loadActivities()
    await checkPresident()
    await loadMembers()
  } catch (error) {
    console.error(error)
    ElMessage.error('加载社团详情失败')
  } finally {
    loading.value = false
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

const loadActivities = async () => {
  try {
    const result = await getClubActivityList({
      clubId: club.value.id
    })
    activities.value = result || []
  } catch (error) {
    console.error('loadActivities error:', error)
    activities.value = []
  }
}

const checkPresident = async () => {
  try {
    const result = await checkIsPresident(club.value.id, userId)
    isPresident.value = result
  } catch (error) {
    console.error(error)
    isPresident.value = false
  }
}

const loadMembers = async () => {
  try {
    const result = await getClubMembers(club.value.id)
    members.value = result || []
  } catch (error) {
    members.value = []
  }
}

const goToManage = () => {
  router.push('/club/manage/' + club.value.id)
}

const handleJoin = async () => {
  if (isJoined.value) {
    ElMessage.info('您已经加入该社团')
    return
  }
  
  try {
    await joinClub({
      clubId: club.value.id,
      userId: userId,
      userName: localStorage.getItem('username')
    })
    ElMessage.success('加入社团成功')
    club.value.memberCount++
    myClubs.value.push(club.value)
    loadActivities()
  } catch (error) {
    ElMessage.error('加入社团失败')
  }
}

const handleRegisterActivity = async (activity) => {
  try {
    await registerClubActivity({
      activityId: activity.id,
      userId: userId,
      userName: localStorage.getItem('username'),
      studentNo: localStorage.getItem('studentNo')
    })
    ElMessage.success('报名成功')
    activity.currentParticipants++
    registeredActivities.value.push(activity.id)
  } catch (error) {
    ElMessage.error('报名失败')
  }
}

const isActivityRegistered = (activityId) => {
  return registeredActivities.value.includes(activityId)
}

const handleCreateActivity = async () => {
  if (!activityForm.title || !activityForm.location || !activityForm.startTime || !activityForm.endTime) {
    ElMessage.warning('请填写完整的活动信息')
    return
  }
  
  try {
    await createClubActivity({
      clubId: club.value.id,
      clubName: club.value.name,
      title: activityForm.title,
      description: activityForm.description,
      location: activityForm.location,
      startTime: activityForm.startTime,
      endTime: activityForm.endTime,
      maxParticipants: activityForm.maxParticipants || 50,
      userId: userId
    })
    ElMessage.success('活动发布成功')
    showCreateActivity.value = false
    Object.assign(activityForm, {
      title: '',
      description: '',
      location: '',
      startTime: '',
      endTime: '',
      maxParticipants: 50
    })
    loadActivities()
  } catch (error) {
    ElMessage.error('发布活动失败')
  }
}

const goBack = () => {
  router.push('/club/list')
}

const formatDate = (date) => {
  if (!date) return '暂无'
  return new Date(date).toLocaleDateString('zh-CN')
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '暂无'
  return new Date(dateTime).toLocaleString('zh-CN')
}

const formatTime = (dateTime) => {
  if (!dateTime) return '暂无'
  return new Date(dateTime).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

onMounted(() => {
  loadClubDetail()
  loadMyClubs()
})
</script>

<style scoped>
.club-detail-container {
  min-height: 100%;
}

.breadcrumb-wrapper {
  margin-bottom: 20px;
}

.breadcrumb :deep(.el-breadcrumb) {
  font-size: 14px;
}

.breadcrumb :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #6366f1;
  font-weight: 500;
}

.loading-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.club-header-card {
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.club-banner {
  position: relative;
  height: 200px;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.1);
}

.banner-content {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 24px;
}

.club-icon-wrapper {
  width: 100px;
  height: 100px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.club-logo {
  width: 100%;
  height: 100%;
  border-radius: 24px;
}

.club-icon {
  font-size: 48px;
  color: white;
}

.club-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.club-title {
  font-size: 32px;
  font-weight: 700;
  color: white;
  margin: 0;
}

.category-tag :deep(.el-tag) {
  background: rgba(255, 255, 255, 0.25);
  border: none;
  color: white;
  padding: 6px 16px;
  font-size: 14px;
}

.club-stats-row {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 40px;
  padding: 24px;
  background: #f8fafc;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #e0e7ff 0%, #c7d2fe 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon :deep(.el-icon) {
  font-size: 20px;
  color: #6366f1;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
}

.stat-label {
  font-size: 12px;
  color: #94a3b8;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: #e2e8f0;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.info-card,
.contact-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-title :deep(.el-icon) {
  color: #6366f1;
}

.club-description {
  font-size: 14px;
  color: #64748b;
  line-height: 1.7;
  margin: 0;
}

.contact-info {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.actions-card {
  margin-bottom: 24px;
}

.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.action-btn :deep(.el-button) {
  padding: 12px 32px;
  border-radius: 12px;
  font-weight: 500;
  font-size: 15px;
}

.join-btn :deep(.el-button--primary) {
  background: linear-gradient(135deg, #4f46e5 0%, #6366f1 100%);
  border: none;
}

.back-btn :deep(.el-button) {
  background: #f1f5f9;
  border-color: #e2e8f0;
}

.activities-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.card-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.add-activity-btn :deep(.el-button) {
  padding: 8px 16px;
  border-radius: 8px;
}

.empty-activities {
  text-align: center;
  padding: 40px;
}

.empty-icon-wrapper {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  border-radius: 50%;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon {
  font-size: 32px;
  color: #94a3b8;
}

.empty-text {
  font-size: 14px;
  color: #94a3b8;
  margin: 0;
}

.activities-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-card {
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.activity-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.activity-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.activity-status :deep(.el-tag) {
  background: #dcfce7;
  color: #16a34a;
  border: none;
}

.activity-description {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 16px;
}

.activity-info {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 16px;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 8px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #64748b;
}

.info-icon {
  font-size: 14px;
  color: #94a3b8;
}

.activity-action {
  display: flex;
  justify-content: flex-end;
}

.activity-action :deep(.el-button) {
  padding: 6px 16px;
  border-radius: 8px;
}

.members-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  margin-bottom: 24px;
}

.members-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #f8fafc;
  border-radius: 8px;
  min-width: 140px;
}

.member-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.member-name {
  font-size: 13px;
  font-weight: 500;
  color: #1e293b;
}

.member-role {
  font-size: 11px;
  color: #f59e0b;
  font-weight: 500;
}

.member-more {
  width: 100%;
  text-align: center;
  padding-top: 8px;
}

.manage-btn :deep(.el-button) {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  border: none;
  color: white;
}
</style>