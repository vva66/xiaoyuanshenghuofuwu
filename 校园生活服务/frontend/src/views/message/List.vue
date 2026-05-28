<template>
  <div class="message-list-container">
    <div class="page-header">
      <div class="header-title">
        <h2 class="page-title">我的私信</h2>
        <p class="page-subtitle">与同学和发布者沟通交流</p>
      </div>
    </div>

    <el-skeleton v-if="loading" :rows="5" animated class="skeleton-loading" />

    <div v-else-if="conversations.length === 0" class="empty-state">
      <div class="empty-icon-wrapper">
        <el-icon class="empty-icon"><ChatDotRound /></el-icon>
      </div>
      <p class="empty-text">暂无私信</p>
      <p class="empty-hint">在帖子详情页点击「私信发布者」即可发起对话</p>
    </div>

    <div v-else class="conversation-list">
      <div
        v-for="conv in conversations"
        :key="conv.id"
        class="conversation-item"
        :class="{ unread: conv.isRead === 0 && String(conv.userId) === currentUserId }"
        @click="openConversation(conv)"
      >
        <div class="conv-avatar">
          <el-avatar :size="44">
            {{ String(conv.senderId) === currentUserId ? '我' : (conv.senderName?.charAt(0) || '?') }}
          </el-avatar>
        </div>
        <div class="conv-content">
          <div class="conv-header">
            <span class="conv-name">
              {{ String(conv.senderId) === currentUserId ? '我' : conv.senderName }}
            </span>
            <span class="conv-time">{{ formatTime(conv.createTime) }}</span>
          </div>
          <div class="conv-preview">
            <span class="conv-text">{{ conv.content }}</span>
          </div>
        </div>
        <div class="conv-indicator" v-if="conv.isRead === 0 && String(conv.userId) === currentUserId"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getConversations } from '@/api/message'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(true)
const conversations = ref([])
const currentUserId = localStorage.getItem('userId')

const formatTime = (time) => {
  return dayjs(time).format('MM-DD HH:mm')
}

const loadConversations = async () => {
  try {
    loading.value = true
    const result = await getConversations(currentUserId, { pageNum: 1, pageSize: 50 })
    const raw = result.records || []
    const seen = new Map()
    raw.forEach(msg => {
      const otherId = String(msg.senderId) === currentUserId ? msg.userId : msg.senderId
      const key = `${otherId}-${msg.relatedId}`
      if (!seen.has(key)) {
        seen.set(key, msg)
      }
    })
    conversations.value = Array.from(seen.values())
  } catch (error) {
    console.error(error)
    conversations.value = []
  } finally {
    loading.value = false
  }
}

const openConversation = (conv) => {
  const otherId = String(conv.senderId) === currentUserId ? conv.userId : conv.senderId
  const otherName = String(conv.senderId) === currentUserId ? '' : conv.senderName
  router.push({
    path: '/message/conversation',
    query: {
      userId: otherId,
      userName: otherName,
      relatedPostId: conv.relatedId,
      relatedPostTitle: conv.title
    }
  })
}

onMounted(() => {
  loadConversations()
})
</script>

<style scoped>
.message-list-container {
  min-height: 100%;
}

.page-header {
  padding: 20px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
}

.header-title {
  color: white;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 4px;
}

.page-subtitle {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
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
  padding: 64px 20px;
  background: #fff;
  border-radius: 16px;
  border: 1px solid #f1f5f9;
}

.empty-icon-wrapper {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.empty-icon {
  font-size: 32px;
  color: #94a3b8;
}

.empty-text {
  font-size: 16px;
  color: #64748b;
  margin: 0 0 8px;
  font-weight: 500;
}

.empty-hint {
  font-size: 13px;
  color: #94a3b8;
  margin: 0;
}

.conversation-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.conversation-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #f1f5f9;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.conversation-item:hover {
  background: #f8fafc;
  transform: translateX(4px);
}

.conversation-item.unread {
  background: #f0f4ff;
  border-color: #dbeafe;
}

.conv-avatar {
  flex-shrink: 0;
}

.conv-avatar .el-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.conv-content {
  flex: 1;
  min-width: 0;
}

.conv-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.conv-name {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.conv-time {
  font-size: 12px;
  color: #94a3b8;
  flex-shrink: 0;
}

.conv-preview {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.conv-text {
  font-size: 13px;
  color: #64748b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.conv-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #3b82f6;
  flex-shrink: 0;
  position: absolute;
  top: 16px;
  right: 16px;
}
</style>
