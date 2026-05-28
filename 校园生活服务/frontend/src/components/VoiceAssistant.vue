<template>
  <div class="voice-assistant">
    <transition name="fade">
      <div v-if="showPanel" class="assistant-panel">
        <div class="panel-header">
          <div class="header-left">
            <div class="assistant-avatar">
              <el-icon :size="20"><MagicStick /></el-icon>
            </div>
            <span class="assistant-title">校园智能助手</span>
          </div>
          <el-button :icon="Close" circle size="small" @click="closePanel" />
        </div>

        <div class="panel-body">
          <div v-if="!isListening && !isProcessing && !showReply" class="welcome">
            <div class="welcome-icon">
              <el-icon :size="48"><ChatLineSquare /></el-icon>
            </div>
            <p class="welcome-text">您好！我是校园智能助手</p>
            <p class="welcome-hint">您可以通过以下方式与我交互：</p>
            <div class="hint-list">
              <span class="hint-item" @click="sendText('我要卖一台笔记本电脑')">📦 我要卖东西</span>
              <span class="hint-item" @click="sendText('今天有什么课')">📚 查课表</span>
              <span class="hint-item" @click="sendText('有什么好吃的推荐')">🍜 食堂推荐</span>
              <span class="hint-item" @click="sendText('最近有什么活动')">🎉 校园活动</span>
              <span class="hint-item" @click="sendText('我丢了钱包')">🔍 失物招领</span>
              <span class="hint-item" @click="sendText('看看有什么社团')">🏫 社团活动</span>
            </div>
          </div>

          <div v-if="isListening" class="listening-status">
            <div class="wave-animation">
              <span class="wave-bar" v-for="i in 5" :key="i"></span>
            </div>
            <p class="listening-text">正在聆听...</p>
            <p class="listening-hint">请说出您的需求</p>
          </div>

          <div v-if="isProcessing" class="processing-status">
            <el-icon :size="36" class="processing-icon"><Loading /></el-icon>
            <p class="processing-text">正在思考...</p>
          </div>

          <div v-if="showReply && reply" class="reply-section">
            <div class="reply-avatar">
              <el-icon :size="18"><MagicStick /></el-icon>
            </div>
            <div class="reply-content">
              <p>{{ reply }}</p>
            </div>
          </div>

          <div v-if="error" class="error-section">
            <el-icon :size="18" color="#F56C6C"><WarningFilled /></el-icon>
            <span>{{ error }}</span>
          </div>
        </div>

        <div class="panel-footer">
          <div class="input-area">
            <el-input
              v-model="inputText"
              placeholder="输入您的需求，按回车发送..."
              :disabled="isProcessing"
              clearable
              @keyup.enter="sendText(inputText)"
            >
              <template #prefix>
                <el-icon><EditPen /></el-icon>
              </template>
            </el-input>
            <el-button
              type="primary"
              :icon="Promotion"
              :disabled="!inputText.trim() || isProcessing"
              @click="sendText(inputText)"
            />
          </div>
          <div class="footer-actions">
            <el-button
              :type="isListening ? 'danger' : 'default'"
              :icon="isListening ? Close : Microphone"
              circle
              size="small"
              :class="{ 'btn-listening': isListening }"
              :disabled="isProcessing"
              @click="isListening ? stopListening() : startListening()"
            />
            <span class="footer-hint">{{ isListening ? '点击停止' : '语音输入' }}</span>
            <el-button
              :icon="muted ? BellFilled : Bell"
              circle
              size="small"
              :type="muted ? 'danger' : 'default'"
              @click="toggleMute"
            />
            <span class="footer-hint">{{ muted ? '已静音' : '播放中' }}</span>
          </div>
        </div>
      </div>
    </transition>

    <el-button
      class="assistant-trigger"
      :class="{ 'has-new': showReply }"
      :icon="MagicStick"
      circle
      size="large"
      @click="togglePanel"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { MagicStick, Microphone, Close, Loading, WarningFilled, ChatLineSquare, EditPen, Promotion, BellFilled, Bell } from '@element-plus/icons-vue'
import { useSpeechRecognition } from '../composables/useSpeechRecognition'
import request from '../utils/request'

const router = useRouter()
const showPanel = ref(false)
const inputText = ref('')

const {
  isListening,
  isProcessing,
  reply,
  showReply,
  error,
  muted,
  startListening,
  stopListening,
  speak,
  toggleMute
} = useSpeechRecognition()

onMounted(() => {
  window.__router = router
})

function togglePanel() {
  showPanel.value = !showPanel.value
  if (!showPanel.value) {
    stopListening()
    inputText.value = ''
  }
}

function closePanel() {
  showPanel.value = false
  stopListening()
  inputText.value = ''
}

async function sendText(text) {
  if (!text || !text.trim() || isProcessing.value) return
  const trimmed = text.trim()
  inputText.value = ''

  isProcessing.value = true
  showReply.value = false
  error.value = ''

  try {
    const data = await request.post('/ai/voice-command', { text: trimmed })
    reply.value = data.reply || ''
    showReply.value = true
    speak(reply.value)
    handleIntent(data.intent, data.params || {})
  } catch (e) {
    console.error('AI请求错误:', e)
    if (e.response) {
      error.value = `服务器错误: ${e.response.status} - ${e.response.data?.message || '未知错误'}`
    } else if (e.request) {
      error.value = '无法连接到服务器，请确认后端已启动'
    } else {
      error.value = `请求出错: ${e.message}`
    }
  } finally {
    isProcessing.value = false
  }
}

function handleIntent(intent, params) {
  switch (intent) {
    case 'publish_secondhand':
    case 'publish_lost_found':
    case 'publish_help':
      router.push({
        path: '/help/publish',
        query: {
          category: params.category || '二手交易',
          title: params.title || '',
          content: params.content || ''
        }
      })
      break
    case 'view_schedule':
      router.push('/course/schedule')
      break
    case 'view_activities':
      router.push('/activity/list')
      break
    case 'view_canteen':
      router.push('/canteen/list')
      break
    case 'view_clubs':
      router.push('/club/list')
      break
    case 'view_help_square':
      router.push('/help/list')
      break
    case 'view_profile':
      router.push('/profile')
      break
    case 'go_home':
      router.push('/home')
      break
    default:
      break
  }
}
</script>

<style scoped>
.voice-assistant {
  position: fixed;
  bottom: 100px;
  right: 30px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.assistant-trigger {
  width: 56px !important;
  height: 56px !important;
  font-size: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  color: #fff !important;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.assistant-trigger:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.assistant-trigger.has-new {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4); }
  50% { box-shadow: 0 4px 25px rgba(102, 126, 234, 0.8); }
  100% { box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4); }
}

.assistant-panel {
  position: absolute;
  bottom: 70px;
  right: 0;
  width: 380px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.assistant-avatar {
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.assistant-title {
  font-size: 16px;
  font-weight: 600;
}

.panel-body {
  min-height: 180px;
  max-height: 300px;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.welcome {
  text-align: center;
  width: 100%;
}

.welcome-icon {
  margin-bottom: 12px;
  color: #667eea;
}

.welcome-text {
  font-size: 15px;
  color: #333;
  margin-bottom: 16px;
  font-weight: 500;
}

.welcome-hint {
  font-size: 13px;
  color: #999;
  margin-bottom: 12px;
}

.hint-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.hint-item {
  padding: 6px 14px;
  background: #f5f7fa;
  border-radius: 20px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.hint-item:hover {
  background: #667eea;
  color: #fff;
}

.listening-status {
  text-align: center;
}

.wave-animation {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  margin-bottom: 16px;
  height: 40px;
}

.wave-bar {
  width: 6px;
  height: 20px;
  background: #667eea;
  border-radius: 3px;
  animation: wave 1s ease-in-out infinite;
}

.wave-bar:nth-child(1) { animation-delay: 0s; }
.wave-bar:nth-child(2) { animation-delay: 0.2s; }
.wave-bar:nth-child(3) { animation-delay: 0.4s; }
.wave-bar:nth-child(4) { animation-delay: 0.6s; }
.wave-bar:nth-child(5) { animation-delay: 0.8s; }

@keyframes wave {
  0%, 100% { height: 20px; }
  50% { height: 40px; }
}

.listening-text {
  font-size: 16px;
  font-weight: 600;
  color: #667eea;
  margin-bottom: 4px;
}

.listening-hint {
  font-size: 13px;
  color: #999;
}

.processing-status {
  text-align: center;
}

.processing-icon {
  color: #667eea;
  animation: spin 1s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.processing-text {
  font-size: 15px;
  color: #666;
}

.reply-section {
  display: flex;
  gap: 12px;
  width: 100%;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.reply-avatar {
  width: 32px;
  height: 32px;
  min-width: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.reply-content {
  background: #f5f7fa;
  border-radius: 12px;
  padding: 12px 16px;
  font-size: 14px;
  color: #333;
  line-height: 1.6;
  flex: 1;
}

.error-section {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: #fef0f0;
  border-radius: 8px;
  font-size: 13px;
  color: #F56C6C;
  width: 100%;
}

.panel-footer {
  padding: 12px 16px 16px;
  border-top: 1px solid #eee;
}

.input-area {
  display: flex;
  gap: 8px;
  align-items: center;
}

.input-area :deep(.el-input__wrapper) {
  border-radius: 20px;
  background: #f5f7fa;
}

.input-area :deep(.el-input__inner) {
  font-size: 13px;
}

.footer-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 10px;
}

.btn-listening {
  animation: pulse-btn 1.5s infinite;
}

@keyframes pulse-btn {
  0% { box-shadow: 0 0 0 0 rgba(245, 108, 108, 0.4); }
  70% { box-shadow: 0 0 0 12px rgba(245, 108, 108, 0); }
  100% { box-shadow: 0 0 0 0 rgba(245, 108, 108, 0); }
}

.footer-hint {
  font-size: 12px;
  color: #999;
}

.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px) scale(0.95);
}
</style>
