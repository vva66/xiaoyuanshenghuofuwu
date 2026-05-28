<template>
  <div class="canteen-list-container">
    <div class="page-header">
      <div class="header-title">
        <h2 class="page-title">食堂推荐</h2>
        <p class="page-subtitle">发现校园美食，品味舌尖上的校园</p>
      </div>
    </div>
    
    <el-skeleton v-if="loading" :rows="5" animated class="skeleton-loading" />
    
    <div v-else class="canteens-grid">
      <el-card 
        v-for="canteen in canteens" 
        :key="canteen.id" 
        class="canteen-card"
        shadow="hover"
        @click="showCanteenDetail(canteen.id)"
      >
        <div class="canteen-image-wrapper">
          <img v-if="canteen.image" :src="canteen.image" class="canteen-image" />
          <div v-else class="canteen-placeholder">
            <el-icon class="placeholder-icon"><ForkSpoon /></el-icon>
          </div>
          <div class="canteen-rating-badge">
            <el-rate v-model="canteen.rating" disabled show-score text-color="#fff" score-template="{value}" />
          </div>
        </div>
        <div class="canteen-content">
          <h3 class="canteen-name">{{ canteen.name }}</h3>
          <div class="canteen-meta">
            <div class="meta-item">
              <el-icon class="meta-icon"><MapLocation /></el-icon>
              <span class="meta-text">{{ canteen.address }}</span>
            </div>
            <div class="meta-item">
              <el-icon class="meta-icon"><Clock /></el-icon>
              <span class="meta-text">{{ canteen.openingHours }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>
    
    <el-dialog 
      v-model="showDetail" 
      :title="selectedCanteen?.name" 
      width="900px"
      class="detail-dialog"
    >
      <div v-if="selectedCanteen" class="dialog-content">
        <div class="detail-header">
          <div class="detail-info">
            <el-descriptions :column="3" class="detail-desc">
              <el-descriptions-item label="地址" class="desc-item">
                <span class="desc-value">{{ selectedCanteen.address }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="营业时间" class="desc-item">
                <span class="desc-value">{{ selectedCanteen.openingHours }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="联系电话" class="desc-item">
                <span class="desc-value">{{ selectedCanteen.contactPhone }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </div>
        
        <div class="dishes-section">
          <h3 class="section-title">
            <el-icon><Food /></el-icon>
            推荐菜品
          </h3>
          <div class="dishes-grid">
            <el-card 
              v-for="dish in dishes" 
              :key="dish.id" 
              class="dish-card"
              shadow="hover"
            >
              <div class="dish-image-wrapper">
                <img v-if="dish.image" :src="dish.image" class="dish-image" />
                <div v-else class="dish-placeholder">
                  <el-icon class="dish-icon"><Apple /></el-icon>
                </div>
              </div>
              <div class="dish-content">
                <h4 class="dish-name">{{ dish.dishName }}</h4>
                <div class="dish-footer">
                  <span class="dish-price">¥{{ dish.price }}</span>
                  <el-rate v-model="dish.rating" disabled size="small" />
                </div>
              </div>
            </el-card>
          </div>
          
          <div v-if="dishes.length === 0" class="empty-dishes">
            <div class="empty-icon-wrapper">
              <el-icon class="empty-icon"><Search /></el-icon>
            </div>
            <p class="empty-text">暂无推荐菜品</p>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCanteenList, getCanteenDetail, getDishesByCanteenId } from '@/api/canteen'
import { MapLocation, Clock, ForkSpoon, Food, Apple, Search } from '@element-plus/icons-vue'

const loading = ref(true)
const canteens = ref([])
const showDetail = ref(false)
const selectedCanteen = ref(null)
const dishes = ref([])

const loadCanteens = async () => {
  try {
    loading.value = true
    try {
      const result = await getCanteenList({ pageNum: 1, pageSize: 10 })
      canteens.value = result.records || []
    } catch (error) {
      canteens.value = [
        { id: 1, name: '第一食堂', description: '环境优雅，菜品丰富', address: '学生生活区A区', rating: 4.5, image: 'https://picsum.photos/seed/canteen-east/800/400', openingHours: '06:30-21:00', contactPhone: '010-12345678' },
        { id: 2, name: '第二食堂', description: '价格实惠，味道正宗', address: '学生生活区B区', rating: 4.2, image: 'https://picsum.photos/seed/canteen-west/800/400', openingHours: '06:30-21:00', contactPhone: '010-12345679' },
        { id: 3, name: '第三食堂', description: '特色小吃，应有尽有', address: '教学区附近', rating: 4.8, image: 'https://picsum.photos/seed/canteen-south/800/400', openingHours: '07:00-20:00', contactPhone: '010-12345680' },
        { id: 4, name: '清真食堂', description: '清真食品，口味独特', address: '学生生活区C区', rating: 4.6, image: 'https://picsum.photos/seed/canteen-halal/800/400', openingHours: '07:00-20:00', contactPhone: '010-12345680' }
      ]
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const mockDishes = {
  1: [
    { id: 1, dishName: '豆浆油条', price: 3.50, rating: 4.6, image: 'https://picsum.photos/seed/food-doujiang/400/300', windowName: '早餐窗口' },
    { id: 2, dishName: '包子套餐', price: 5.00, rating: 4.4, image: 'https://picsum.photos/seed/food-baozi/400/300', windowName: '早餐窗口' },
    { id: 3, dishName: '番茄炒蛋盖饭', price: 12.00, rating: 4.5, image: 'https://picsum.photos/seed/food-fanqie/400/300', windowName: '中餐窗口1' },
    { id: 4, dishName: '宫保鸡丁盖饭', price: 15.00, rating: 4.7, image: 'https://picsum.photos/seed/food-gongbao/400/300', windowName: '中餐窗口1' },
    { id: 5, dishName: '红烧肉套餐', price: 18.00, rating: 4.8, image: 'https://picsum.photos/seed/food-hongshao/400/300', windowName: '中餐窗口2' },
    { id: 6, dishName: '麻辣烫', price: 15.00, rating: 4.3, image: 'https://picsum.photos/seed/food-malatang/400/300', windowName: '晚餐窗口' }
  ],
  2: [
    { id: 7, dishName: '兰州拉面', price: 10.00, rating: 4.5, image: 'https://picsum.photos/seed/food-lamian/400/300', windowName: '特色面食' },
    { id: 8, dishName: '刀削面', price: 11.00, rating: 4.4, image: 'https://picsum.photos/seed/food-daoxiao/400/300', windowName: '特色面食' },
    { id: 9, dishName: '麻婆豆腐', price: 10.00, rating: 4.6, image: 'https://picsum.photos/seed/food-mapodoufu/400/300', windowName: '川味窗口' },
    { id: 10, dishName: '水煮肉片', price: 16.00, rating: 4.7, image: 'https://picsum.photos/seed/food-shuizhu/400/300', windowName: '川味窗口' },
    { id: 11, dishName: '炸鸡套餐', price: 20.00, rating: 4.2, image: 'https://picsum.photos/seed/food-zhaji/400/300', windowName: '快餐窗口' }
  ],
  3: [
    { id: 12, dishName: '兰州牛肉拉面', price: 12.00, rating: 4.8, image: 'https://picsum.photos/seed/food-niurou/400/300', windowName: '清真窗口' },
    { id: 13, dishName: '大盘鸡', price: 38.00, rating: 4.9, image: 'https://picsum.photos/seed/food-dapanji/400/300', windowName: '清真窗口' },
    { id: 14, dishName: '手抓饭', price: 15.00, rating: 4.6, image: 'https://picsum.photos/seed/food-shouzhua/400/300', windowName: '清真窗口' },
    { id: 15, dishName: '烤羊肉串', price: 5.00, rating: 4.7, image: 'https://picsum.photos/seed/food-yangrou/400/300', windowName: '清真窗口' }
  ],
  4: [
    { id: 12, dishName: '兰州牛肉拉面', price: 12.00, rating: 4.8, image: 'https://picsum.photos/seed/food-niurou/400/300', windowName: '清真窗口' },
    { id: 13, dishName: '大盘鸡', price: 38.00, rating: 4.9, image: 'https://picsum.photos/seed/food-dapanji/400/300', windowName: '清真窗口' },
    { id: 14, dishName: '手抓饭', price: 15.00, rating: 4.6, image: 'https://picsum.photos/seed/food-shouzhua/400/300', windowName: '清真窗口' },
    { id: 15, dishName: '烤羊肉串', price: 5.00, rating: 4.7, image: 'https://picsum.photos/seed/food-yangrou/400/300', windowName: '清真窗口' }
  ]
}

const showCanteenDetail = async (id) => {
  try {
    selectedCanteen.value = await getCanteenDetail(id)
    dishes.value = await getDishesByCanteenId(id)
    showDetail.value = true
  } catch (error) {
    selectedCanteen.value = canteens.value.find(c => c.id === id)
    dishes.value = mockDishes[id] || []
    showDetail.value = true
  }
}

onMounted(() => {
  loadCanteens()
})
</script>

<style scoped>
.canteen-list-container {
  min-height: 100%;
}

.page-header {
  padding: 24px 32px;
  background: linear-gradient(135deg, #ec4899 0%, #db2777 100%);
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 10px 40px rgba(236, 72, 153, 0.3);
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
  opacity: 0.9;
  margin: 0;
}

.skeleton-loading {
  padding: 20px;
}

.canteens-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.canteen-card {
  cursor: pointer;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.canteen-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1);
}

.canteen-image-wrapper {
  position: relative;
  height: 200px;
}

.canteen-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.canteen-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #fecaca 0%, #fca5a5 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-icon {
  font-size: 48px;
  color: white;
}

.canteen-rating-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.6);
  padding: 6px 12px;
  border-radius: 20px;
}

.canteen-content {
  padding: 20px;
}

.canteen-name {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 12px;
}

.canteen-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-icon {
  font-size: 14px;
  color: #94a3b8;
}

.meta-text {
  font-size: 14px;
  color: #64748b;
}

.detail-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

.detail-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #ec4899 0%, #db2777 100%);
  padding: 20px 24px;
}

.detail-dialog :deep(.el-dialog__title) {
  color: white;
  font-size: 20px;
  font-weight: 600;
}

.detail-dialog :deep(.el-dialog__close) {
  color: white;
}

.dialog-content {
  padding: 24px;
}

.detail-header {
  margin-bottom: 24px;
}

.detail-desc :deep(.el-descriptions__item) {
  padding: 16px;
  background: #f8fafc;
}

.detail-desc :deep(.el-descriptions__label) {
  color: #64748b;
  font-weight: 500;
}

.desc-value {
  color: #1e293b;
  font-weight: 600;
}

.dishes-section {
  margin-top: 24px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 20px;
}

.dishes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.dish-card {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.dish-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.dish-image-wrapper {
  height: 140px;
}

.dish-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.dish-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.dish-icon {
  font-size: 40px;
  color: #d97706;
}

.dish-content {
  padding: 16px;
}

.dish-name {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 10px;
}

.dish-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dish-price {
  font-size: 18px;
  font-weight: 700;
  color: #dc2626;
}

.empty-dishes {
  text-align: center;
  padding: 40px 20px;
  background: #f8fafc;
  border-radius: 12px;
}

.empty-icon-wrapper {
  width: 60px;
  height: 60px;
  margin: 0 auto 12px;
  border-radius: 50%;
  background: #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon {
  font-size: 28px;
  color: #94a3b8;
}

.empty-text {
  font-size: 14px;
  color: #94a3b8;
  margin: 0;
}
</style>
