<template>
  <div class="help-list-container">
    <div class="page-header">
      <div class="header-title">
        <h2 class="page-title">互助广场</h2>
        <p class="page-subtitle">互帮互助，共建美好校园</p>
      </div>
      <div class="header-action">
        <el-button type="primary" class="publish-btn" @click="$router.push('/help/publish')">
          <el-icon><Plus /></el-icon>
          发布求助
        </el-button>
      </div>
    </div>
    
    <el-card class="tabs-card">
      <div class="search-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索帖子标题或内容..."
          clearable
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>
      <el-tabs v-model="activeCategory" @tab-change="handleTabChange" class="category-tabs">
        <el-tab-pane label="全部" name="" />
        <el-tab-pane label="失物招领" name="失物招领" />
        <el-tab-pane label="学习求助" name="学习求助" />
        <el-tab-pane label="二手交易" name="二手交易" />
        <el-tab-pane label="活动组织" name="活动组织" />
        <el-tab-pane label="其他" name="其他" />
      </el-tabs>
    </el-card>
    
    <el-skeleton v-if="loading" :rows="5" animated class="skeleton-loading" />
    
    <div v-else-if="posts.length === 0" class="empty-state">
      <div class="empty-icon-wrapper">
        <el-icon class="empty-icon"><ChatDotRound /></el-icon>
      </div>
      <p class="empty-text">暂无相关帖子</p>
      <el-button type="primary" @click="$router.push('/help/publish')">发布第一条帖子</el-button>
    </div>
    
    <div v-else class="posts-grid">
      <el-card 
        v-for="post in posts" 
        :key="post.id" 
        class="post-card"
        shadow="hover"
        @click="$router.push(`/help/detail/${post.id}`)"
      >
        <div class="post-header">
          <div class="post-tags">
            <el-tag v-if="post.isTop === 1" type="danger" size="small">置顶</el-tag>
            <el-tag v-if="post.isEssence === 1" type="success" size="small">精华</el-tag>
            <el-tag class="category-tag" size="small">{{ post.category }}</el-tag>
          </div>
          <h3 class="post-title">{{ post.title }}</h3>
        </div>
        <div v-if="post.images" class="post-image">
          <img :src="post.images.split(',')[0]" alt="帖子图片" @error="handleImgError" />
        </div>
        <p class="post-content">{{ post.content.substring(0, 150) }}...</p>
        <div class="post-footer">
          <div class="post-meta">
            <span class="meta-item">
              <el-icon class="meta-icon"><User /></el-icon>
              <span>{{ post.publisherName }}</span>
            </span>
            <span class="meta-item">
              <el-icon class="meta-icon"><Clock /></el-icon>
              <span>{{ formatTime(post.createTime) }}</span>
            </span>
          </div>
          <div class="post-stats">
            <span class="stat-item">
              <el-icon class="stat-icon"><Eye /></el-icon>
              <span>{{ post.viewCount }}</span>
            </span>
            <span class="stat-item">
              <el-icon class="stat-icon"><MessageCircle /></el-icon>
              <span>{{ post.replyCount }}</span>
            </span>
          </div>
        </div>
      </el-card>
    </div>
    
    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        @current-change="loadPosts"
        layout="total, prev, pager, next"
        class="pagination"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPostList } from '@/api/help'
import dayjs from 'dayjs'

const loading = ref(true)
const posts = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const activeCategory = ref('')
const keyword = ref('')

const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

const loadPosts = async () => {
  try {
    loading.value = true
    const result = await getPostList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      category: activeCategory.value,
      keyword: keyword.value || undefined
    })
    posts.value = result.records || []
    total.value = result.total || 0
  } catch (error) {
    console.error(error)
    posts.value = []
  } finally {
    loading.value = false
  }
}

const handleImgError = (e) => {
  e.target.style.display = 'none'
}

const handleSearch = () => {
  pageNum.value = 1
  loadPosts()
}

const handleTabChange = () => {
  pageNum.value = 1
  loadPosts()
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.help-list-container {
  min-height: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 24px 32px;
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 10px 40px rgba(6, 182, 212, 0.3);
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
  padding: 10px 24px;
  border-radius: 10px;
  font-weight: 500;
  background: white;
  color: #0891b2;
  border: none;
}

.tabs-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: none;
  margin-bottom: 24px;
}

.category-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
  padding: 0 20px;
  border-bottom: 1px solid #e2e8f0;
}

.category-tabs :deep(.el-tabs__nav) {
  margin: 0;
}

.search-bar {
  padding: 16px 20px 0;
}

.search-input {
  width: 100%;
}

.search-input :deep(.el-input-group__append) {
  background-color: #06b6d4;
  border-color: #06b6d4;
}

.search-input :deep(.el-input-group__append .el-button) {
  color: white;
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
  background: #e2e8f0;
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

.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.post-card {
  cursor: pointer;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.post-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1);
}

.post-header {
  margin-bottom: 12px;
}

.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 10px;
}

.category-tag {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  color: #fff;
  border: none;
}

.post-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
  line-height: 1.4;
}

.post-image {
  width: 100%;
  height: 180px;
  border-radius: 8px;
  overflow: hidden;
  margin: 12px 0;
}

.post-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.post-card:hover .post-image img {
  transform: scale(1.05);
}

.post-content {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.post-meta {
  display: flex;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #64748b;
}

.meta-icon {
  font-size: 14px;
  color: #94a3b8;
}

.post-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #94a3b8;
}

.stat-icon {
  font-size: 14px;
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
