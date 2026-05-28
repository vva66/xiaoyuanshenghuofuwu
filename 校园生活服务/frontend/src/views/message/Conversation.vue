<template>
  <div class="conversation-container">
    <div class="page-header">
      <div class="header-left">
        <el-button class="back-btn" @click="$router.push('/message/list')">
          <el-icon><ArrowLeft /></el-icon>
          返回私信列表
        </el-button>
        <div class="header-info">
          <h2 class="page-title">与 {{ otherUserName }} 的对话</h2>
          <p class="page-subtitle" v-if="relatedPostTitle">关于「{{ relatedPostTitle }}」</p>
        </div>
      </div>
    </div>

    <el-card class="conversation-card">
      <div class="messages-list" ref="messagesRef">
        <div v-if="messages.length === 0" class="empty-messages">
          <p>暂无消息，发送第一条消息吧</p>
        </div>
        <div
          v-for="msg in messages"
          :key="msg.id"
          class="message-item"
          :class="{ 'message-self': String(msg.senderId) === currentUserId, 'message-other': String(msg.senderId) !== currentUserId }"
        >
          <div class="message-avatar" v-if="String(msg.senderId) !== currentUserId">
            <el-avatar :size="36">{{ msg.senderName?.charAt(0) || '?' }}</el-avatar>
          </div>
          <div class="message-bubble">
            <div class="message-sender" v-if="String(msg.senderId) !== currentUserId">{{ msg.senderName }}</div>
            <div class="message-text">{{ msg.content }}</div>
            <div class="message-time">{{ formatTime(msg.createTime) }}</div>
          </div>
          <div class="message-avatar" v-if="String(msg.senderId) === currentUserId">
            <el-avatar :size="36">{{ msg.senderName?.charAt(0) || '?' }}</el-avatar>
          </div>
        </div>
      </div>

      <div class="message-input-area">
        <el-input
          v-model="newMessage"
          type="textarea"
          :rows="2"
          placeholder="输入消息..."
          maxlength="500"
          show-word-limit
          @keyup.enter.ctrl="handleSend"
        />
        <div class="input-actions">
          <span class="input-hint">按 Ctrl+Enter 发送</span>
          <el-button type="primary" @click="handleSend" :disabled="!newMessage.trim()">
            发送
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { getConversation, sendMessage, markAsRead } from '@/api/message'
import dayjs from 'dayjs'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const messages = ref([])
const newMessage = ref('')
const currentUserId = localStorage.getItem('userId')
const messagesRef = ref(null)

const otherUserId = ref('')
const otherUserName = ref('')
const relatedPostId = ref('')
const relatedPostTitle = ref('')

const formatTime = (time) => {
  return dayjs(time).format('MM-DD HH:mm')
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

const loadMessages = async () => {
  try {
    const result = await getConversation(currentUserId, otherUserId.value, relatedPostId.value)
    messages.value = result || []
    await scrollToBottom()
    await markAsRead(currentUserId, otherUserId.value, relatedPostId.value)
  } catch (error) {
    console.error(error)
    messages.value = []
  }
}

const handleSend = async () => {
  if (!newMessage.value.trim()) return
  try {
    await sendMessage({
      senderId: currentUserId,
      senderName: localStorage.getItem('username'),
      userId: otherUserId.value,
      title: '私信',
      content: newMessage.value,
      relatedId: relatedPostId.value
    })
    newMessage.value = ''
    await loadMessages()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  otherUserId.value = route.query.userId || ''
  otherUserName.value = route.query.userName || ''
  relatedPostId.value = route.query.relatedPostId || ''
  relatedPostTitle.value = route.query.relatedPostTitle || ''
  if (otherUserId.value && relatedPostId.value) {
    loadMessages()
  }
})
</script>

<style scoped>
.conversation-container {
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

.page-header {
  padding: 16px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-btn :deep(.el-button) {
  padding: 8px 16px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
}

.header-info {
  color: white;
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 2px;
}

.page-subtitle {
  font-size: 13px;
  opacity: 0.85;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 400px;
}

.conversation-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-radius: 16px;
  border: 1px solid #f1f5f9;
}

.conversation-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 280px);
  padding: 0;
}

.messages-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.empty-messages {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  font-size: 15px;
}

.message-item {
  display: flex;
  gap: 10px;
  max-width: 80%;
}

.message-self {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-other {
  align-self: flex-start;
}

.message-avatar {
  flex-shrink: 0;
  align-self: flex-end;
}

.message-avatar .el-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.message-self .message-avatar .el-avatar {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.message-bubble {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.message-self .message-bubble {
  align-items: flex-end;
}

.message-sender {
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 2px;
}

.message-text {
  font-size: 14px;
  line-height: 1.5;
  padding: 10px 14px;
  border-radius: 12px;
  word-break: break-word;
}

.message-other .message-text {
  background: #f1f5f9;
  color: #1e293b;
  border-bottom-left-radius: 4px;
}

.message-self .message-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message-time {
  font-size: 11px;
  color: #94a3b8;
}

.message-input-area {
  padding: 16px 20px;
  border-top: 1px solid #f1f5f9;
  background: #fafafa;
  border-radius: 0 0 16px 16px;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.input-hint {
  font-size: 12px;
  color: #94a3b8;
}
</style>
