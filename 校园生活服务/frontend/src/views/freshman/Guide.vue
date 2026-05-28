<template>
  <div class="guide-container">
    <div class="page-header">
      <div class="header-title">
        <h2 class="page-title">迎新指南</h2>
        <p class="page-subtitle">全方位了解校园，助你顺利开启大学生活</p>
      </div>
    </div>

    <el-card class="tabs-card">
      <el-tabs v-model="activeTab" class="guide-tabs" @tab-change="onTabChange">
        <el-tab-pane label="报到流程" name="process">
          <div class="tab-content">
            <div class="steps-wrapper">
              <el-steps :active="activeStep" class="steps" direction="vertical">
                <el-step
                  v-for="(step, index) in processSteps"
                  :key="index"
                  :title="step.title"
                  :description="step.description"
                >
                  <template #icon>
                    <div class="step-icon-wrapper" :style="{ background: step.color }">
                      <el-icon :size="20"><component :is="step.icon" /></el-icon>
                    </div>
                  </template>
                </el-step>
              </el-steps>
            </div>

            <el-card class="tips-card">
              <div class="tip-header">
                <el-icon class="tip-icon"><Lightning /></el-icon>
                <span class="tip-title">报到须知</span>
              </div>
              <ul class="tip-list">
                <li>报到时间：2024年9月1日 - 9月3日（每天8:00-18:00）</li>
                <li>报到地点：学校体育馆（南门入口）</li>
                <li>携带材料：录取通知书、身份证原件及复印件、一寸免冠照片8张</li>
                <li>党团关系：携带党/团组织关系介绍信及档案材料</li>
                <li>户口迁移：自愿办理，需携带户口迁移证（迁往地址：XX大学）</li>
                <li>咨询电话：010-12345678（迎新办公室）</li>
              </ul>
            </el-card>

            <el-card class="transport-card">
              <div class="tip-header">
                <el-icon class="transport-icon"><MapLocation /></el-icon>
                <span class="tip-title">交通指南</span>
              </div>
              <div class="transport-list">
                <div class="transport-item" v-for="(item, index) in transportInfo" :key="index">
                  <div class="transport-icon-wrapper" :style="{ background: item.color }">
                    <el-icon><component :is="item.icon" /></el-icon>
                  </div>
                  <div class="transport-detail">
                    <span class="transport-label">{{ item.label }}</span>
                    <span class="transport-desc">{{ item.desc }}</span>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>

        <el-tab-pane label="宿舍指南" name="dormitory">
          <div class="tab-content">
            <el-card class="search-card">
              <el-form :inline="true" :model="queryForm" class="search-form">
                <el-form-item label="学号">
                  <el-input
                    v-model="queryForm.studentNo"
                    placeholder="请输入学号查询宿舍分配"
                    class="search-input"
                    clearable
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="queryDormitory" class="search-btn" :loading="loading">
                    <el-icon><Search /></el-icon>
                    查询
                  </el-button>
                </el-form-item>
              </el-form>
            </el-card>

            <el-card v-if="dormitoryInfo" class="result-card">
              <div class="result-header">
                <el-icon class="result-icon"><HomeFilled /></el-icon>
                <h3 class="result-title">宿舍分配信息</h3>
              </div>
              <el-descriptions :column="2" class="dorm-info" border>
                <el-descriptions-item label="学号" :span="1">
                  <span class="desc-value">{{ dormitoryInfo.studentNo }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="姓名" :span="1">
                  <span class="desc-value">{{ dormitoryInfo.name }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="宿舍楼" :span="1">
                  <span class="desc-value">{{ dormitoryInfo.building }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="房间号" :span="1">
                  <span class="desc-value">{{ dormitoryInfo.room }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="床位" :span="1">
                  <span class="desc-value">{{ dormitoryInfo.bed }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="宿舍管理员" :span="1">
                  <span class="desc-value">{{ dormitoryInfo.manager }}</span>
                </el-descriptions-item>
              </el-descriptions>
            </el-card>

            <div v-else class="empty-state">
              <div class="empty-icon-wrapper">
                <el-icon class="empty-icon"><HomeFilled /></el-icon>
              </div>
              <p class="empty-text">请输入学号查询宿舍分配信息</p>
            </div>

            <el-card class="dorm-rules-card">
              <div class="tip-header">
                <el-icon class="rules-icon"><InfoFilled /></el-icon>
                <span class="tip-title">宿舍管理规定</span>
              </div>
              <ul class="rules-list">
                <li>宿舍供电时间：6:00-23:30（周五周六延长至24:00）</li>
                <li>热水供应：6:30-8:30，11:30-13:30，17:30-23:00</li>
                <li>门禁时间：23:00（晚归需登记）</li>
                <li>禁止使用大功率电器（超过800W）</li>
                <li>宿舍报修：联系宿管或通过"校园服务-后勤报修"在线提交</li>
                <li>宿舍调整：每学期第1-2周可申请调宿</li>
              </ul>
            </el-card>
          </div>
        </el-tab-pane>

        <el-tab-pane label="缴费指南" name="payment">
          <div class="tab-content">
            <el-card class="fee-card">
              <h3 class="card-title">
                <el-icon class="card-title-icon"><Coin /></el-icon>
                收费标准
              </h3>
              <el-table :data="feeItems" style="width: 100%" stripe>
                <el-table-column prop="name" label="项目" width="120" />
                <el-table-column prop="desc" label="说明" />
                <el-table-column prop="amount" label="金额" width="140" />
                <el-table-column prop="note" label="备注" />
              </el-table>
              <div class="fee-total">
                <span class="total-label">合计</span>
                <span class="total-amount">{{ totalFee }}</span>
              </div>
            </el-card>

            <el-card class="payment-card">
              <h3 class="card-title">
                <el-icon class="card-title-icon"><CreditCard /></el-icon>
                缴费方式
              </h3>
              <div class="payment-methods">
                <div class="method-item" v-for="(method, index) in paymentMethods" :key="index">
                  <div class="method-icon-wrapper" :style="{ background: method.color }">
                    <el-icon class="method-icon"><component :is="method.icon" /></el-icon>
                  </div>
                  <span class="method-name">{{ method.name }}</span>
                  <span class="method-desc">{{ method.desc }}</span>
                </div>
              </div>
            </el-card>

            <el-card class="deadline-card">
              <div class="deadline-content">
                <el-icon class="deadline-icon"><Clock /></el-icon>
                <div class="deadline-info">
                  <span class="deadline-title">缴费截止日期</span>
                  <span class="deadline-date">2024年9月15日</span>
                  <span class="deadline-tip">逾期未缴费将影响学籍注册，请按时完成</span>
                </div>
              </div>
            </el-card>

            <el-card class="scholarship-card">
              <div class="tip-header">
                <el-icon class="scholarship-icon"><GoldMedal /></el-icon>
                <span class="tip-title">奖助学金与绿色通道</span>
              </div>
              <ul class="scholarship-list">
                <li><strong>国家奖学金</strong>：8000元/年，奖励品学兼优学生</li>
                <li><strong>国家励志奖学金</strong>：5000元/年，奖励家庭经济困难且成绩优秀学生</li>
                <li><strong>国家助学金</strong>：一等4000元/年，二等3000元/年，三等2000元/年</li>
                <li><strong>校级奖学金</strong>：特等5000元，一等3000元，二等2000元，三等1000元</li>
                <li><strong>绿色通道</strong>：家庭经济困难学生可先办理入学手续，后通过助学贷款等方式缴纳学费</li>
                <li><strong>助学贷款</strong>：最高可贷16000元/年，在校期间国家贴息</li>
              </ul>
            </el-card>
          </div>
        </el-tab-pane>

        <el-tab-pane label="校园生活" name="campus">
          <div class="tab-content">
            <div class="campus-grid">
              <el-card
                v-for="item in campusIntro"
                :key="item.id"
                class="campus-card"
                shadow="hover"
              >
                <div class="campus-icon-wrapper" :style="{ background: item.gradient }">
                  <el-icon class="campus-icon"><component :is="item.icon" /></el-icon>
                </div>
                <h4 class="campus-title">{{ item.title }}</h4>
                <p class="campus-desc">{{ item.description }}</p>
                <div class="campus-tags">
                  <el-tag
                    v-for="(tag, idx) in item.tags"
                    :key="idx"
                    :type="tag.type"
                    size="small"
                    class="campus-tag"
                  >
                    {{ tag.label }}
                  </el-tag>
                </div>
              </el-card>
            </div>

            <el-card class="life-tips-card">
              <div class="tip-header">
                <el-icon class="life-icon"><Bell /></el-icon>
                <span class="tip-title">校园生活小贴士</span>
              </div>
              <div class="tips-grid">
                <div class="tip-item" v-for="(tip, index) in lifeTips" :key="index">
                  <div class="tip-item-icon" :style="{ background: tip.color }">
                    <el-icon><component :is="tip.icon" /></el-icon>
                  </div>
                  <div class="tip-item-content">
                    <span class="tip-item-title">{{ tip.title }}</span>
                    <span class="tip-item-desc">{{ tip.desc }}</span>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>

        <el-tab-pane label="校园服务" name="services">
          <div class="tab-content">
            <div class="service-grid">
              <el-card
                v-for="(item, index) in campusServices"
                :key="index"
                class="service-card"
                shadow="hover"
              >
                <div class="service-icon-wrapper" :style="{ background: item.color }">
                  <el-icon class="service-icon"><component :is="item.icon" /></el-icon>
                </div>
                <h4 class="service-title">{{ item.title }}</h4>
                <div class="service-info">
                  <div class="service-info-item">
                    <el-icon><Clock /></el-icon>
                    <span>{{ item.time }}</span>
                  </div>
                  <div class="service-info-item">
                    <el-icon><MapLocation /></el-icon>
                    <span>{{ item.location }}</span>
                  </div>
                  <div v-if="item.phone" class="service-info-item">
                    <el-icon><Phone /></el-icon>
                    <span>{{ item.phone }}</span>
                  </div>
                </div>
                <div class="service-items">
                  <el-tag
                    v-for="(tag, idx) in item.items"
                    :key="idx"
                    size="small"
                    class="service-tag"
                  >
                    {{ tag }}
                  </el-tag>
                </div>
              </el-card>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Lightning, Search, HomeFilled, Clock, MapLocation, InfoFilled,
  Notebook, OfficeBuilding, Cpu, GoldMedal, Coin, CreditCard,
  Bell, Phone, School, Food, Basketball, Printer,
  FirstAidKit, Postcard, Wallet, Reading, Edit, Check,
  Document, User, Key, Van, Service, Aim, ChatDotRound, Tools,
  Setting, HelpFilled, TrophyBase, Warning
} from '@element-plus/icons-vue'
import { getGuides } from '@/api/freshman'

const activeTab = ref('process')
const activeStep = ref(3)
const guides = ref([])
const loading = ref(false)

const queryForm = reactive({
  studentNo: ''
})
const dormitoryInfo = ref(null)

const processSteps = [
  {
    title: '网上报到',
    description: '登录迎新系统（freshman.xxu.edu.cn），完成个人信息登记、上传证件照、选择军训服装尺码',
    icon: Edit,
    color: 'linear-gradient(135deg, #10b981 0%, #059669 100%)'
  },
  {
    title: '现场确认',
    description: '携带录取通知书、身份证原件到体育馆迎新大厅，核验身份信息，领取校园一卡通和新生礼包',
    icon: Document,
    color: 'linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)'
  },
  {
    title: '宿舍入住',
    description: '凭宿舍分配通知到对应宿舍楼办理入住，在宿管处领取钥匙和宿舍用品（被褥、脸盆等）',
    icon: HomeFilled,
    color: 'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)'
  },
  {
    title: '缴费注册',
    description: '通过校园统一支付平台完成学费、住宿费缴纳，办理学籍注册，领取教材和课程表',
    icon: Wallet,
    color: 'linear-gradient(135deg, #ef4444 0%, #dc2626 100%)'
  },
  {
    title: '体检与保险',
    description: '到校医院进行入学体检（空腹），自愿购买大学生医疗保险（280元/年）',
    icon: User,
    color: 'linear-gradient(135deg, #ec4899 0%, #db2777 100%)'
  },
  {
    title: '入学教育',
    description: '参加学院新生见面会、专业介绍会、安全教育讲座，熟悉校园环境和教学设施',
    icon: Reading,
    color: 'linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%)'
  }
]

const transportInfo = [
  {
    label: '火车站接站',
    desc: '9月1日-3日 8:00-18:00，学校在火车站出站口设有迎新接待点，有专车接送',
    icon: School,
    color: 'linear-gradient(135deg, #10b981 0%, #059669 100%)'
  },
  {
    label: '公交路线',
    desc: '乘坐5路、12路、28路公交车至"大学城北门"站下车，步行200米即到',
    icon: MapLocation,
    color: 'linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)'
  },
  {
    label: '地铁路线',
    desc: '乘坐地铁2号线至"大学城"站，从A口出站后换乘校园摆渡车（免费）',
    icon: Van,
    color: 'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)'
  },
  {
    label: '自驾导航',
    desc: '导航搜索"XX大学南门"，车辆可凭录取通知书免费入校，校内设有临时停车区',
    icon: MapLocation,
    color: 'linear-gradient(135deg, #ef4444 0%, #dc2626 100%)'
  }
]

const feeItems = [
  { name: '学费', desc: '按专业标准收取（文理科不同）', amount: '4,200-5,500元/学年', note: '具体以录取通知书为准' },
  { name: '住宿费', desc: '四人间（独立卫浴、空调）', amount: '1,200元/学年', note: '含水电基础额度' },
  { name: '教材费', desc: '按专业配备必修教材', amount: '约800元/学年', note: '多退少补' },
  { name: '军训服装费', desc: '含迷彩服、军鞋、帽子等', amount: '200元', note: '一次性收取' },
  { name: '体检费', desc: '入学体检项目', amount: '150元', note: '一次性收取' },
  { name: '大学生医保', desc: '城镇居民基本医疗保险', amount: '280元/年', note: '自愿购买' }
]
const totalFee = '约7,630-8,930元/学年（不含医保）'

const paymentMethods = [
  { name: '校园统一支付平台', desc: '登录pay.xxu.edu.cn在线支付', icon: Coin, color: 'linear-gradient(135deg, #10b981 0%, #059669 100%)' },
  { name: '微信支付', desc: '关注"XX大学财务处"公众号缴费', icon: ChatDotRound, color: 'linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)' },
  { name: '支付宝', desc: '搜索"XX大学"生活号缴费', icon: Aim, color: 'linear-gradient(135deg, #6366f1 0%, #4f46e5 100%)' },
  { name: '银行转账', desc: '户名：XX大学，账号：XXXXXXXXX，开户行：工商银行', icon: OfficeBuilding, color: 'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)' },
  { name: '现场缴费', desc: '报到日可到财务处现场刷卡或现金缴费', icon: Service, color: 'linear-gradient(135deg, #ec4899 0%, #db2777 100%)' }
]

const campusIntro = [
  { id: 1, title: '图书馆', description: '馆藏纸质图书100余万册，电子图书50万册，设有自习区、研讨室、电子阅览室', icon: Notebook, gradient: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)', tags: [{ label: '8:00-22:00', type: 'info' }, { label: '免费WiFi', type: 'success' }] },
  { id: 2, title: '教学楼', description: '第一至第六教学楼，多媒体教室全覆盖，配备空调和智能黑板', icon: OfficeBuilding, gradient: 'linear-gradient(135deg, #10b981 0%, #059669 100%)', tags: [{ label: '6:30-22:00', type: 'info' }, { label: '通宵自习室', type: 'warning' }] },
  { id: 3, title: '实验楼', description: '理工科实验中心、计算机房、语音室，配备先进实验设备', icon: Cpu, gradient: 'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)', tags: [{ label: '预约使用', type: 'info' }, { label: '刷卡进入', type: 'danger' }] },
  { id: 4, title: '体育馆', description: '综合体育馆含篮球场、羽毛球场、乒乓球馆、游泳馆、健身房', icon: GoldMedal, gradient: 'linear-gradient(135deg, #ec4899 0%, #db2777 100%)', tags: [{ label: '6:00-21:30', type: 'info' }, { label: '免费开放', type: 'success' }] },
  { id: 5, title: '食堂', description: '第一至第四食堂、民族餐厅、西餐厅，提供多样化的餐饮选择', icon: Food, gradient: 'linear-gradient(135deg, #ef4444 0%, #dc2626 100%)', tags: [{ label: '6:30-22:00', type: 'info' }, { label: '支持校园卡', type: 'success' }] },
  { id: 6, title: '运动场', description: '标准400米田径场、足球场、篮球场、排球场、网球场', icon: Basketball, gradient: 'linear-gradient(135deg, #f97316 0%, #ea580c 100%)', tags: [{ label: '全天开放', type: 'info' }, { label: '灯光球场', type: 'warning' }] }
]

const lifeTips = [
  { title: '校园卡使用', desc: '一卡通可用于食堂消费、图书馆借书、门禁通行、热水充值等', icon: Key, color: 'linear-gradient(135deg, #10b981 0%, #059669 100%)' },
  { title: '网络服务', desc: '校园WiFi全覆盖（XXU-WLAN），学号登录，每月免费30GB流量', icon: Cpu, color: 'linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)' },
  { title: '快递服务', desc: '菜鸟驿站位于北门旁，支持顺丰、京东、中通、圆通等主流快递', icon: Postcard, color: 'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)' },
  { title: '医疗服务', desc: '校医院24小时值班，急诊电话：12345678，位于图书馆东侧', icon: FirstAidKit, color: 'linear-gradient(135deg, #ef4444 0%, #dc2626 100%)' },
  { title: '打印复印', desc: '各教学楼一楼设有自助打印机，图书馆二楼提供打印复印服务', icon: Printer, color: 'linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%)' },
  { title: '银行服务', desc: '校内设有工商银行、建设银行ATM机，位于食堂一楼大厅', icon: OfficeBuilding, color: 'linear-gradient(135deg, #ec4899 0%, #db2777 100%)' }
]

const campusServices = [
  { title: '学生事务中心', time: '周一至周五 8:30-17:30', location: '行政楼一楼', phone: '12345678', icon: Service, color: '#6366f1', items: ['学籍证明', '学生证补办', '助学贷款咨询', '保险理赔'] },
  { title: '后勤服务中心', time: '周一至周日 8:00-20:00', location: '后勤楼101室', phone: '12345679', icon: Setting, color: '#10b981', items: ['宿舍报修', '水电缴费', '空调租赁', '家具维修'] },
  { title: '网络服务中心', time: '周一至周五 9:00-18:00', location: '图书馆二楼', phone: '12345680', icon: Cpu, color: '#3b82f6', items: ['网络开户', '密码重置', '故障报修', '校园卡办理'] },
  { title: '心理咨询中心', time: '周一至周五 9:00-17:00', location: '学生活动中心三楼', phone: '12345681', icon: HelpFilled, color: '#ec4899', items: ['心理咨询预约', '心理测评', '团体辅导', '危机干预'] },
  { title: '就业指导中心', time: '周一至周五 8:30-17:30', location: '教学楼A座101', phone: '12345682', icon: TrophyBase, color: '#f59e0b', items: ['职业规划', '简历指导', '招聘信息', '创业扶持'] },
  { title: '保卫处', time: '24小时值班', location: '学校正门东侧', phone: '12345683', icon: Warning, color: '#ef4444', items: ['户籍管理', '车辆通行', '监控查询', '失物招领'] }
]

const queryDormitory = () => {
  if (!queryForm.studentNo) {
    ElMessage.warning('请输入学号')
    return
  }
  loading.value = true
  setTimeout(() => {
    const studentId = queryForm.studentNo
    const building = ['学宿1号楼', '学宿2号楼', '学宿3号楼', '学宿4号楼', '学宿5号楼', '学宿6号楼'][Math.floor(Math.random() * 6)]
    const room = `${Math.floor(Math.random() * 6) + 1}${String(Math.floor(Math.random() * 20) + 1).padStart(2, '0')}`
    const bed = ['A01', 'A02', 'B01', 'B02'][Math.floor(Math.random() * 4)]
    dormitoryInfo.value = {
      studentNo: studentId,
      name: '张同学',
      building: building,
      room: room,
      bed: bed,
      manager: '李管理员'
    }
    loading.value = false
  }, 800)
}

const onTabChange = (tab) => {
  activeTab.value = tab
}

onMounted(() => {
  getGuides().then(res => {
    guides.value = res.data || []
  })
})
</script>

<style scoped>
.guide-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
}

.tabs-card {
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.tab-content {
  padding: 8px 0;
}

.guide-tabs {
  margin-bottom: 24px;
}

.guide-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  padding: 0 20px;
}

.guide-tabs :deep(.el-tabs__item.is-active) {
  font-weight: 600;
}

.steps-wrapper {
  margin-bottom: 24px;
}

.steps :deep(.el-step__title) {
  font-size: 15px;
}

.steps :deep(.el-step__description) {
  font-size: 13px;
  line-height: 1.6;
}

.step-icon-wrapper {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.tip-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.tip-title {
  font-size: 17px;
  font-weight: 600;
  color: #1a1a2e;
}

.tip-icon,
.transport-icon,
.rules-icon,
.life-icon,
.scholarship-icon {
  font-size: 22px;
  color: #4f46e5;
}

.tips-card,
.transport-card,
.dorm-rules-card,
.life-tips-card,
.scholarship-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
}

.tip-list,
.rules-list,
.scholarship-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tip-list li,
.scholarship-list li {
  padding: 6px 0;
  color: #475569;
  font-size: 14px;
  line-height: 1.8;
}

.tip-list li::before {
  content: '•';
  color: #4f46e5;
  font-weight: bold;
  margin-right: 8px;
}

.scholarship-list li::before {
  content: '✦';
  color: #f59e0b;
  font-weight: bold;
  margin-right: 8px;
}

.transport-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.transport-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.transport-item:hover {
  background: #f1f5f9;
  transform: translateX(4px);
}

.transport-icon-wrapper {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
  flex-shrink: 0;
}

.transport-detail {
  flex: 1;
}

.transport-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 2px;
}

.transport-desc {
  display: block;
  font-size: 13px;
  color: #64748b;
  line-height: 1.6;
}

.search-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
}

.search-form {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.search-input {
  width: 240px;
}

.result-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: 1px solid #bbf7d0;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
}

.result-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.result-icon {
  font-size: 24px;
  color: #16a34a;
}

.result-title {
  font-size: 18px;
  font-weight: 600;
  color: #166534;
  margin: 0;
}

.dorm-info :deep(.el-descriptions__label) {
  font-weight: 600;
  color: #475569;
}

.desc-value {
  color: #1a1a2e;
  font-weight: 500;
}

.empty-state {
  text-align: center;
  padding: 48px 20px;
  background: #f8fafc;
  border-radius: 12px;
  margin-bottom: 20px;
}

.empty-icon-wrapper {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: #e0e7ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.empty-icon {
  font-size: 32px;
  color: #6366f1;
}

.empty-text {
  font-size: 15px;
  color: #94a3b8;
  margin: 0;
}

.rules-list li {
  padding: 8px 0;
  color: #475569;
  font-size: 14px;
  line-height: 1.8;
  border-bottom: 1px solid #f5f5f5;
}

.rules-list li:last-child {
  border-bottom: none;
}

.rules-list li::before {
  content: '✓';
  color: #10b981;
  font-weight: bold;
  margin-right: 8px;
}

.fee-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0 0 16px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-title-icon {
  font-size: 22px;
  color: #4f46e5;
}

.fee-card :deep(.el-table th) {
  background: #f8fafc;
  font-weight: 600;
  color: #1a1a2e;
}

.fee-total {
  text-align: right;
  padding: 16px 20px;
  background: #f8fafc;
  border-radius: 8px;
  margin-top: 16px;
  font-size: 16px;
}

.total-label {
  color: #475569;
  margin-right: 12px;
}

.total-amount {
  color: #ef4444;
  font-size: 24px;
  font-weight: 700;
}

.payment-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
}

.payment-methods {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.method-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 16px;
  background: #f8fafc;
  border-radius: 12px;
  text-align: center;
  transition: all 0.3s ease;
}

.method-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.method-icon-wrapper {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: #fff;
}

.method-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a2e;
}

.method-desc {
  font-size: 12px;
  color: #64748b;
  line-height: 1.5;
}

.deadline-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: 1px solid #fecaca;
  background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%);
}

.deadline-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.deadline-icon {
  font-size: 36px;
  color: #ef4444;
  flex-shrink: 0;
}

.deadline-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.deadline-title {
  font-size: 16px;
  font-weight: 600;
  color: #991b1b;
}

.deadline-date {
  font-size: 20px;
  font-weight: 700;
  color: #dc2626;
}

.deadline-tip {
  font-size: 13px;
  color: #b91c1c;
}

.campus-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(340px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.campus-card {
  border-radius: 16px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.campus-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
}

.campus-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  margin-bottom: 16px;
}

.campus-icon {
  font-size: 24px;
}

.campus-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0 0 8px 0;
}

.campus-desc {
  font-size: 14px;
  color: #475569;
  line-height: 1.7;
  margin: 0 0 12px 0;
}

.campus-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.campus-tag {
  padding: 2px 10px;
}

.life-tips-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
}

.tips-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(340px, 1fr));
  gap: 12px;
}

.tip-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 14px 16px;
  background: #f8fafc;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.tip-item:hover {
  background: #f1f5f9;
  transform: translateX(4px);
}

.tip-item-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
  flex-shrink: 0;
}

.tip-item-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.tip-item-title {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a2e;
}

.tip-item-desc {
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(340px, 1fr));
  gap: 20px;
}

.service-card {
  border-radius: 16px;
  border: 1px solid #f0f0f0;
  padding: 20px;
  transition: all 0.3s ease;
}

.service-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
}

.service-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  margin-bottom: 16px;
}

.service-icon-wrapper .service-icon {
  font-size: 24px;
}

.service-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0 0 16px 0;
}

.service-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.service-info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 0;
  font-size: 14px;
  color: #475569;
}

.service-info-item .el-icon {
  color: #94a3b8;
  flex-shrink: 0;
}

.service-items {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.service-tag {
  padding: 4px 12px;
  background: #f8fafc;
  border-radius: 6px;
  font-size: 12px;
  color: #64748b;
  border: 1px solid #e2e8f0;
}

@media (max-width: 768px) {
  .guide-container {
    padding: 16px;
  }

  .page-title {
    font-size: 22px;
  }

  .campus-grid,
  .tips-grid,
  .service-grid {
    grid-template-columns: 1fr;
  }

  .payment-methods {
    grid-template-columns: repeat(2, 1fr);
  }

  .search-input {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .payment-methods {
    grid-template-columns: 1fr;
  }
}
</style>