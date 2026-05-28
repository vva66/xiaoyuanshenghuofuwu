<template>
  <div class="profile-container">
    <h2 class="page-title">个人中心</h2>
    
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <div class="user-info">
            <div class="avatar-wrapper" @click="showAvatarDialog = true">
              <el-avatar :size="100" :src="userInfo.avatar || ''" icon="UserFilled" class="avatar-hover" />
              <div class="avatar-overlay">
                <el-icon><Camera /></el-icon>
                <span>更换头像</span>
              </div>
            </div>
            <h3>{{ userInfo.realName || '未设置' }}</h3>
            <p>学号：{{ userInfo.studentNo || '未设置' }}</p>
            <p>学院：{{ userInfo.college || '未设置' }}</p>
            <p>专业：{{ userInfo.major || '未设置' }}</p>
          </div>

          <!-- 更换头像对话框 -->
          <el-dialog v-model="showAvatarDialog" title="更换头像" width="420px" :close-on-click-modal="false">
            <div class="avatar-dialog-body">
              <div class="current-avatar">
                <h4>当前头像</h4>
                <el-avatar :size="120" :src="userInfo.avatar || ''" icon="UserFilled" />
              </div>
              <el-divider />
              <div class="avatar-input">
                <h4>输入头像图片链接</h4>
                <p class="avatar-tip">请输入图片URL链接（支持JPG、PNG等格式）</p>
                <el-input v-model="newAvatarUrl" placeholder="请输入头像图片URL" clearable>
                  <template #prefix>
                    <el-icon><Link /></el-icon>
                  </template>
                </el-input>
                <div class="avatar-actions">
                  <el-button @click="showAvatarDialog = false">取消</el-button>
                  <el-button type="primary" @click="handleUpdateAvatar" :loading="avatarUpdating">保存头像</el-button>
                </div>
              </div>
            </div>
          </el-dialog>
        </el-card>
      </el-col>
      
      <el-col :span="16">
        <el-card>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本信息" name="basic">
              <el-form :model="userInfo" label-width="100px">
                <el-form-item label="用户名">
                  <el-input v-model="userInfo.username" disabled />
                </el-form-item>
                <el-form-item label="真实姓名">
                  <el-input v-model="userInfo.realName" />
                </el-form-item>
                <el-form-item label="性别">
                  <el-radio-group v-model="userInfo.gender">
                    <el-radio :label="1">男</el-radio>
                    <el-radio :label="0">女</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="手机号">
                  <el-input v-model="userInfo.phone" />
                </el-form-item>
                <el-form-item label="邮箱">
                  <el-input v-model="userInfo.email" />
                </el-form-item>
                <el-form-item label="学院">
                  <el-input v-model="userInfo.college" />
                </el-form-item>
                <el-form-item label="专业">
                  <el-input v-model="userInfo.major" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleUpdate">保存</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            
            <el-tab-pane label="修改密码" name="password">
              <el-form :model="passwordForm" label-width="100px">
                <el-form-item label="旧密码">
                  <el-input v-model="passwordForm.oldPassword" type="password" />
                </el-form-item>
                <el-form-item label="新密码">
                  <el-input v-model="passwordForm.newPassword" type="password" />
                </el-form-item>
                <el-form-item label="确认密码">
                  <el-input v-model="passwordForm.confirmPassword" type="password" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="我的帖子" name="posts">
              <div class="my-posts">
                <el-table :data="myPosts" style="width: 100%" v-loading="postsLoading">
                  <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
                  <el-table-column prop="category" label="分类" width="100" />
                  <el-table-column prop="viewCount" label="浏览" width="70" align="center" />
                  <el-table-column prop="replyCount" label="回复" width="70" align="center" />
                  <el-table-column label="状态" width="80" align="center">
                    <template #default="{ row }">
                      <el-tag v-if="row.status === 0" type="primary" size="small">正常</el-tag>
                      <el-tag v-else-if="row.status === 1" type="success" size="small">已解决</el-tag>
                      <el-tag v-else-if="row.status === 2" type="info" size="small">已关闭</el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="180" align="center">
                    <template #default="{ row }">
                      <el-button type="primary" link size="small" @click="$router.push('/help/detail/' + row.id)">查看</el-button>
                      <el-button v-if="row.status === 0" type="success" link size="small" @click="handleMyResolve(row)">解决</el-button>
                      <el-button v-if="row.status === 0" type="warning" link size="small" @click="handleMyClose(row)">关闭</el-button>
                      <el-button type="danger" link size="small" @click="handleMyDelete(row)">删除</el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <div class="pagination-wrapper" v-if="postsTotal > 0">
                  <el-pagination
                    v-model:current-page="postsPageNum"
                    v-model:page-size="postsPageSize"
                    :total="postsTotal"
                    @current-change="loadMyPosts"
                    layout="total, prev, pager, next"
                    small
                  />
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="社团管理" name="clubs">
              <div class="my-clubs">
                <div v-if="managedClubs.length > 0">
                  <h4 class="section-title">我管理的社团</h4>
                  <div class="club-cards">
                    <el-card v-for="club in managedClubs" :key="club.id" class="club-card" shadow="hover">
                      <div class="club-card-header">
                        <span class="club-name">{{ club.name }}</span>
                        <el-tag size="small" type="warning">社长</el-tag>
                      </div>
                      <p class="club-desc">{{ club.description || '暂无简介' }}</p>
                      <div class="club-stats">
                        <span>成员：{{ club.memberCount || 0 }}人</span>
                        <span>活动：{{ club.activityCount || 0 }}个</span>
                      </div>
                      <div class="club-card-actions">
                        <el-button type="primary" size="small" @click="goToManage(club.id)">
                          <el-icon><Setting /></el-icon>
                          社团管理
                        </el-button>
                        <el-button size="small" @click="goToDetail(club.id)">查看详情</el-button>
                      </div>
                    </el-card>
                  </div>
                </div>
                <div v-if="joinedClubs.length > 0" style="margin-top: 24px">
                  <h4 class="section-title">我加入的社团</h4>
                  <div class="club-cards">
                    <el-card v-for="club in joinedClubs" :key="club.id" class="club-card" shadow="hover">
                      <div class="club-card-header">
                        <span class="club-name">{{ club.name }}</span>
                      </div>
                      <p class="club-desc">{{ club.description || '暂无简介' }}</p>
                      <div class="club-stats">
                        <span>成员：{{ club.memberCount || 0 }}人</span>
                      </div>
                      <div class="club-card-actions">
                        <el-button size="small" @click="goToDetail(club.id)">查看详情</el-button>
                        <el-button type="danger" size="small" link @click="handleLeaveClub(club.id)">退出</el-button>
                      </div>
                    </el-card>
                  </div>
                </div>
                <el-empty v-if="managedClubs.length === 0 && joinedClubs.length === 0" description="暂未加入任何社团" />
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting, Camera, Link } from '@element-plus/icons-vue'
import { getUserInfo, updateUserInfo, changePassword } from '@/api/user'
import { getMyPosts, resolvePost, closePost, deletePost } from '@/api/help'
import { getMyManagedClubs, getMyClubs, leaveClub } from '@/api/club'

const router = useRouter()
const activeTab = ref('basic')

const managedClubs = ref([])
const joinedClubs = ref([])

const userInfo = reactive({
  username: '',
  realName: '',
  gender: 1,
  phone: '',
  email: '',
  college: '',
  major: '',
  studentNo: '',
  avatar: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const showAvatarDialog = ref(false)
const newAvatarUrl = ref('')
const avatarUpdating = ref(false)

const userId = localStorage.getItem('userId')

const myPosts = ref([])
const postsTotal = ref(0)
const postsPageNum = ref(1)
const postsPageSize = ref(10)
const postsLoading = ref(false)

const loadUserInfo = async () => {
  try {
    const data = await getUserInfo(userId)
    Object.assign(userInfo, data)
  } catch (error) {
    console.error(error)
  }
}

const loadMyPosts = async () => {
  try {
    postsLoading.value = true
    const result = await getMyPosts(userId, {
      pageNum: postsPageNum.value,
      pageSize: postsPageSize.value
    })
    myPosts.value = result.records || []
    postsTotal.value = result.total || 0
  } catch (error) {
    console.error(error)
    myPosts.value = []
  } finally {
    postsLoading.value = false
  }
}

const handleMyResolve = async (row) => {
  try {
    await ElMessageBox.confirm('确定标记为已解决？', '提示')
    await resolvePost(row.id, userId)
    ElMessage.success('已标记为已解决')
    loadMyPosts()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const handleMyClose = async (row) => {
  try {
    await ElMessageBox.confirm('确定关闭该帖子？', '提示')
    await closePost(row.id, userId)
    ElMessage.success('帖子已关闭')
    loadMyPosts()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const handleMyDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该帖子？', '提示')
    await deletePost(row.id)
    ElMessage.success('删除成功')
    loadMyPosts()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const handleUpdate = async () => {
  try {
    await updateUserInfo({ id: userId, ...userInfo })
    ElMessage.success('更新成功')
  } catch (error) {
    console.error(error)
  }
}

const handleUpdateAvatar = async () => {
  if (!newAvatarUrl.value) {
    ElMessage.warning('请输入头像图片链接')
    return
  }
  try {
    avatarUpdating.value = true
    await updateUserInfo({ id: userId, avatar: newAvatarUrl.value })
    userInfo.avatar = newAvatarUrl.value
    localStorage.setItem('avatar', newAvatarUrl.value)
    ElMessage.success('头像更新成功')
    showAvatarDialog.value = false
    newAvatarUrl.value = ''
  } catch (error) {
    console.error(error)
    ElMessage.error('头像更新失败')
  } finally {
    avatarUpdating.value = false
  }
}

const handleChangePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  
  if (passwordForm.newPassword.length < 6) {
    ElMessage.warning('密码长度至少6位')
    return
  }
  
  try {
    await changePassword({
      userId,
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    localStorage.removeItem('token')
    setTimeout(() => {
      window.location.href = '/login'
    }, 1000)
  } catch (error) {
    console.error(error)
  }
}

const loadManagedClubs = async () => {
  try {
    const result = await getMyManagedClubs(userId)
    managedClubs.value = result || []
  } catch (error) {
    managedClubs.value = []
  }
}

const loadJoinedClubs = async () => {
  try {
    const result = await getMyClubs(userId)
    joinedClubs.value = result || []
  } catch (error) {
    joinedClubs.value = []
  }
}

const goToManage = (clubId) => {
  router.push('/club/manage/' + clubId)
}

const goToDetail = (clubId) => {
  router.push('/club/detail?id=' + clubId)
}

const handleLeaveClub = async (clubId) => {
  try {
    await ElMessageBox.confirm('确定退出该社团？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await leaveClub({ clubId, userId })
    ElMessage.success('已退出社团')
    loadJoinedClubs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('退出失败')
    }
  }
}

watch(activeTab, (val) => {
  if (val === 'posts') {
    loadMyPosts()
  }
  if (val === 'clubs') {
    loadManagedClubs()
    loadJoinedClubs()
  }
})

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  color: #303133;
}

.user-info {
  text-align: center;
}

.user-info h3 {
  margin: 15px 0 10px;
  color: #303133;
}

.user-info p {
  margin: 8px 0;
  color: #909399;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.avatar-hover {
  transition: filter 0.3s;
}

.avatar-wrapper:hover .avatar-hover {
  filter: brightness(0.7);
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
  pointer-events: none;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay .el-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.avatar-overlay span {
  font-size: 12px;
}

.avatar-dialog-body {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.current-avatar {
  text-align: center;
}

.current-avatar h4 {
  margin-bottom: 12px;
  color: #303133;
}

.avatar-input {
  width: 100%;
}

.avatar-input h4 {
  margin-bottom: 8px;
  color: #303133;
}

.avatar-tip {
  font-size: 12px;
  color: #909399;
  margin-bottom: 12px;
}

.avatar-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 16px;
}

.my-posts {
  min-height: 200px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding-top: 16px;
}

.my-clubs {
  min-height: 200px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #f1f5f9;
}

.club-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.club-card {
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.club-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.club-name {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.club-desc {
  font-size: 13px;
  color: #64748b;
  margin: 0 0 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.club-stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #94a3b8;
  margin-bottom: 12px;
}

.club-card-actions {
  display: flex;
  gap: 8px;
}
</style>
