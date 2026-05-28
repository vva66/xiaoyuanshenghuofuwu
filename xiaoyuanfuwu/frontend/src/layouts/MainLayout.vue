<template>
  <el-container class="main-layout">
    <el-aside width="220px" class="main-aside">
      <div class="logo">
        <div class="logo-icon">
          <el-icon size="28"><School /></el-icon>
        </div>
        <div class="logo-text">
          <h3>校园服务</h3>
          <p class="logo-subtitle">Campus Service</p>
        </div>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="main-menu"
        background-color="#1e293b"
        text-color="#cbd5e1"
        active-text-color="#60a5fa"
      >
        <el-menu-item index="/home">
          <el-icon size="18"><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/freshman/guide">
          <el-icon size="18"><School /></el-icon>
          <span>迎新指南</span>
        </el-menu-item>
        <el-menu-item index="/activity/list">
          <el-icon size="18"><Star /></el-icon>
          <span>校园活动</span>
        </el-menu-item>
        <el-menu-item index="/course/schedule">
          <el-icon size="18"><Calendar /></el-icon>
          <span>课表查询</span>
        </el-menu-item>
        <el-menu-item index="/canteen/list">
          <el-icon size="18"><Food /></el-icon>
          <span>食堂推荐</span>
        </el-menu-item>
        <el-menu-item index="/club/list">
          <el-icon size="18"><Flag /></el-icon>
          <span>社团活动</span>
        </el-menu-item>
        <el-menu-item index="/help/list">
          <el-icon size="18"><ChatDotRound /></el-icon>
          <span>互助广场</span>
        </el-menu-item>
        <el-menu-item index="/message/list">
          <el-icon size="18"><ChatLineSquare /></el-icon>
          <span>我的私信</span>
          <el-tag v-if="unreadCount > 0" size="small" type="danger" class="menu-badge">{{ unreadCount > 99 ? '99+' : unreadCount }}</el-tag>
        </el-menu-item>
        <el-menu-item index="/campus/map">
          <el-icon size="18"><MapLocation /></el-icon>
          <span>校园地图</span>
        </el-menu-item>
        <el-menu-item index="/profile">
          <el-icon size="18"><User /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="main-header">
        <div class="header-left">
          <el-icon class="menu-toggle" @click="toggleSidebar"><Menu /></el-icon>
          <span class="header-title">欢迎使用校园服务系统</span>
        </div>
        <div class="header-right">
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="message-badge">
            <el-icon class="header-icon" @click="$router.push('/message/list')"><ChatLineSquare /></el-icon>
          </el-badge>
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
                <span class="user-role">{{ isAdmin ? '管理员' : '学生' }}</span>
              </div>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-if="isAdmin" command="admin">
                  <el-icon><Setting /></el-icon>
                  管理后台
                </el-dropdown-item>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人信息
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>

  <VoiceAssistant />
</template>

<script setup>import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { School, HomeFilled, Star, Calendar, Food, Flag, ChatDotRound, ChatLineSquare, MapLocation, User, Menu, Bell, Setting, SwitchButton, UserFilled, Close } from '@element-plus/icons-vue';
import { getUnreadCount } from '@/api/message';
import VoiceAssistant from '../components/VoiceAssistant.vue';
const router = useRouter();
const route = useRoute();
const username = computed(() => {
 return localStorage.getItem('username') || '用户';
});
const avatar = computed(() => {
 const stored = localStorage.getItem('avatar') || '';
 // 清理旧的 pexels 链接（已被浏览器安全策略拦截）
 if (stored.includes('pexels.com')) {
   localStorage.removeItem('avatar');
   return '';
 }
 return stored;
});
const isAdmin = ref(false);
const notificationCount = ref(3);
const unreadCount = ref(0);
const sidebarCollapsed = ref(false);
const activeMenu = computed(() => route.path);

const notifications = ref([
  { id: 1, content: '欢迎加入校园服务系统！', time: '刚刚', read: false },
  { id: 2, content: '你有新的社团活动待参加', time: '10分钟前', read: false },
  { id: 3, content: '互助广场有新回复', time: '1小时前', read: false }
])

const toggleSidebar = () => {
 sidebarCollapsed.value = !sidebarCollapsed.value;
};

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
 else if (command === 'admin') {
 router.push('/admin/dashboard');
 }
};
const loadUnreadCount = async () => {
  try {
    const userId = localStorage.getItem('userId')
    if (userId) {
      const result = await getUnreadCount(userId)
      unreadCount.value = result.count || 0
    }
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
 const userRole = localStorage.getItem('userRole');
 isAdmin.value = userRole === 'ADMIN' || localStorage.getItem('username') === 'admin';
 loadUnreadCount()
 setInterval(loadUnreadCount, 30000)
});
</script>

<style scoped>
.main-layout {
  height: 100vh;
  display: flex;
}

.main-aside {
  background: linear-gradient(180deg, #1e293b 0%, #334155 100%);
  overflow: hidden;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px;
  background: rgba(30, 41, 59, 0.8);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.logo-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3);
}

.logo-text {
  display: flex;
  flex-direction: column;
}

.logo-text h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #f8fafc;
}

.logo-subtitle {
  font-size: 11px;
  color: #94a3b8;
  margin: 0;
}

.main-menu {
  border-right: none;
  padding-top: 16px;
}

.main-menu :deep(.el-menu-item) {
  margin: 0 8px 4px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.main-menu :deep(.el-menu-item:hover) {
  background: rgba(96, 165, 250, 0.15);
  color: #60a5fa;
}

.main-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2) 0%, rgba(37, 99, 235, 0.15) 100%);
  color: #60a5fa;
}

.main-menu :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: linear-gradient(180deg, #60a5fa 0%, #3b82f6 100%);
  border-radius: 0 3px 3px 0;
}

.main-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-bottom: 1px solid #e2e8f0;
  padding: 0 24px;
  height: 65px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.menu-toggle {
  font-size: 20px;
  color: #64748b;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.menu-toggle:hover {
  background: #f1f5f9;
  color: #334155;
}

.header-title {
  font-size: 16px;
  font-weight: 500;
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

  .header-icon {
    font-size: 22px;
    cursor: pointer;
    color: #64748b;
    transition: color 0.3s ease;
    padding: 8px;
    border-radius: 8px;
  }

  .header-icon:hover {
    color: #3b82f6;
  }

  .message-badge {
    margin-right: 8px;
  }

  .menu-badge {
    margin-left: auto;
    border: none;
    font-weight: 600;
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
  background: #f1f5f9;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
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
  color: #94a3b8;
}

.main-content {
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
