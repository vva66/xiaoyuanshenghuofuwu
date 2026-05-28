<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="admin-aside">
      <div class="logo">
        <div class="logo-icon">
          <el-icon size="24"><Settings /></el-icon>
        </div>
        <div class="logo-text">
          <h2>管理后台</h2>
          <p class="logo-subtitle">Admin Panel</p>
        </div>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="admin-menu"
        background-color="#0f172a"
        text-color="#cbd5e1"
        active-text-color="#a5b4fc"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon size="18"><DataAnalysis /></el-icon>
          <span>数据统计</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/users">
          <el-icon size="18"><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/activities">
          <el-icon size="18"><Star /></el-icon>
          <span>活动管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/posts">
          <el-icon size="18"><ChatDotRound /></el-icon>
          <span>帖子审核</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/clubs">
          <el-icon size="18"><Flag /></el-icon>
          <span>社团管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/canteens">
          <el-icon size="18"><Food /></el-icon>
          <span>食堂管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/settings">
          <el-icon size="18"><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
          <el-button @click="$router.back()" class="back-btn" plain size="small">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <span class="page-title">{{ currentPageTitle }}</span>
        </div>
        <div class="header-right">
          <el-popover
            placement="bottom-end"
            :width="360"
            trigger="click"
            popper-class="notification-popover"
          >
            <template #reference>
              <el-badge :value="notificationCount" class="notification-badge">
                <el-icon class="notification-icon"><Bell /></el-icon>
              </el-badge>
            </template>
            <div class="notification-panel">
              <div class="notification-header">
                <h4 class="notification-title">消息通知</h4>
                <el-button text size="small" @click="markAllRead">全部已读</el-button>
              </div>
              <div class="notification-list">
                <div
                  v-for="item in notifications"
                  :key="item.id"
                  class="notification-item"
                  :class="{ unread: !item.read }"
                  @click="handleNotificationClick(item)"
                >
                  <div class="notification-dot" v-if="!item.read" />
                  <div class="notification-content">
                    <div class="notification-text">{{ item.content }}</div>
                    <div class="notification-time">{{ item.time }}</div>
                  </div>
                  <el-icon class="notification-close" @click.stop="removeNotification(item)"><Close /></el-icon>
                </div>
                <div v-if="notifications.length === 0" class="notification-empty">
                  <el-icon class="empty-icon"><Bell /></el-icon>
                  <p>暂无消息通知</p>
                </div>
              </div>
              <div class="notification-footer">
                <el-button text size="small" @click="viewAllNotifications">查看全部</el-button>
              </div>
            </div>
          </el-popover>
          <el-dropdown @command="handleCommand" class="user-dropdown">
            <span class="user-info">
              <el-avatar :size="36" :src="avatar || ''" icon="UserFilled" class="user-avatar" />
              <div class="user-detail">
                <span class="user-name">{{ username }}</span>
                <span class="user-role">管理员</span>
              </div>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人信息
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><Logout /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="admin-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>import { ref, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Bell, Close } from '@element-plus/icons-vue';
const router = useRouter();
const route = useRoute();
const username = computed(() => {
 return localStorage.getItem('username') || '管理员';
});
const avatar = computed(() => {
 return localStorage.getItem('avatar') || '';
});
const activeMenu = computed(() => route.path);
const pageTitleMap = {
 '/admin/dashboard': '数据统计',
 '/admin/users': '用户管理',
 '/admin/activities': '活动管理',
 '/admin/posts': '帖子审核',
 '/admin/clubs': '社团管理',
 '/admin/canteens': '食堂管理',
 '/admin/settings': '系统设置'
};
const currentPageTitle = computed(() => {
 return pageTitleMap[route.path] || '管理后台';
});

const notificationCount = ref(3)
const notifications = ref([
  { id: 1, content: '有新的社团待审核', time: '刚刚', read: false },
  { id: 2, content: '用户举报内容待处理', time: '30分钟前', read: false },
  { id: 3, content: '系统更新通知', time: '2小时前', read: false }
])

const handleNotificationClick = (item) => {
  item.read = true
  notificationCount.value = notifications.value.filter(n => !n.read).length
}

const markAllRead = () => {
  notifications.value.forEach(n => n.read = true)
  notificationCount.value = 0
  ElMessage.success('已全部标记为已读')
}

const removeNotification = (item) => {
  const index = notifications.value.indexOf(item)
  if (index > -1) {
    notifications.value.splice(index, 1)
    notificationCount.value = notifications.value.filter(n => !n.read).length
  }
}

const viewAllNotifications = () => {
  router.push('/profile')
}

const handleCommand = (command) => {
 if (command === 'logout') {
 localStorage.removeItem('token');
 localStorage.removeItem('userId');
 localStorage.removeItem('username');
 ElMessage.success('退出成功');
 router.push('/login');
 }
 else if (command === 'profile') {
 router.push('/profile');
 }
};
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  display: flex;
}

.admin-aside {
  background: linear-gradient(180deg, #0f172a 0%, #1e293b 100%);
  overflow: hidden;
  box-shadow: 2px 0 15px rgba(0, 0, 0, 0.2);
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px;
  background: rgba(15, 23, 42, 0.9);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.logo-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 15px rgba(99, 102, 241, 0.3);
}

.logo-text {
  display: flex;
  flex-direction: column;
}

.logo-text h2 {
  color: #f8fafc;
  font-size: 17px;
  font-weight: 600;
  margin: 0;
}

.logo-subtitle {
  font-size: 10px;
  color: #64748b;
  margin: 0;
}

.admin-menu {
  border-right: none;
  padding-top: 16px;
}

.admin-menu :deep(.el-menu-item) {
  margin: 0 8px 4px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.admin-menu :deep(.el-menu-item:hover) {
  background: rgba(148, 163, 184, 0.1);
  color: #e2e8f0;
}

.admin-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.2) 0%, rgba(79, 70, 229, 0.15) 100%);
  color: #a5b4fc;
}

.admin-menu :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: linear-gradient(180deg, #a5b4fc 0%, #6366f1 100%);
  border-radius: 0 3px 3px 0;
}

.admin-header {
  background: white;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 65px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-btn :deep(.el-button) {
  color: #64748b;
  border-color: #e2e8f0;
}

.back-btn :deep(.el-button:hover) {
  background: #f8fafc;
  border-color: #cbd5e1;
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.notification-icon {
  font-size: 22px;
  color: #64748b;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.notification-icon:hover {
  background: #f1f5f9;
  color: #334155;
}

.notification-badge :deep(.el-badge__content) {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.4);
}

.notification-panel {
  padding: 0;
}

.notification-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.notification-title {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.notification-list {
  max-height: 320px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s ease;
  position: relative;
}

.notification-item:hover {
  background: #f8fafc;
}

.notification-item.unread {
  background: #f0f7ff;
}

.notification-item.unread:hover {
  background: #e8f2ff;
}

.notification-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #3b82f6;
  flex-shrink: 0;
  margin-top: 6px;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-text {
  font-size: 13px;
  color: #334155;
  line-height: 1.5;
  word-break: break-all;
}

.notification-time {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 4px;
}

.notification-close {
  font-size: 14px;
  color: #cbd5e1;
  flex-shrink: 0;
  padding: 2px;
  border-radius: 4px;
  transition: all 0.2s ease;
  opacity: 0;
}

.notification-item:hover .notification-close {
  opacity: 1;
}

.notification-close:hover {
  background: #f1f5f9;
  color: #64748b;
}

.notification-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #94a3b8;
}

.notification-empty .empty-icon {
  font-size: 40px;
  margin-bottom: 12px;
  color: #cbd5e1;
}

.notification-empty p {
  margin: 0;
  font-size: 14px;
}

.notification-footer {
  display: flex;
  justify-content: center;
  padding: 8px 16px;
  border-top: 1px solid #f0f0f0;
}

.user-dropdown :deep(.el-dropdown-link) {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 10px;
  transition: all 0.2s ease;
}

.user-dropdown :deep(.el-dropdown-link:hover) {
  background: #f8fafc;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
}

.user-role {
  font-size: 12px;
  color: #64748b;
}

.admin-main {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  padding: 24px;
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
