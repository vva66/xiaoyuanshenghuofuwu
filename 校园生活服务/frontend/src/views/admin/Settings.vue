<template>
  <div class="settings-container">
    <div class="page-header">
      <div class="header-title">
        <h2 class="page-title">系统设置</h2>
        <p class="page-subtitle">配置系统各项参数</p>
      </div>
    </div>
    
    <div class="settings-grid">
      <el-card class="setting-card">
        <div class="card-header">
          <el-icon class="card-icon"><Setting /></el-icon>
          <h3 class="card-title">基础设置</h3>
        </div>
        <el-form class="setting-form">
          <el-form-item class="form-item">
            <div class="form-label">
              <span class="label-text">系统名称</span>
            </div>
            <el-input v-model="settings.systemName" placeholder="请输入系统名称" class="form-input" />
          </el-form-item>
          <el-form-item class="form-item">
            <div class="form-label">
              <span class="label-text">系统描述</span>
            </div>
            <el-input v-model="settings.description" type="textarea" :rows="3" placeholder="请输入系统描述" class="form-textarea" />
          </el-form-item>
          <el-form-item class="form-item">
            <div class="form-label">
              <span class="label-text">维护模式</span>
              <span class="label-hint">开启后用户将无法访问系统</span>
            </div>
            <el-switch v-model="settings.maintenanceMode" class="form-switch" />
          </el-form-item>
          <el-form-item class="form-actions">
            <el-button type="primary" @click="handleSave" class="save-btn">
              <el-icon><Check /></el-icon>
              保存设置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
      
      <el-card class="setting-card">
        <div class="card-header">
          <el-icon class="card-icon"><Bell /></el-icon>
          <h3 class="card-title">通知设置</h3>
        </div>
        <el-form class="setting-form">
          <el-form-item class="form-item">
            <div class="form-label">
              <span class="label-text">邮件通知</span>
            </div>
            <el-switch v-model="settings.emailNotification" class="form-switch" />
          </el-form-item>
          <el-form-item class="form-item">
            <div class="form-label">
              <span class="label-text">短信通知</span>
            </div>
            <el-switch v-model="settings.smsNotification" class="form-switch" />
          </el-form-item>
          <el-form-item class="form-item">
            <div class="form-label">
              <span class="label-text">活动提醒</span>
            </div>
            <el-switch v-model="settings.activityReminder" class="form-switch" />
          </el-form-item>
          <el-form-item class="form-actions">
            <el-button type="primary" @click="handleSave" class="save-btn">
              <el-icon><Check /></el-icon>
              保存设置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
    
    <el-card class="info-card">
      <div class="card-header">
        <el-icon class="card-icon"><InfoFilled /></el-icon>
        <h3 class="card-title">系统信息</h3>
      </div>
      <div class="info-grid">
        <div class="info-item">
          <span class="info-label">系统版本</span>
          <span class="info-value">v1.0.0</span>
        </div>
        <div class="info-item">
          <span class="info-label">数据库版本</span>
          <span class="info-value">MySQL 8.0</span>
        </div>
        <div class="info-item">
          <span class="info-label">后端框架</span>
          <span class="info-value">Spring Boot 3.2.0</span>
        </div>
        <div class="info-item">
          <span class="info-label">前端框架</span>
          <span class="info-value">Vue 3.4.0</span>
        </div>
        <div class="info-item">
          <span class="info-label">部署时间</span>
          <span class="info-value">2024-01-15</span>
        </div>
        <div class="info-item">
          <span class="info-label">服务器状态</span>
          <el-tag type="success" class="status-tag">运行正常</el-tag>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Setting, Bell, InfoFilled, Check } from '@element-plus/icons-vue'
import request from '@/utils/request'

const settings = reactive({
  systemName: '校园服务系统',
  description: '为校园师生提供便捷的校园服务',
  maintenanceMode: false,
  emailNotification: true,
  smsNotification: false,
  activityReminder: true
})

const handleSave = async () => {
  try {
    await request({
      url: '/admin/settings',
      method: 'post',
      data: {
        systemName: settings.systemName,
        description: settings.description,
        maintenanceMode: settings.maintenanceMode,
        emailNotification: settings.emailNotification,
        smsNotification: settings.smsNotification,
        activityReminder: settings.activityReminder
      }
    })
    ElMessage.success('保存成功')
  } catch (e) {
    ElMessage.error('保存失败')
  }
}
</script>

<style scoped>
.settings-container {
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

.settings-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.setting-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: none;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.card-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.setting-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-item {
  margin: 0;
}

.form-label {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 10px;
}

.label-text {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.label-hint {
  font-size: 12px;
  color: #94a3b8;
}

.form-input :deep(.el-input) {
  width: 100%;
}

.form-input :deep(.el-input__wrapper) {
  border-radius: 8px;
}

.form-textarea :deep(.el-textarea__inner) {
  border-radius: 8px;
}

.form-switch :deep(.el-switch) {
  --el-switch-on-color: #6366f1;
  --el-switch-off-color: #cbd5e1;
}

.form-actions {
  margin-top: 10px;
}

.save-btn :deep(.el-button) {
  padding: 10px 24px;
  border-radius: 8px;
}

.info-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: none;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 10px;
}

.info-label {
  font-size: 13px;
  color: #64748b;
}

.info-value {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.status-tag {
  padding: 4px 12px;
  border-radius: 20px;
}
</style>
