<template>
  <div class="schedule-container">
    <h2 class="page-title">我的课表</h2>
    
    <el-radio-group v-model="viewMode" size="large" style="margin-bottom: 20px">
      <el-radio-button label="week">周视图</el-radio-button>
      <el-radio-button label="day">日视图</el-radio-button>
    </el-radio-group>
    
    <el-date-picker
      v-if="viewMode === 'day'"
      v-model="selectedDate"
      type="date"
      placeholder="选择日期"
      @change="loadCourses"
      style="margin-bottom: 20px"
    />
    
    <el-skeleton v-if="loading" :rows="10" animated />
    
    <div v-else class="schedule-content">
      <!-- 周视图 -->
      <div v-if="viewMode === 'week'" class="week-view">
        <div class="schedule-grid">
          <!-- 表头 -->
          <div class="grid-header">
            <div class="time-column">时间</div>
            <div v-for="day in weekDays" :key="day.value" class="day-column">
              {{ day.label }}
            </div>
          </div>
          
          <!-- 课程网格 -->
          <div class="grid-body">
            <div v-for="timeSlot in timeSlots" :key="timeSlot.label" class="time-row">
              <div class="time-label">
                <div class="time-text">{{ timeSlot.label }}</div>
                <div class="time-range">{{ timeSlot.range }}</div>
              </div>
              <div v-for="day in weekDays" :key="day.value" class="day-cell">
                <div v-for="course in getCoursesByTimeAndDay(timeSlot.start, day.value)" 
                     :key="course.id" 
                     class="course-card"
                     :style="{ backgroundColor: course.color || '#409EFF' }">
                  <div class="course-name">{{ course.courseName }}</div>
                  <div class="course-teacher">{{ course.teacher }}</div>
                  <div class="course-info">{{ course.weekInfo }},{{ course.classroom }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 日视图 -->
      <div v-else class="day-view">
        <el-timeline>
          <el-timeline-item
            v-for="course in dayCourses"
            :key="course.id"
            :timestamp="`${course.weekInfo} | ${course.classroom}`"
            placement="top"
          >
            <el-card>
              <h4>{{ course.courseName }}</h4>
              <p>教师：{{ course.teacher }}</p>
              <p>教室：{{ course.classroom }}</p>
              <p>周次：{{ course.weekInfo }}</p>
              <p>节次：第{{ course.startSection }}-{{ course.endSection }}节</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>
    

    
    <!-- 添加课程对话框 -->
    <el-dialog v-model="showAddDialog" title="添加课程" width="600px">
      <el-form :model="courseForm" label-width="100px">
        <el-form-item label="课程名称">
          <el-input v-model="courseForm.courseName" />
        </el-form-item>
        <el-form-item label="教师">
          <el-input v-model="courseForm.teacher" />
        </el-form-item>
        <el-form-item label="教室">
          <el-input v-model="courseForm.classroom" />
        </el-form-item>
        <el-form-item label="星期">
          <el-select v-model="courseForm.dayOfWeek">
            <el-option v-for="day in weekDays" :key="day.value" :label="day.label" :value="day.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始节次">
          <el-input-number v-model="courseForm.startSection" :min="1" :max="12" />
        </el-form-item>
        <el-form-item label="结束节次">
          <el-input-number v-model="courseForm.endSection" :min="1" :max="12" />
        </el-form-item>
        <el-form-item label="周次范围">
          <el-input v-model="courseForm.weekInfo" placeholder="例如：1-16周" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddCourse">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserCourses, addCourse } from '@/api/course'
import dayjs from 'dayjs'

const loading = ref(true)
const viewMode = ref('week')
const selectedDate = ref(new Date())
const courses = ref([])
const showAddDialog = ref(false)

const weekDays = [
  { value: 1, label: '星期一' },
  { value: 2, label: '星期二' },
  { value: 3, label: '星期三' },
  { value: 4, label: '星期四' },
  { value: 5, label: '星期五' },
  { value: 6, label: '星期六' },
  { value: 7, label: '星期日' }
]

// 时间段定义（按照大学常见的时间安排）
const timeSlots = [
  { label: '上午1', start: 1, end: 2, range: '08:00-09:40' },
  { label: '上午2', start: 3, end: 4, range: '10:00-11:40' },
  { label: '下午1', start: 5, end: 6, range: '14:00-15:40' },
  { label: '下午2', start: 7, end: 8, range: '16:00-17:40' },
  { label: '晚上1', start: 9, end: 10, range: '19:00-20:40' },
  { label: '晚上2', start: 11, end: 12, range: '21:00-22:40' }
]

const courseForm = reactive({
  courseName: '',
  teacher: '',
  classroom: '',
  dayOfWeek: 1,
  startSection: 1,
  endSection: 2,
  weekInfo: ''
})

const userId = localStorage.getItem('userId')

// 直接使用您提供的课表数据
const defaultCourses = [
  // 星期一
  {
    id: 1,
    userId: userId,
    courseName: '120732 体育与健康4[46]',
    teacher: '凌舒畅',
    classroom: 'A0-体育场地2',
    dayOfWeek: 1,
    startSection: 6,
    endSection: 6,
    weekInfo: '1-16周',
    color: '#A8E6CF'
  },
  {
    id: 2,
    userId: userId,
    courseName: '140057 形势与政策4[03]',
    teacher: '江大洪',
    classroom: 'A0-图3017',
    dayOfWeek: 1,
    startSection: 7,
    endSection: 8,
    weekInfo: '1-4周',
    color: '#FFB3BA'
  },
  // 星期二
  {
    id: 3,
    userId: userId,
    courseName: '120930 智能交互技术应用[02]',
    teacher: '汪姣',
    classroom: 'A0-9206',
    dayOfWeek: 2,
    startSection: 5,
    endSection: 6,
    weekInfo: '1-16周',
    color: '#FFDFBA'
  },
  // 星期三
  {
    id: 4,
    userId: userId,
    courseName: '120832 自动化运维技术[02]',
    teacher: '祁立冬',
    classroom: 'A0-17101',
    dayOfWeek: 3,
    startSection: 1,
    endSection: 2,
    weekInfo: '1-18周',
    color: '#BAE1FF'
  },
  {
    id: 5,
    userId: userId,
    courseName: '120832 自动化运维技术[02]',
    teacher: '祁立冬',
    classroom: 'A0-17101',
    dayOfWeek: 3,
    startSection: 3,
    endSection: 4,
    weekInfo: '1-18周',
    color: '#BAE1FF'
  },
  {
    id: 6,
    userId: userId,
    courseName: '120833 前端开发框架[02]',
    teacher: '戴春林',
    classroom: 'A0-图2002-2',
    dayOfWeek: 3,
    startSection: 5,
    endSection: 6,
    weekInfo: '1-16周',
    color: '#FFFFBA'
  },
  {
    id: 7,
    userId: userId,
    courseName: '120833 前端开发框架[02]',
    teacher: '戴春林',
    classroom: 'A0-图2002-2',
    dayOfWeek: 3,
    startSection: 7,
    endSection: 8,
    weekInfo: '1-16周',
    color: '#FFFFBA'
  },
  // 星期四
  {
    id: 8,
    userId: userId,
    courseName: '120834 JavaEE企业级项目实践[02]',
    teacher: '夏瑞玲',
    classroom: 'A0-17101',
    dayOfWeek: 4,
    startSection: 3,
    endSection: 4,
    weekInfo: '11-18周',
    color: '#C7CEEA'
  },
  {
    id: 9,
    userId: userId,
    courseName: '120929 微服务应用开发[02]',
    teacher: '夏瑞玲',
    classroom: 'A0-17101',
    dayOfWeek: 4,
    startSection: 3,
    endSection: 4,
    weekInfo: '1-10周',
    color: '#E2C6FF'
  },
  {
    id: 10,
    userId: userId,
    courseName: '120834 JavaEE企业级项目实践[02]',
    teacher: '夏瑞玲',
    classroom: 'A0-17101',
    dayOfWeek: 4,
    startSection: 5,
    endSection: 6,
    weekInfo: '11-18周',
    color: '#C7CEEA'
  },
  {
    id: 11,
    userId: userId,
    courseName: '120929 微服务应用开发[02]',
    teacher: '夏瑞玲',
    classroom: 'A0-17101',
    dayOfWeek: 4,
    startSection: 5,
    endSection: 6,
    weekInfo: '1-10周',
    color: '#E2C6FF'
  },
  {
    id: 12,
    userId: userId,
    courseName: '120834 JavaEE企业级项目实践[02]',
    teacher: '夏瑞玲',
    classroom: 'A0-17101',
    dayOfWeek: 4,
    startSection: 7,
    endSection: 8,
    weekInfo: '11-18周',
    color: '#C7CEEA'
  },
  {
    id: 13,
    userId: userId,
    courseName: '120929 微服务应用开发[02]',
    teacher: '夏瑞玲',
    classroom: 'A0-17101',
    dayOfWeek: 4,
    startSection: 7,
    endSection: 8,
    weekInfo: '1-10周',
    color: '#E2C6FF'
  },
  // 星期五
  {
    id: 14,
    userId: userId,
    courseName: '120928 JavaEE企业级应用开发[02]',
    teacher: '祁立冬',
    classroom: 'A0-图2002-2',
    dayOfWeek: 5,
    startSection: 3,
    endSection: 4,
    weekInfo: '1-18周',
    color: '#FFB3BA'
  },
  {
    id: 15,
    userId: userId,
    courseName: '120928 JavaEE企业级应用开发[02]',
    teacher: '祁立冬',
    classroom: 'A0-图2002-2',
    dayOfWeek: 5,
    startSection: 5,
    endSection: 6,
    weekInfo: '1-18周',
    color: '#FFB3BA'
  },
  {
    id: 16,
    userId: userId,
    courseName: '120928 JavaEE企业级应用开发[02]',
    teacher: '祁立冬',
    classroom: 'A0-图2002-2',
    dayOfWeek: 5,
    startSection: 7,
    endSection: 8,
    weekInfo: '1-18周',
    color: '#FFB3BA'
  }
]

// 根据时间段和星期获取课程
const getCoursesByTimeAndDay = (timeStart, dayOfWeek) => {
  return courses.value.filter(course => {
    // 检查课程是否在这个时间段内（课程的开始节次等于当前时间段的开始节次）
    // 并且课程的结束节次不超过当前时间段的结束节次
    return course.dayOfWeek === dayOfWeek && 
           course.startSection === timeStart
  })
}

const dayCourses = computed(() => {
  const dayOfWeek = dayjs(selectedDate.value).day() || 7
  return courses.value.filter(c => c.dayOfWeek === dayOfWeek)
})

const formatTime = (time) => {
  return time ? time.substring(0, 5) : ''
}

const loadCourses = async () => {
  try {
    loading.value = true
    // 直接使用默认课表数据，不从后端获取
    courses.value = defaultCourses
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAddCourse = async () => {
  try {
    await addCourse({
      ...courseForm,
      userId: userId
    })
    ElMessage.success('添加成功')
    showAddDialog.value = false
    loadCourses()
  } catch (error) {
    console.error(error)
  }
}

const handleImport = () => {
  ElMessage.info('导入功能开发中')
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.schedule-container {
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  color: #303133;
}

.week-view {
  overflow-x: auto;
}

.schedule-grid {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  min-width: 800px; /* 确保在小屏幕下可以横向滚动 */
}

.grid-header {
  display: grid;
  grid-template-columns: 100px repeat(7, 1fr);
  background: #f5f7fa;
  font-weight: bold;
  border-bottom: 2px solid #e0e0e0;
}

.time-column {
  padding: 12px 8px;
  text-align: center;
  border-right: 1px solid #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.day-column {
  padding: 12px 8px;
  text-align: center;
  border-right: 1px solid #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.day-column:last-child {
  border-right: none;
}

.grid-body {
  display: flex;
  flex-direction: column;
}

.time-row {
  display: grid;
  grid-template-columns: 100px repeat(7, 1fr);
  min-height: 120px;
  border-bottom: 1px solid #e0e0e0;
}

.time-row:last-child {
  border-bottom: none;
}

.time-label {
  padding: 12px 8px;
  background: #fafafa;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.time-text {
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.time-range {
  font-size: 12px;
  color: #909399;
}

.day-cell {
  padding: 8px;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
  gap: 8px;
  position: relative;
}

.day-cell:last-child {
  border-right: none;
}

.course-card {
  padding: 10px;
  border-radius: 6px;
  color: #333;
  font-size: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.course-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  z-index: 1;
}

.course-name {
  font-weight: bold;
  margin-bottom: 6px;
  font-size: 13px;
  line-height: 1.4;
  word-break: break-all;
}

.course-teacher {
  margin-bottom: 4px;
  color: #606266;
  font-size: 12px;
}

.course-info {
  font-size: 11px;
  color: #909399;
  line-height: 1.5;
  word-break: break-all;
}

.day-view {
  max-width: 800px;
  margin: 0 auto;
}

.action-bar {
  margin-top: 20px;
  text-align: center;
}

@media (max-width: 768px) {
  .grid-header,
  .time-row {
    grid-template-columns: 80px repeat(7, minmax(120px, 1fr));
  }
  
  .time-column,
  .day-column,
  .time-label {
    padding: 8px 4px;
    font-size: 12px;
  }
  
  .course-card {
    padding: 6px;
    font-size: 11px;
  }
  
  .course-name {
    font-size: 12px;
  }
  
  .course-info {
    font-size: 10px;
  }
}
</style>