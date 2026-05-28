import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      },
      {
        path: 'freshman/guide',
        name: 'FreshmanGuide',
        component: () => import('@/views/freshman/Guide.vue')
      },
      {
        path: 'activity/list',
        name: 'ActivityList',
        component: () => import('@/views/activity/List.vue')
      },
      {
        path: 'activity/detail/:id',
        name: 'ActivityDetail',
        component: () => import('@/views/activity/Detail.vue')
      },
      {
        path: 'course/schedule',
        name: 'CourseSchedule',
        component: () => import('@/views/course/Schedule.vue')
      },
      {
        path: 'canteen/list',
        name: 'CanteenList',
        component: () => import('@/views/canteen/List.vue')
      },
      {
        path: 'club/list',
        name: 'ClubList',
        component: () => import('@/views/club/List.vue')
      },
      {
        path: 'club/detail',
        name: 'ClubDetail',
        component: () => import('@/views/club/Detail.vue')
      },
      {
        path: 'club/manage/:id',
        name: 'ClubManage',
        component: () => import('@/views/club/Manage.vue')
      },
      {
        path: 'help/list',
        name: 'HelpList',
        component: () => import('@/views/help/List.vue')
      },
      {
        path: 'help/publish',
        name: 'HelpPublish',
        component: () => import('@/views/help/Publish.vue')
      },
      {
        path: 'help/detail/:id',
        name: 'HelpDetail',
        component: () => import('@/views/help/Detail.vue')
      },
      {
        path: 'message/list',
        name: 'MessageList',
        component: () => import('@/views/message/List.vue')
      },
      {
        path: 'message/conversation',
        name: 'MessageConversation',
        component: () => import('@/views/message/Conversation.vue')
      },
      {
        path: 'campus/map',
        name: 'CampusMap',
        component: () => import('@/views/campus/Map.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue')
      }
    ]
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/UserManagement.vue')
      },
      {
        path: 'activities',
        name: 'AdminActivities',
        component: () => import('@/views/admin/ActivityManagement.vue')
      },
      {
        path: 'posts',
        name: 'AdminPosts',
        component: () => import('@/views/admin/PostModeration.vue')
      },
      {
        path: 'clubs',
        name: 'AdminClubs',
        component: () => import('@/views/admin/ClubManagement.vue')
      },
      {
        path: 'canteens',
        name: 'AdminCanteens',
        component: () => import('@/views/admin/CanteenManagement.vue')
      },
      {
        path: 'settings',
        name: 'AdminSettings',
        component: () => import('@/views/admin/Settings.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.path === '/login' || to.path === '/register') {
    next()
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
