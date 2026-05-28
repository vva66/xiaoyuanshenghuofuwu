<template>
  <div class="publish-container">
    <div class="page-header">
      <div class="header-left">
        <el-button class="back-btn" @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <div class="header-title">
          <h2 class="page-title">发布求助</h2>
          <p class="page-subtitle">填写信息，寻求帮助</p>
        </div>
      </div>
    </div>
    
    <el-card class="form-card">
      <el-form :model="postForm" :rules="rules" ref="formRef" class="publish-form">
        <el-form-item label="标题" prop="title" class="form-item">
          <div class="form-label">
            <span class="label-text">标题</span>
            <span class="label-required">*</span>
          </div>
          <el-input 
            v-model="postForm.title" 
            placeholder="请输入标题（5-200字）" 
            maxlength="200" 
            show-word-limit
            class="form-input"
          />
        </el-form-item>
        
        <el-form-item label="分类" prop="category" class="form-item">
          <div class="form-label">
            <span class="label-text">分类</span>
            <span class="label-required">*</span>
          </div>
          <el-select 
            v-model="postForm.category" 
            placeholder="请选择分类"
            class="form-select"
          >
            <el-option label="失物招领" value="失物招领" />
            <el-option label="学习求助" value="学习求助" />
            <el-option label="二手交易" value="二手交易" />
            <el-option label="活动组织" value="活动组织" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="内容" prop="content" class="form-item">
          <div class="form-label">
            <span class="label-text">内容</span>
            <span class="label-required">*</span>
          </div>
          <el-input
            v-model="postForm.content"
            type="textarea"
            :rows="8"
            placeholder="请详细描述您的需求（10-2000字）"
            maxlength="2000"
            show-word-limit
            class="form-textarea"
          />
        </el-form-item>
        
        <el-form-item label="联系方式" class="form-item">
          <div class="form-label">
            <span class="label-text">联系方式</span>
          </div>
          <el-input 
            v-model="postForm.contactInfo" 
            placeholder="手机号或微信号（选填）"
            class="form-input"
          />
          <p class="form-hint">填写联系方式便于他人联系您</p>
        </el-form-item>
        
        <el-form-item label="图片" class="form-item">
          <div class="form-label">
            <span class="label-text">图片</span>
          </div>
          <el-upload
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :limit="5"
            class="uploader"
          >
            <div class="upload-add">
              <el-icon class="upload-icon"><Plus /></el-icon>
              <span class="upload-text">添加图片</span>
            </div>
          </el-upload>
          <p class="form-hint">支持上传5张图片，帮助更好地描述问题</p>
        </el-form-item>
        
        <el-form-item class="form-actions">
          <el-button class="cancel-btn" @click="$router.back()">取消</el-button>
          <el-button type="primary" class="submit-btn" @click="handleSubmit">
            <el-icon><Check /></el-icon>
            发布求助
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { publishPost } from '@/api/help'
import { ArrowLeft, Plus, Check } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)

const postForm = reactive({
  title: '',
  category: '',
  content: '',
  contactInfo: '',
  publisherId: localStorage.getItem('userId'),
  publisherName: localStorage.getItem('username')
})

const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 5, max: 200, message: '标题长度5-200位', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' },
    { min: 10, max: 2000, message: '内容长度10-2000位', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    await publishPost(postForm)
    ElMessage.success('发布成功')
    router.push('/help/list')
  } catch (error) {
    console.error(error)
  }
}
</script>

<style scoped>
.publish-container {
  min-height: 100%;
}

.page-header {
  padding: 20px 32px;
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 10px 40px rgba(6, 182, 212, 0.3);
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

.form-card {
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: none;
}

.publish-form {
  padding: 32px;
}

.form-item {
  margin-bottom: 28px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 12px;
}

.label-text {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.label-required {
  color: #ef4444;
  font-size: 14px;
}

.form-input :deep(.el-input) {
  width: 100%;
}

.form-input :deep(.el-input__wrapper) {
  border-radius: 10px;
}

.form-select :deep(.el-select) {
  width: 100%;
}

.form-select :deep(.el-select__wrapper) {
  border-radius: 10px;
}

.form-textarea :deep(.el-textarea__inner) {
  border-radius: 10px;
  resize: none;
}

.form-hint {
  font-size: 13px;
  color: #94a3b8;
  margin: 8px 0 0;
}

.uploader :deep(.el-upload) {
  border-radius: 10px;
}

.upload-add {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 24px;
  border: 2px dashed #cbd5e1;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.upload-add:hover {
  border-color: #06b6d4;
  background: #f0fdfa;
}

.upload-icon {
  font-size: 24px;
  color: #94a3b8;
}

.upload-text {
  font-size: 14px;
  color: #64748b;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f1f5f9;
}

.cancel-btn :deep(.el-button) {
  padding: 10px 24px;
  border-radius: 10px;
}

.submit-btn :deep(.el-button) {
  padding: 10px 32px;
  border-radius: 10px;
  font-weight: 500;
}
</style>
