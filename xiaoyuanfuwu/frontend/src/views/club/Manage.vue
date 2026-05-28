<template>
  <div class="club-manage-container">
    <div class="breadcrumb-wrapper">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-breadcrumb-item>
        <el-breadcrumb-item @click="goBack">社团活动</el-breadcrumb-item>
        <el-breadcrumb-item>{{ club.name }}</el-breadcrumb-item>
        <el-breadcrumb-item>社团管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="manage-header">
      <div class="header-info">
        <h2 class="manage-title">
          <el-icon><Setting /></el-icon>
          社团管理
        </h2>
        <p class="manage-subtitle">{{ club.name }} - 社长管理面板</p>
      </div>
      <el-button @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回详情
      </el-button>
    </div>

    <el-card class="manage-card">
      <el-tabs v-model="activeTab" class="manage-tabs">
        <el-tab-pane label="成员管理" name="members">
          <div class="tab-toolbar">
            <span class="tab-title">社团成员 ({{ members.length }}人)</span>
          </div>
          <el-table :data="members" style="width: 100%" v-loading="membersLoading">
            <el-table-column prop="userName" label="姓名" min-width="120" />
            <el-table-column prop="studentNo" label="学号" min-width="140" />
            <el-table-column prop="college" label="学院" min-width="150" />
            <el-table-column prop="joinTime" label="加入时间" min-width="160">
              <template #default="{ row }">
                {{ formatDate(row.joinTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" align="center">
              <template #default="{ row }">
                <el-button
                  type="danger"
                  size="small"
                  link
                  @click="handleRemoveMember(row)"
                  :disabled="row.userId === club.presidentId"
                >
                  {{ row.userId === club.presidentId ? '社长' : '移除' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="入社审核" name="requests">
          <div class="tab-toolbar">
            <span class="tab-title">入社申请</span>
          </div>
          <el-table :data="joinRequests" style="width: 100%" v-loading="requestsLoading">
            <el-table-column prop="userName" label="申请人" min-width="100" />
            <el-table-column prop="studentNo" label="学号" min-width="140" />
            <el-table-column prop="reason" label="申请理由" min-width="200" show-overflow-tooltip />
            <el-table-column prop="createTime" label="申请时间" min-width="160">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" align="center">
              <template #default="{ row }">
                <el-button type="success" size="small" @click="handleApprove(row)">通过</el-button>
                <el-button type="danger" size="small" @click="handleReject(row)">拒绝</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!requestsLoading && joinRequests.length === 0" description="暂无待审核的入社申请" />
        </el-tab-pane>

        <el-tab-pane label="活动管理" name="activities">
          <div class="tab-toolbar">
            <span class="tab-title">社团活动</span>
            <el-button type="primary" size="small" @click="showCreateActivity = true">
              <el-icon><Plus /></el-icon>
              发布活动
            </el-button>
          </div>
          <el-table :data="activities" style="width: 100%" v-loading="activitiesLoading">
            <el-table-column prop="title" label="活动名称" min-width="160" />
            <el-table-column prop="location" label="地点" min-width="120" />
            <el-table-column label="时间" min-width="160">
              <template #default="{ row }">
                {{ formatDateTime(row.startTime) }}
              </template>
            </el-table-column>
            <el-table-column label="报名情况" width="120" align="center">
              <template #default="{ row }">
                {{ row.currentParticipants || 0 }} / {{ row.maxParticipants || 0 }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" align="center">
              <template #default="{ row }">
                <el-button type="primary" size="small" link @click="showActivityDetail(row)">
                  报名列表
                </el-button>
                <el-button type="success" size="small" link @click="showActivitySignIn(row)">
                  签到管理
                </el-button>
                <el-button type="danger" size="small" link @click="handleDeleteActivity(row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!activitiesLoading && activities.length === 0" description="暂无活动" />
        </el-tab-pane>

        <el-tab-pane label="任务管理" name="tasks">
          <div class="tab-toolbar">
            <span class="tab-title">社团任务</span>
            <el-button type="primary" size="small" @click="showCreateTask = true">
              <el-icon><Plus /></el-icon>
              布置任务
            </el-button>
          </div>
          <div v-if="tasks.length > 0" class="task-list">
            <el-card v-for="task in tasks" :key="task.id" class="task-card" shadow="hover">
              <div class="task-header">
                <h4 class="task-title">{{ task.title }}</h4>
                <el-tag :type="task.status === 0 ? 'warning' : 'success'" size="small">
                  {{ task.status === 0 ? '进行中' : '已完成' }}
                </el-tag>
              </div>
              <p class="task-desc">{{ task.description }}</p>
              <div class="task-meta">
                <span>负责人：{{ task.assigneeName || '全体成员' }}</span>
                <span>截止：{{ formatDate(task.deadline) }}</span>
              </div>
              <div class="task-actions" v-if="task.status === 0">
                <el-button type="danger" size="small" link @click="handleDeleteTask(task)">
                  删除任务
                </el-button>
              </div>
            </el-card>
          </div>
          <el-empty v-if="!tasksLoading && tasks.length === 0" description="暂无任务" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="showCreateActivity" title="发布活动" width="600px">
      <el-form :model="activityForm" label-width="100px">
        <el-form-item label="活动标题">
          <el-input v-model="activityForm.title" placeholder="请输入活动标题" />
        </el-form-item>
        <el-form-item label="活动简介">
          <el-input v-model="activityForm.description" type="textarea" :rows="4" placeholder="请输入活动简介" />
        </el-form-item>
        <el-form-item label="活动地点">
          <el-input v-model="activityForm.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="activityForm.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="activityForm.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="最大人数">
          <el-input v-model.number="activityForm.maxParticipants" type="number" placeholder="请输入最大参与人数" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateActivity = false">取消</el-button>
        <el-button type="primary" @click="handleCreateActivity">发布活动</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showCreateTask" title="布置任务" width="550px">
      <el-form :model="taskForm" label-width="100px">
        <el-form-item label="任务标题">
          <el-input v-model="taskForm.title" placeholder="请输入任务标题" />
        </el-form-item>
        <el-form-item label="任务内容">
          <el-input v-model="taskForm.description" type="textarea" :rows="4" placeholder="请输入任务内容" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-select v-model="taskForm.assigneeId" placeholder="选择负责人（可选）" clearable filterable>
            <el-option label="全体成员" :value="null" />
            <el-option v-for="m in members" :key="m.userId" :label="m.userName" :value="m.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker v-model="taskForm.deadline" type="date" placeholder="选择截止日期" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateTask = false">取消</el-button>
        <el-button type="primary" @click="handleCreateTask">布置任务</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showActivityDetailDialog" title="活动报名列表" width="700px">
      <el-table :data="activityRegistrations" style="width: 100%">
        <el-table-column prop="userName" label="姓名" min-width="100" />
        <el-table-column prop="studentNo" label="学号" min-width="140" />
        <el-table-column label="报名时间" min-width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="签到状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.signedIn ? 'success' : 'info'" size="small">
              {{ row.signedIn ? '已签到' : '未签到' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="activityRegistrations.length === 0" description="暂无报名" />
    </el-dialog>

    <el-dialog v-model="showSignInDialog" title="签到管理" width="700px">
      <div class="signin-tip">
        <el-alert title="输入成员学号进行签到" type="info" :closable="false" show-icon />
      </div>
      <div class="signin-input">
        <el-input v-model="signInStudentNo" placeholder="输入学号签到" style="width: 300px">
          <template #append>
            <el-button type="primary" @click="handleSignIn">签到</el-button>
          </template>
        </el-input>
      </div>
      <el-table :data="signInRecords" style="width: 100%; margin-top: 16px">
        <el-table-column prop="userName" label="姓名" min-width="100" />
        <el-table-column prop="studentNo" label="学号" min-width="140" />
        <el-table-column label="签到时间" min-width="160">
          <template #default="{ row }">
            {{ row.signInTime ? formatDateTime(row.signInTime) : '未签到' }}
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { HomeFilled, Setting, ArrowLeft, Plus } from '@element-plus/icons-vue'
import {
  getClubDetail, getClubMembers, removeMember,
  getJoinRequests, approveJoinRequest,
  getClubActivityList, createClubActivity, deleteClubActivity,
  getActivityRegistrations, getActivitySignIns, signInActivity,
  getClubTasks, createTask, deleteTask
} from '@/api/club'

const route = useRoute()
const router = useRouter()
const clubId = Number(route.params.id)
const userId = localStorage.getItem('userId')

const activeTab = ref('members')
const club = ref({})

const members = ref([])
const membersLoading = ref(false)
const joinRequests = ref([])
const requestsLoading = ref(false)
const activities = ref([])
const activitiesLoading = ref(false)
const tasks = ref([])
const tasksLoading = ref(false)

const showCreateActivity = ref(false)
const showCreateTask = ref(false)
const showActivityDetailDialog = ref(false)
const showSignInDialog = ref(false)

const activityRegistrations = ref([])
const signInRecords = ref([])
const signInStudentNo = ref('')
const currentActivityId = ref(null)

const activityForm = reactive({
  title: '',
  description: '',
  location: '',
  startTime: '',
  endTime: '',
  maxParticipants: 50
})

const taskForm = reactive({
  title: '',
  description: '',
  assigneeId: null,
  deadline: ''
})

const loadClub = async () => {
  try {
    const result = await getClubDetail(clubId)
    club.value = result
  } catch (error) {
    ElMessage.error('加载社团信息失败')
  }
}

const loadMembers = async () => {
  try {
    membersLoading.value = true
    const result = await getClubMembers(clubId)
    members.value = result || []
  } catch (error) {
    members.value = []
  } finally {
    membersLoading.value = false
  }
}

const loadJoinRequests = async () => {
  try {
    requestsLoading.value = true
    const result = await getJoinRequests(clubId)
    joinRequests.value = result || []
  } catch (error) {
    joinRequests.value = []
  } finally {
    requestsLoading.value = false
  }
}

const loadActivities = async () => {
  try {
    activitiesLoading.value = true
    const result = await getClubActivityList({ clubId })
    activities.value = result || []
  } catch (error) {
    activities.value = []
  } finally {
    activitiesLoading.value = false
  }
}

const loadTasks = async () => {
  try {
    tasksLoading.value = true
    const result = await getClubTasks(clubId)
    tasks.value = result || []
  } catch (error) {
    tasks.value = []
  } finally {
    tasksLoading.value = false
  }
}

const handleRemoveMember = async (row) => {
  try {
    await ElMessageBox.confirm(`确定将 ${row.userName} 移出社团？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await removeMember(clubId, row.userId)
    ElMessage.success('已移除该成员')
    loadMembers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('移除失败')
    }
  }
}

const handleApprove = async (row) => {
  try {
    await approveJoinRequest(row.id, 1)
    ElMessage.success('已通过该申请')
    loadJoinRequests()
    loadMembers()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleReject = async (row) => {
  try {
    await approveJoinRequest(row.id, 2)
    ElMessage.success('已拒绝该申请')
    loadJoinRequests()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleCreateActivity = async () => {
  if (!activityForm.title || !activityForm.location || !activityForm.startTime || !activityForm.endTime) {
    ElMessage.warning('请填写完整的活动信息')
    return
  }
  try {
    await createClubActivity({
      clubId,
      clubName: club.value.name,
      title: activityForm.title,
      description: activityForm.description,
      location: activityForm.location,
      startTime: activityForm.startTime,
      endTime: activityForm.endTime,
      maxParticipants: activityForm.maxParticipants || 50,
      userId
    })
    ElMessage.success('活动发布成功')
    showCreateActivity.value = false
    Object.assign(activityForm, { title: '', description: '', location: '', startTime: '', endTime: '', maxParticipants: 50 })
    loadActivities()
  } catch (error) {
    ElMessage.error('活动发布失败')
  }
}

const handleCreateTask = async () => {
  if (!taskForm.title) {
    ElMessage.warning('请填写任务标题')
    return
  }
  try {
    await createTask({
      clubId,
      title: taskForm.title,
      description: taskForm.description,
      assigneeId: taskForm.assigneeId,
      deadline: taskForm.deadline,
      createdBy: userId
    })
    ElMessage.success('任务布置成功')
    showCreateTask.value = false
    Object.assign(taskForm, { title: '', description: '', assigneeId: null, deadline: '' })
    loadTasks()
  } catch (error) {
    ElMessage.error('布置任务失败')
  }
}

const handleDeleteTask = async (task) => {
  try {
    await ElMessageBox.confirm('确定删除该任务？', '提示')
    await deleteTask(task.id)
    ElMessage.success('任务已删除')
    loadTasks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const showActivityDetail = async (activity) => {
  currentActivityId.value = activity.id
  try {
    const result = await getActivityRegistrations(activity.id)
    activityRegistrations.value = result || []
  } catch (error) {
    activityRegistrations.value = []
  }
  showActivityDetailDialog.value = true
}

const showActivitySignIn = async (activity) => {
  currentActivityId.value = activity.id
  signInStudentNo.value = ''
  try {
    const result = await getActivitySignIns(activity.id)
    signInRecords.value = result || []
  } catch (error) {
    signInRecords.value = []
  }
  showSignInDialog.value = true
}

const handleSignIn = async () => {
  if (!signInStudentNo.value) {
    ElMessage.warning('请输入学号')
    return
  }
  try {
    await signInActivity(currentActivityId.value, signInStudentNo.value)
    ElMessage.success('签到成功')
    signInStudentNo.value = ''
    const result = await getActivitySignIns(currentActivityId.value)
    signInRecords.value = result || []
  } catch (error) {
    ElMessage.error('签到失败，请检查学号是否正确')
  }
}

const handleDeleteActivity = async (activity) => {
  try {
    await ElMessageBox.confirm(`确定删除活动"${activity.title}"？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteClubActivity(activity.id)
    ElMessage.success('活动已删除')
    loadActivities()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const goBack = () => {
  router.push('/club/detail?id=' + clubId)
}

const formatDate = (date) => {
  if (!date) return '暂无'
  return new Date(date).toLocaleDateString('zh-CN')
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '暂无'
  return new Date(dateTime).toLocaleString('zh-CN')
}

onMounted(() => {
  loadClub()
  loadMembers()
  loadJoinRequests()
  loadActivities()
  loadTasks()
})
</script>

<style scoped>
.club-manage-container {
  min-height: 100%;
}

.breadcrumb-wrapper {
  margin-bottom: 20px;
}

.manage-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.manage-title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.manage-subtitle {
  font-size: 14px;
  color: #94a3b8;
  margin: 0;
}

.manage-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.manage-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.tab-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.tab-title {
  font-size: 15px;
  font-weight: 600;
  color: #475569;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-card {
  border-radius: 10px;
  border: 1px solid #e2e8f0;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.task-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.task-desc {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 12px;
}

.task-meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #94a3b8;
}

.task-actions {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
}

.signin-tip {
  margin-bottom: 16px;
}

.signin-input {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}
</style>
