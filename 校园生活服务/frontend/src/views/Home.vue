<template>
  <div class="home-container">
    <div class="hero-section">
      <div class="hero-bg-pattern"></div>
      <div class="hero-content">
        <div class="hero-top">
          <div class="hero-welcome">
            <div class="avatar-wrapper">
              <el-avatar :size="48" :src="userAvatar">
                <el-icon :size="24"><User /></el-icon>
              </el-avatar>
            </div>
            <div class="welcome-text">
              <h1 class="greeting">{{ greeting }}，<span class="username">{{ username }}</span></h1>
              <p class="subtitle">欢迎来到校园服务系统，开启你的精彩校园生活</p>
            </div>
          </div>
          <div class="hero-info">
            <div class="info-item">
              <el-icon><Calendar /></el-icon>
              <span>{{ todayDate }}</span>
            </div>
            <div class="info-divider"></div>
            <div class="info-item">
              <el-icon><Clock /></el-icon>
              <span>{{ currentWeek }}</span>
            </div>
          </div>
        </div>
        <div class="hero-stats">
          <div class="stat-item" v-for="stat in heroStats" :key="stat.label">
            <span class="stat-number">{{ stat.number }}</span>
            <span class="stat-label">{{ stat.label }}</span>
          </div>
        </div>
      </div>
      <div class="hero-decoration">
        <div class="decoration-circle circle-1"></div>
        <div class="decoration-circle circle-2"></div>
        <div class="decoration-circle circle-3"></div>
        <div class="decoration-circle circle-4"></div>
      </div>
    </div>

    <div class="quick-access-section" v-if="recentModules.length > 0">
      <h2 class="section-title">
        <el-icon><Clock /></el-icon>
        最近使用
      </h2>
      <div class="quick-access-list">
        <div
          v-for="item in recentModules"
          :key="item.path"
          class="quick-access-item"
          @click="$router.push(item.path)"
        >
          <div class="quick-icon" :style="{ background: item.gradient }">
            <el-icon><component :is="item.icon" /></el-icon>
          </div>
          <span>{{ item.title }}</span>
        </div>
      </div>
    </div>

    <div class="modules-section">
      <h2 class="section-title">
        <el-icon><Grid /></el-icon>
        功能模块
      </h2>
      <div class="modules-grid">
        <el-card
          v-for="module in modules"
          :key="module.path"
          class="module-card"
          shadow="hover"
          @click="handleModuleClick(module)"
        >
          <div class="module-icon-wrapper" :style="{ background: module.gradient }">
            <el-icon class="module-icon"><component :is="module.icon" /></el-icon>
          </div>
          <h3 class="module-title">{{ module.title }}</h3>
          <p class="module-desc">{{ module.desc }}</p>
        </el-card>
      </div>
    </div>

    <div class="notice-section">
      <div class="section-header">
        <h2 class="section-title">
          <el-icon><Bell /></el-icon>
          通知公告
        </h2>
        <el-button type="text" class="view-all-btn" @click="handleViewAllNotices">
          查看全部
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      <el-card class="notice-card" shadow="never">
        <div class="notice-list">
          <div
            v-for="(notice, index) in visibleNotices"
            :key="index"
            class="notice-item"
            @click="handleNoticeClick(notice)"
          >
            <span class="notice-dot" :style="{ background: notice.color }"></span>
            <span class="notice-tag" :style="{ background: notice.tagBg, color: notice.tagColor }">{{ notice.tag }}</span>
            <span class="notice-title">{{ notice.title }}</span>
            <span class="notice-time">{{ notice.time }}</span>
          </div>
        </div>
        <div class="notice-marquee" v-if="notices.length > 3">
          <el-icon class="marquee-icon"><WarningFilled /></el-icon>
          <div class="marquee-text-wrapper">
            <div class="marquee-text" :style="{ animationDuration: `${notices.length * 3}s` }">
              <span v-for="(notice, index) in notices" :key="index" class="marquee-item">
                {{ notice.title }}
                <span class="marquee-separator" v-if="index < notices.length - 1">•</span>
              </span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <div class="activities-section">
      <div class="section-header">
        <h2 class="section-title">
          <el-icon><Star /></el-icon>
          推荐活动
        </h2>
        <el-button type="text" class="view-all-btn" @click="$router.push('/activity/list')">
          查看全部
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>

      <el-skeleton v-if="loading" :rows="3" animated class="skeleton-loading" />

      <div v-else-if="activities.length === 0" class="empty-state">
        <div class="empty-icon-wrapper">
          <el-icon class="empty-icon"><Calendar /></el-icon>
        </div>
        <p class="empty-text">暂无推荐活动</p>
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
              <span class="participants">{{ activity.currentParticipants || 0 }}/{{ activity.maxParticipants || 50 }}人参与</span>
              <el-button type="primary" size="small" class="join-btn">立即报名</el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <div class="stats-overview-section">
      <h2 class="section-title">
        <el-icon><DataAnalysis /></el-icon>
        校园数据概览
      </h2>
      <div class="stats-grid">
        <div class="stats-card" v-for="stat in overviewStats" :key="stat.label">
          <div class="stats-card-icon" :style="{ background: stat.bg }">
            <el-icon :color="stat.color"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stats-card-info">
            <span class="stats-card-number">{{ stat.number }}</span>
            <span class="stats-card-label">{{ stat.label }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { getRecommendActivities } from '@/api/activity'
import dayjs from 'dayjs'
import {
  School, Star, Calendar, Food, Flag, ChatDotRound,
  Grid, ArrowRight, MapLocation, Clock, Picture, User,
  Bell, DataAnalysis, WarningFilled,
  Edit, Document, HomeFilled, InfoFilled, TrophyBase,
  Collection, Reading, CoffeeCup, TrendCharts
} from '@element-plus/icons-vue'

const router = useRouter()

const username = computed(() => localStorage.getItem('username') || '同学')
const userAvatar = computed(() => {
  const stored = localStorage.getItem('avatar') || '';
  if (stored.includes('pexels.com')) {
    localStorage.removeItem('avatar');
    return '';
  }
  return stored;
});

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const todayDate = computed(() => {
  return dayjs().format('YYYY年MM月DD日 dddd')
})

const currentWeek = computed(() => {
  const startOfYear = dayjs().startOf('year')
  const weekNum = dayjs().diff(startOfYear, 'week') + 1
  return `第${weekNum}周`
})

const heroStats = [
  { number: '100+', label: '校园活动' },
  { number: '50+', label: '学生社团' },
  { number: '10000+', label: '服务师生' }
]

const modules = [
  {
    title: '迎新指南',
    desc: '了解报到流程、宿舍分配等信息',
    path: '/freshman/guide',
    icon: markRaw(School),
    gradient: 'linear-gradient(135deg, #10b981 0%, #059669 100%)'
  },
  {
    title: '校园活动',
    desc: '查看和参与各类校园活动',
    path: '/activity/list',
    icon: markRaw(Star),
    gradient: 'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)'
  },
  {
    title: '课表查询',
    desc: '查看个人课程安排',
    path: '/course/schedule',
    icon: markRaw(Calendar),
    gradient: 'linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)'
  },
  {
    title: '食堂推荐',
    desc: '探索美食，查看评分',
    path: '/canteen/list',
    icon: markRaw(Food),
    gradient: 'linear-gradient(135deg, #ef4444 0%, #dc2626 100%)'
  },
  {
    title: '社团活动',
    desc: '加入社团，丰富校园生活',
    path: '/club/list',
    icon: markRaw(Flag),
    gradient: 'linear-gradient(135deg, #6366f1 0%, #4f46e5 100%)'
  },
  {
    title: '互助广场',
    desc: '发布求助，互帮互助',
    path: '/help/list',
    icon: markRaw(ChatDotRound),
    gradient: 'linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%)'
  },
  {
    title: '校园地图',
    desc: '校园导航，路线规划',
    path: '/campus/map',
    icon: markRaw(MapLocation),
    gradient: 'linear-gradient(135deg, #06b6d4 0%, #0891b2 100%)'
  }
]

const recentModules = ref([])

const notices = [
  { title: '2025级新生报到须知及入学流程安排', tag: '重要', time: '2天前', color: '#ef4444', tagBg: '#fef2f2', tagColor: '#ef4444' },
  { title: '校园歌手大赛报名通道已开启，截止至本月底', tag: '活动', time: '3天前', color: '#f59e0b', tagBg: '#fffbeb', tagColor: '#f59e0b' },
  { title: '图书馆期末周延长开放时间通知', tag: '通知', time: '5天前', color: '#3b82f6', tagBg: '#eff6ff', tagColor: '#3b82f6' },
  { title: '关于2025年寒假放假安排的通知', tag: '重要', time: '1周前', color: '#ef4444', tagBg: '#fef2f2', tagColor: '#ef4444' },
  { title: '第二食堂新窗口开业，推出特色菜品', tag: '美食', time: '1周前', color: '#10b981', tagBg: '#ecfdf5', tagColor: '#10b981' },
  { title: '社团招新季：50+社团等你来加入', tag: '社团', time: '2周前', color: '#6366f1', tagBg: '#eef2ff', tagColor: '#6366f1' }
]

const visibleNotices = computed(() => notices.slice(0, 3))

const overviewStats = [
  { number: '100+', label: '校园活动', icon: markRaw(Star), bg: '#fef3c7', color: '#f59e0b' },
  { number: '50+', label: '学生社团', icon: markRaw(Flag), bg: '#e0e7ff', color: '#6366f1' },
  { number: '10000+', label: '服务师生', icon: markRaw(User), bg: '#dbeafe', color: '#3b82f6' },
  { number: '500+', label: '互助帖子', icon: markRaw(ChatDotRound), bg: '#ede9fe', color: '#8b5cf6' },
  { number: '30+', label: '食堂菜品', icon: markRaw(Food), bg: '#fce4ec', color: '#ef4444' },
  { number: '200+', label: '课程资源', icon: markRaw(Reading), bg: '#d1fae5', color: '#10b981' }
]

const loading = ref(true)
const activities = ref([])

const formatTime = (time) => {
  return dayjs(time).format('MM-DD HH:mm')
}

function handleModuleClick(module) {
  const existing = recentModules.value.find(m => m.path === module.path)
  if (existing) {
    const idx = recentModules.value.indexOf(existing)
    recentModules.value.splice(idx, 1)
  }
  recentModules.value.unshift(module)
  if (recentModules.value.length > 4) {
    recentModules.value = recentModules.value.slice(0, 4)
  }
  localStorage.setItem('recentModules', JSON.stringify(recentModules.value.map(m => m.path)))
  router.push(module.path)
}

function handleNoticeClick(notice) {
  router.push('/activity/list')
}

function handleViewAllNotices() {
  router.push('/activity/list')
}

const loadActivities = async () => {
  try {
    loading.value = true
    try {
      activities.value = await getRecommendActivities()
    } catch (error) {
      activities.value = [
        { id: 1, title: '校园歌手大赛', description: '展示你的歌喉，成为校园之星！', location: '学生活动中心', startTime: '2024-01-20 19:00', coverImage: 'https://picsum.photos/seed/activity-singer/800/400', category: '文体活动', currentParticipants: 28, maxParticipants: 50 },
        { id: 2, title: '学术讲座：人工智能前沿', description: '邀请知名专家分享AI最新发展', location: '图书馆报告厅', startTime: '2024-01-22 14:00', coverImage: 'https://picsum.photos/seed/activity-python/800/400', category: '学术讲座', currentParticipants: 45, maxParticipants: 60 },
        { id: 3, title: '志愿者招募', description: '加入志愿者团队，奉献爱心', location: '大学生活动中心', startTime: '2024-01-25 10:00', coverImage: 'https://picsum.photos/seed/activity-volunteer/800/400', category: '志愿服务', currentParticipants: 12, maxParticipants: 30 }
      ]
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadActivities()
  const savedPaths = JSON.parse(localStorage.getItem('recentModules') || '[]')
  if (savedPaths.length > 0) {
    recentModules.value = savedPaths.map(path => modules.find(m => m.path === path)).filter(Boolean)
  }
})
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px 40px;
}

.hero-section {
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 40px;
  margin: 24px 0 32px;
  overflow: hidden;
  color: #fff;
}

.hero-bg-pattern {
  position: absolute;
  inset: 0;
  background-image: radial-gradient(circle at 20% 50%, rgba(255,255,255,0.1) 0%, transparent 50%),
                    radial-gradient(circle at 80% 20%, rgba(255,255,255,0.08) 0%, transparent 50%),
                    radial-gradient(circle at 50% 80%, rgba(255,255,255,0.06) 0%, transparent 50%);
  pointer-events: none;
}

.hero-content {
  position: relative;
  z-index: 1;
}

.hero-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.hero-welcome {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-wrapper {
  flex-shrink: 0;
  border: 3px solid rgba(255,255,255,0.4);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.welcome-text h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  line-height: 1.4;
}

.welcome-text .username {
  background: linear-gradient(90deg, #fbbf24, #f59e0b);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-text .subtitle {
  font-size: 14px;
  opacity: 0.85;
  margin: 4px 0 0;
}

.hero-info {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255,255,255,0.15);
  backdrop-filter: blur(10px);
  padding: 8px 16px;
  border-radius: 12px;
  font-size: 13px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.info-divider {
  width: 1px;
  height: 16px;
  background: rgba(255,255,255,0.3);
}

.hero-stats {
  display: flex;
  gap: 40px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-number {
  font-size: 32px;
  font-weight: 800;
  line-height: 1.2;
  color: #fff;
  text-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.stat-label {
  font-size: 14px;
  color: rgba(255,255,255,0.9);
  font-weight: 500;
}

.hero-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  border: 2px solid rgba(255,255,255,0.08);
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  right: -80px;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -60px;
  right: 20%;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 30%;
  right: 10%;
}

.circle-4 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 60%;
}

.quick-access-section {
  margin-bottom: 32px;
}

.quick-access-list {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.quick-access-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #f8fafc;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 13px;
  color: #475569;
}

.quick-access-item:hover {
  background: #f1f5f9;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.quick-icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 14px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 16px;
}

.section-title .el-icon {
  font-size: 20px;
  color: #667eea;
}

.modules-section {
  margin-bottom: 32px;
}

.modules-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.module-card {
  border-radius: 16px;
  border: 1px solid #f1f5f9;
  cursor: pointer;
  transition: all 0.3s ease;
}

.module-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.1);
}

.module-card :deep(.el-card__body) {
  padding: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.module-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  transition: transform 0.3s ease;
}

.module-card:hover .module-icon-wrapper {
  transform: scale(1.1);
}

.module-icon {
  font-size: 28px;
  color: #fff;
}

.module-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 8px;
}

.module-desc {
  font-size: 13px;
  color: #94a3b8;
  margin: 0;
  line-height: 1.5;
}

.notice-section {
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header .section-title {
  margin: 0;
}

.view-all-btn {
  font-size: 13px;
  color: #667eea;
  display: flex;
  align-items: center;
  gap: 4px;
}

.view-all-btn:hover {
  color: #764ba2;
}

.notice-card {
  border-radius: 16px;
  border: 1px solid #f1f5f9;
}

.notice-card :deep(.el-card__body) {
  padding: 16px 20px;
}

.notice-list {
  display: flex;
  flex-direction: column;
}

.notice-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 0;
  cursor: pointer;
  transition: all 0.2s ease;
  border-bottom: 1px solid #f8fafc;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-item:hover {
  padding-left: 4px;
}

.notice-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex-shrink: 0;
}

.notice-tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
  flex-shrink: 0;
  font-weight: 500;
}

.notice-title {
  flex: 1;
  font-size: 14px;
  color: #334155;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-time {
  font-size: 12px;
  color: #94a3b8;
  flex-shrink: 0;
}

.notice-marquee {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 0 4px;
  border-top: 1px solid #f1f5f9;
  margin-top: 4px;
}

.marquee-icon {
  color: #f59e0b;
  font-size: 14px;
  flex-shrink: 0;
}

.marquee-text-wrapper {
  flex: 1;
  overflow: hidden;
  height: 20px;
}

.marquee-text {
  display: flex;
  white-space: nowrap;
  animation: marquee 20s linear infinite;
}

.marquee-item {
  font-size: 13px;
  color: #64748b;
  margin-right: 8px;
}

.marquee-separator {
  margin: 0 8px;
  color: #cbd5e1;
}

@keyframes marquee {
  0% { transform: translateX(100%); }
  100% { transform: translateX(-100%); }
}

.activities-section {
  margin-bottom: 32px;
}

.skeleton-loading {
  padding: 20px;
  border-radius: 16px;
  background: #fff;
  border: 1px solid #f1f5f9;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48px 20px;
  background: #fff;
  border-radius: 16px;
  border: 1px solid #f1f5f9;
}

.empty-icon-wrapper {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.empty-icon {
  font-size: 28px;
  color: #94a3b8;
}

.empty-text {
  font-size: 14px;
  color: #94a3b8;
  margin: 0;
}

.activities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.activity-card {
  border-radius: 16px;
  border: 1px solid #f1f5f9;
  overflow: hidden;
  transition: all 0.3s ease;
}

.activity-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.1);
}

.activity-card :deep(.el-card__body) {
  padding: 0;
}

.activity-image-wrapper {
  position: relative;
  width: 100%;
  height: 180px;
  overflow: hidden;
}

.activity-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.activity-card:hover .activity-image {
  transform: scale(1.05);
}

.activity-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #e0e7ff 0%, #f0e6ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-icon {
  font-size: 48px;
  color: #a5b4fc;
}

.activity-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: rgba(0,0,0,0.6);
  backdrop-filter: blur(4px);
  color: #fff;
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 20px;
}

.activity-content {
  padding: 16px 20px 20px;
}

.activity-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.activity-desc {
  font-size: 13px;
  color: #94a3b8;
  margin: 0 0 12px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.activity-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #64748b;
}

.meta-item .el-icon {
  font-size: 14px;
}

.activity-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.participants {
  font-size: 12px;
  color: #94a3b8;
}

.join-btn {
  border-radius: 8px;
}

.stats-overview-section {
  margin-bottom: 32px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 16px;
}

.stats-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 16px;
  border: 1px solid #f1f5f9;
  transition: all 0.3s ease;
}

.stats-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.06);
}

.stats-card-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stats-card-icon .el-icon {
  font-size: 24px;
}

.stats-card-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stats-card-number {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.2;
}

.stats-card-label {
  font-size: 13px;
  color: #94a3b8;
}

@media (max-width: 768px) {
  .hero-section {
    padding: 24px 20px;
  }

  .hero-top {
    flex-direction: column;
    gap: 16px;
  }

  .hero-welcome {
    flex-direction: column;
    text-align: center;
  }

  .hero-info {
    width: 100%;
    justify-content: center;
  }

  .hero-stats {
    justify-content: center;
    gap: 24px;
  }

  .stat-number {
    font-size: 22px;
  }

  .modules-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }

  .activities-grid {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }

  .welcome-text h1 {
    font-size: 20px;
  }

  .notice-item {
    flex-wrap: wrap;
    gap: 6px;
  }

  .notice-time {
    width: 100%;
    padding-left: 16px;
  }
}

@media (max-width: 480px) {
  .home-container {
    padding: 0 12px 24px;
  }

  .hero-section {
    padding: 20px 16px;
    border-radius: 16px;
  }

  .modules-grid {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .hero-stats {
    gap: 16px;
  }

  .stat-number {
    font-size: 18px;
  }
}
</style>