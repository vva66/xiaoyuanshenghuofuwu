<template>
  <div class="detail-container">
    <div class="page-header">
      <div class="header-left">
        <el-button class="back-btn" @click="$router.push('/help/list')">
          <el-icon><ArrowLeft /></el-icon>
          返回列表
        </el-button>
      </div>
    </div>

    <el-skeleton v-if="loading" :rows="8" animated />

    <template v-else-if="post">
      <el-card class="post-card">
        <div class="post-status-bar" v-if="post.status === 1">
          <el-icon><SuccessFilled /></el-icon>
          <span>该问题已解决</span>
        </div>
        <div class="post-status-bar closed" v-if="post.status === 2">
          <el-icon><CircleCloseFilled /></el-icon>
          <span>该帖子已关闭</span>
        </div>

        <div class="post-header">
          <div class="post-tags">
            <el-tag v-if="post.isTop === 1" type="danger" size="small">置顶</el-tag>
            <el-tag v-if="post.isEssence === 1" type="success" size="small">精华</el-tag>
            <el-tag class="category-tag" size="small">{{ post.category }}</el-tag>
          </div>
          <h2 class="post-title">{{ post.title }}</h2>
          <div class="post-meta">
            <span class="meta-item">
              <el-icon><User /></el-icon>
              {{ post.publisherName }}
            </span>
            <span class="meta-item">
              <el-icon><Clock /></el-icon>
              {{ formatTime(post.createTime) }}
            </span>
            <span class="meta-item">
              <el-icon><View /></el-icon>
              {{ post.viewCount }} 次浏览
            </span>
            <span class="meta-item">
              <el-icon><ChatLineSquare /></el-icon>
              {{ post.replyCount }} 条回复
            </span>
          </div>
        </div>

        <div v-if="post.images" class="post-images">
          <el-image
            v-for="(img, index) in post.images.split(',')"
            :key="index"
            :src="img"
            :preview-src-list="post.images.split(',')"
            :initial-index="index"
            class="post-image-item"
            fit="cover"
          />
        </div>

        <div class="post-content">{{ post.content }}</div>

        <div class="post-footer">
          <div class="contact-info" v-if="post.contactInfo">
            <el-icon><Phone /></el-icon>
            <span>联系方式：{{ post.contactInfo }}</span>
          </div>
          <div class="contact-actions" v-if="!isOwner && post.status === 0">
            <el-button type="primary" plain size="small" @click="handleSendMessage">
              <el-icon><ChatDotSquare /></el-icon>
              私信发布者
            </el-button>
          </div>
          <div class="post-actions" v-if="isOwner">
            <el-button v-if="post.status === 0" type="success" plain size="small" @click="handleResolve">
              <el-icon><Check /></el-icon>
              标记已解决
            </el-button>
            <el-button v-if="post.status === 0" type="warning" plain size="small" @click="handleClose">
              <el-icon><CircleClose /></el-icon>
              关闭帖子
            </el-button>
            <el-button type="danger" plain size="small" @click="handleDelete">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
        </div>
      </el-card>

      <el-card class="comments-card">
        <template #header>
          <div class="comments-header">
            <span class="comments-title">评论 ({{ post.replyCount }})</span>
          </div>
        </template>

        <div class="comment-input-area" v-if="post.status === 0">
          <el-input
            v-model="commentContent"
            type="textarea"
            :rows="3"
            placeholder="写下你的评论..."
            maxlength="500"
            show-word-limit
          />
          <div class="comment-actions">
            <el-button type="primary" @click="handleAddComment" :disabled="!commentContent.trim()">
              发表评论
            </el-button>
          </div>
        </div>

        <div v-if="comments.length === 0" class="empty-comments">
          <p>暂无评论，快来抢沙发吧~</p>
        </div>

        <div v-else class="comments-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-header">
              <el-avatar :size="32" class="comment-avatar">{{ comment.userName?.charAt(0) }}</el-avatar>
              <div class="comment-user">
                <span class="comment-name">{{ comment.userName }}</span>
                <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
              </div>
              <el-button
                v-if="isOwner || String(comment.userId) === currentUserId"
                text
                type="danger"
                size="small"
                @click="handleDeleteComment(comment.id)"
              >
                删除
              </el-button>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
          </div>
        </div>

        <div class="pagination-wrapper" v-if="commentTotal > commentPageSize">
          <el-pagination
            v-model:current-page="commentPageNum"
            v-model:page-size="commentPageSize"
            :total="commentTotal"
            @current-change="loadComments"
            layout="total, prev, pager, next"
            small
          />
        </div>
      </el-card>
    </template>

    <el-empty v-else description="帖子不存在" />

    <el-dialog v-model="messageDialogVisible" title="发送私信" width="420px" :close-on-click-modal="false">
      <div class="message-dialog-body">
        <div class="message-to">发送给：<strong>{{ post?.publisherName }}</strong></div>
        <div class="message-post" v-if="post">
          <span class="message-post-label">关于帖子：</span>
          <span class="message-post-title">{{ post.title }}</span>
        </div>
        <el-input
          v-model="messageContent"
          type="textarea"
          :rows="4"
          placeholder="请输入你想说的话..."
          maxlength="500"
          show-word-limit
        />
      </div>
      <template #footer>
        <el-button @click="messageDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSendMessage" :disabled="!messageContent.trim()">
          发送
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPostDetail, deletePost, resolvePost, closePost, addComment, getCommentList, deleteComment } from '@/api/help'
import { sendMessage } from '@/api/message'
import dayjs from 'dayjs'
import { ElImage } from 'element-plus'
import {
  ArrowLeft, SuccessFilled, CircleCloseFilled, User, Clock, View,
  ChatLineSquare, Phone, Check, CircleClose, Delete, ChatDotSquare
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const post = ref(null)
const comments = ref([])
const commentTotal = ref(0)
const commentPageNum = ref(1)
const commentPageSize = ref(20)
const commentContent = ref('')
const currentUserId = localStorage.getItem('userId')

const isOwner = computed(() => {
  return post.value && String(post.value.publisherId) === currentUserId
})

const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

const loadPost = async () => {
  try {
    loading.value = true
    const id = route.params.id
    const data = await getPostDetail(id)
    post.value = data
  } catch (error) {
    console.error(error)
    post.value = null
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  try {
    const id = route.params.id
    const result = await getCommentList(id, {
      pageNum: commentPageNum.value,
      pageSize: commentPageSize.value
    })
    comments.value = result.records || []
    commentTotal.value = result.total || 0
  } catch (error) {
    console.error(error)
    comments.value = []
  }
}

const handleAddComment = async () => {
  if (!commentContent.value.trim()) return
  try {
    await addComment({
      postId: post.value.id,
      content: commentContent.value,
      userId: currentUserId,
      userName: localStorage.getItem('username')
    })
    ElMessage.success('评论成功')
    commentContent.value = ''
    post.value.replyCount = (post.value.replyCount || 0) + 1
    loadComments()
  } catch (error) {
    console.error(error)
  }
}

const handleDeleteComment = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该评论？', '提示')
    await deleteComment(id)
    ElMessage.success('删除成功')
    post.value.replyCount = Math.max(0, (post.value.replyCount || 0) - 1)
    loadComments()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const handleResolve = async () => {
  try {
    await ElMessageBox.confirm('确定标记为已解决？', '提示')
    await resolvePost(post.value.id, currentUserId)
    ElMessage.success('已标记为已解决')
    post.value.status = 1
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const handleClose = async () => {
  try {
    await ElMessageBox.confirm('确定关闭该帖子？关闭后无法恢复', '提示')
    await closePost(post.value.id, currentUserId)
    ElMessage.success('帖子已关闭')
    post.value.status = 2
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定删除该帖子？删除后无法恢复', '提示')
    await deletePost(post.value.id)
    ElMessage.success('删除成功')
    router.push('/help/list')
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const messageContent = ref('')
const messageDialogVisible = ref(false)

const handleSendMessage = () => {
  messageContent.value = ''
  messageDialogVisible.value = true
}

const confirmSendMessage = async () => {
  if (!messageContent.value.trim()) return
  try {
    await sendMessage({
      senderId: currentUserId,
      senderName: localStorage.getItem('username'),
      userId: post.value.publisherId,
      title: '私信',
      content: messageContent.value,
      relatedId: post.value.id
    })
    ElMessage.success('私信已发送')
    messageDialogVisible.value = false
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadPost()
  loadComments()
})
</script>

<style scoped>
.detail-container {
  min-height: 100%;
}

.page-header {
  padding: 16px 24px;
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 10px 40px rgba(6, 182, 212, 0.3);
}

.back-btn :deep(.el-button) {
  padding: 8px 16px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
}

.post-card {
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: none;
  margin-bottom: 24px;
}

.post-status-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: #f0fdf4;
  border-radius: 8px;
  margin-bottom: 20px;
  color: #16a34a;
  font-size: 14px;
  font-weight: 500;
}

.post-status-bar.closed {
  background: #fef2f2;
  color: #dc2626;
}

.post-header {
  margin-bottom: 24px;
}

.post-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.category-tag {
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
  border: none;
  color: white;
}

.post-title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 16px;
  line-height: 1.4;
}

.post-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  color: #64748b;
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.post-images {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  padding: 16px 0;
  border-top: 1px solid #f1f5f9;
}

.post-image-item {
  width: 200px;
  height: 150px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #e2e8f0;
  transition: transform 0.3s ease;
}

.post-image-item:hover {
  transform: scale(1.02);
}

.post-content {
  font-size: 16px;
  line-height: 1.8;
  color: #334155;
  padding: 20px 0;
  border-top: 1px solid #f1f5f9;
  white-space: pre-wrap;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f1f5f9;
}

.contact-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #0891b2;
  font-size: 14px;
}

.contact-actions {
  display: flex;
  align-items: center;
}

.post-actions {
  display: flex;
  gap: 8px;
}

.message-dialog-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message-to {
  font-size: 14px;
  color: #475569;
}

.message-post {
  font-size: 13px;
  color: #64748b;
  background: #f8fafc;
  padding: 8px 12px;
  border-radius: 8px;
}

.message-post-label {
  flex-shrink: 0;
}

.message-post-title {
  color: #334155;
  font-weight: 500;
}

.comments-card {
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: none;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comments-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.comment-input-area {
  margin-bottom: 24px;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.empty-comments {
  text-align: center;
  padding: 40px;
  color: #94a3b8;
  font-size: 15px;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.comment-avatar {
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
  color: white;
  flex-shrink: 0;
}

.comment-user {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.comment-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.comment-time {
  font-size: 12px;
  color: #94a3b8;
}

.comment-content {
  font-size: 15px;
  line-height: 1.6;
  color: #475569;
  padding-left: 44px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding-top: 16px;
}
</style>
