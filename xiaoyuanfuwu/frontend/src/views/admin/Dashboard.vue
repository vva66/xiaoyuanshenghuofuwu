<template>
  <div class="dashboard-container">
    <div class="page-header">
      <div class="header-title">
        <h2 class="page-title">数据概览</h2>
        <p class="page-subtitle">实时监控系统运营数据</p>
      </div>
    </div>

    <div class="stats-grid">
      <el-card class="stat-card" v-for="(item, index) in statItems" :key="index">
        <div class="stat-icon-wrapper" :style="{ background: item.bgColor }">
          <el-icon class="stat-icon"><component :is="item.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-number">{{ item.value }}</div>
          <div class="stat-label">{{ item.label }}</div>
        </div>
        <div class="stat-trend" :class="item.trend > 0 ? 'up' : 'down'">
          <el-icon><component :is="item.trend > 0 ? ArrowUp : ArrowDown" /></el-icon>
          <span>{{ Math.abs(item.trend) }}%</span>
        </div>
      </el-card>
    </div>

    <div class="charts-grid">
      <el-card class="chart-card">
        <div class="card-header">
          <h3 class="card-title">
            <el-icon><Star /></el-icon>
            活动分类统计
          </h3>
        </div>
        <div ref="activityChartRef" class="chart-container"></div>
      </el-card>

      <el-card class="chart-card">
        <div class="card-header">
          <h3 class="card-title">
            <el-icon><ChatDotRound /></el-icon>
            帖子分类统计
          </h3>
        </div>
        <div ref="postChartRef" class="chart-container"></div>
      </el-card>

      <el-card class="chart-card full-width">
        <div class="card-header">
          <h3 class="card-title">
            <el-icon><School /></el-icon>
            用户学院分布
          </h3>
        </div>
        <div ref="collegeChartRef" class="chart-container"></div>
      </el-card>
    </div>

    <div class="content-grid">
      <el-card class="data-card">
        <div class="card-header">
          <h3 class="card-title">
            <el-icon><Calendar /></el-icon>
            最近活动
          </h3>
        </div>
        <div class="activity-list">
          <div class="activity-item" v-for="activity in recentActivities" :key="activity.title">
            <div class="activity-info">
              <h4 class="activity-title">{{ activity.title }}</h4>
              <span class="activity-time">{{ activity.createTime }}</span>
            </div>
            <div class="activity-participants">
              <el-icon class="participant-icon"><User /></el-icon>
              <span>{{ activity.currentParticipants }} 人参与</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="data-card">
        <div class="card-header">
          <h3 class="card-title">
            <el-icon><ChatDotRound /></el-icon>
            最近帖子
          </h3>
        </div>
        <div class="post-list">
          <div class="post-item" v-for="post in recentPosts" :key="post.title">
            <div class="post-info">
              <h4 class="post-title">{{ post.title }}</h4>
              <div class="post-meta">
                <el-icon class="meta-icon"><User /></el-icon>
                <span>{{ post.publisherName }}</span>
              </div>
            </div>
            <span class="post-time">{{ post.createTime }}</span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, markRaw, nextTick } from 'vue'
import { User, Star, ChatDotRound, Flag, Calendar, ArrowUp, ArrowDown, School, Food, Shop } from '@element-plus/icons-vue'
import { getStatistics, getActivityList, getPostList } from '@/api/admin'
import * as echarts from 'echarts'

const statItems = ref([])
const recentActivities = ref([])
const recentPosts = ref([])
const activityChartRef = ref(null)
const postChartRef = ref(null)
const collegeChartRef = ref(null)

const loadStats = async () => {
  try {
    const stats = await getStatistics()
    statItems.value = [
      {
        icon: markRaw(User),
        value: (stats.userCount || 0).toLocaleString(),
        label: '用户总数',
        bgColor: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)',
        trend: 12
      },
      {
        icon: markRaw(Star),
        value: (stats.activityCount || 0).toLocaleString(),
        label: '活动总数',
        bgColor: 'linear-gradient(135deg, #10b981 0%, #059669 100%)',
        trend: 8
      },
      {
        icon: markRaw(ChatDotRound),
        value: (stats.postCount || 0).toLocaleString(),
        label: '帖子总数',
        bgColor: 'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)',
        trend: 15
      },
      {
        icon: markRaw(Flag),
        value: (stats.clubCount || 0).toLocaleString(),
        label: '社团总数',
        bgColor: 'linear-gradient(135deg, #ec4899 0%, #db2777 100%)',
        trend: -2
      },
      {
        icon: markRaw(Shop),
        value: (stats.canteenCount || 0).toLocaleString(),
        label: '食堂总数',
        bgColor: 'linear-gradient(135deg, #06b6d4 0%, #0891b2 100%)',
        trend: 5
      },
      {
        icon: markRaw(Food),
        value: (stats.dishCount || 0).toLocaleString(),
        label: '菜品总数',
        bgColor: 'linear-gradient(135deg, #f43f5e 0%, #e11d48 100%)',
        trend: 10
      }
    ]

    nextTick(() => {
      renderActivityChart(stats.activityCategoryStats || {})
      renderPostChart(stats.postCategoryStats || {})
      renderCollegeChart(stats.collegeStats || {})
    })
  } catch (e) {
    statItems.value = []
  }
}

const renderActivityChart = (data) => {
  if (!activityChartRef.value) return
  const chart = echarts.init(activityChartRef.value)
  const entries = Object.entries(data)
  const colors = ['#6366f1', '#10b981', '#f59e0b', '#ec4899', '#06b6d4', '#f43f5e', '#8b5cf6', '#14b8a6']
  chart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: '5%', left: 'center', textStyle: { fontSize: 12 } },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}\n{d}%' },
      labelLine: { show: true },
      data: entries.map(([name, value], i) => ({
        name, value,
        itemStyle: { color: colors[i % colors.length] }
      }))
    }]
  })
  window.addEventListener('resize', () => chart.resize())
}

const renderPostChart = (data) => {
  if (!postChartRef.value) return
  const chart = echarts.init(postChartRef.value)
  const entries = Object.entries(data)
  const colors = ['#6366f1', '#10b981', '#f59e0b', '#ec4899', '#06b6d4', '#f43f5e']
  chart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: entries.map(([name]) => name),
      axisLabel: { interval: 0, rotate: 15, fontSize: 11 }
    },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{
      type: 'bar',
      data: entries.map(([, value], i) => ({
        value,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: colors[i % colors.length] },
            { offset: 1, color: colors[i % colors.length] + '80' }
          ]),
          borderRadius: [6, 6, 0, 0]
        }
      })),
      barWidth: '50%'
    }]
  })
  window.addEventListener('resize', () => chart.resize())
}

const renderCollegeChart = (data) => {
  if (!collegeChartRef.value) return
  const chart = echarts.init(collegeChartRef.value)
  const entries = Object.entries(data).sort((a, b) => b[1] - a[1])
  const colors = ['#6366f1', '#10b981', '#f59e0b', '#ec4899', '#06b6d4', '#f43f5e', '#8b5cf6', '#14b8a6', '#f97316', '#64748b']
  chart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: entries.map(([name]) => name),
      axisLabel: { interval: 0, rotate: 20, fontSize: 11 }
    },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{
      type: 'bar',
      data: entries.map(([, value], i) => ({
        value,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: colors[i % colors.length] },
            { offset: 1, color: colors[i % colors.length] + '80' }
          ]),
          borderRadius: [6, 6, 0, 0]
        }
      })),
      barWidth: '40%'
    }]
  })
  window.addEventListener('resize', () => chart.resize())
}

const loadRecentActivities = async () => {
  try {
    const res = await getActivityList({ pageNum: 1, pageSize: 3 })
    recentActivities.value = (res.records || []).map(item => ({
      title: item.title,
      currentParticipants: item.currentParticipants || 0,
      createTime: item.createTime || ''
    }))
  } catch (e) {
    recentActivities.value = []
  }
}

const loadRecentPosts = async () => {
  try {
    const res = await getPostList({ pageNum: 1, pageSize: 3 })
    recentPosts.value = (res.records || []).map(item => ({
      title: item.title,
      publisherName: item.publisherName || '',
      createTime: item.createTime || ''
    }))
  } catch (e) {
    recentPosts.value = []
  }
}

onMounted(() => {
  loadStats()
  loadRecentActivities()
  loadRecentPosts()
})
</script>

<style scoped>
.dashboard-container {
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

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: none;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon {
  font-size: 28px;
  color: white;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  margin: 4px 0 0;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  padding: 4px 10px;
  border-radius: 12px;
}

.stat-trend.up {
  color: #059669;
  background: #d1fae5;
}

.stat-trend.down {
  color: #dc2626;
  background: #fee2e2;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: none;
}

.chart-card.full-width {
  grid-column: 1 / -1;
}

.chart-container {
  width: 100%;
  height: 350px;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.data-card {
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: none;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.activity-list,
.post-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.activity-item,
.post-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8fafc;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.activity-item:hover,
.post-item:hover {
  background: #f1f5f9;
}

.activity-info,
.post-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.activity-title,
.post-title {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.activity-time,
.post-time {
  font-size: 13px;
  color: #94a3b8;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 6px;
}

.meta-icon {
  font-size: 12px;
  color: #94a3b8;
}

.post-meta span {
  font-size: 13px;
  color: #64748b;
}

.activity-participants {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #dbeafe;
  border-radius: 12px;
}

.participant-icon {
  font-size: 14px;
  color: #2563eb;
}

.activity-participants span {
  font-size: 13px;
  color: #1d4ed8;
  font-weight: 500;
}
</style>
