<template>
  <div class="campus-map-container">
    <div class="map-header">
      <h2 class="page-title">
        <el-icon><MapLocation /></el-icon>
        校园地图
      </h2>
      <div class="map-controls">
        <el-button-group>
          <el-button :type="mapMode === 'view' ? 'primary' : 'default'" @click="switchMode('view')">
            <el-icon><View /></el-icon>
            地图浏览
          </el-button>
          <el-button :type="mapMode === 'navigate' ? 'primary' : 'default'" @click="switchMode('navigate')">
            <el-icon><Compass /></el-icon>
            路线规划
          </el-button>
          <el-button :type="mapMode === 'search' ? 'primary' : 'default'" @click="switchMode('search')">
            <el-icon><Search /></el-icon>
            周边搜索
          </el-button>
        </el-button-group>
        <el-button type="success" @click="locateUser" :loading="locating">
          <el-icon><Aim /></el-icon>
          定位
        </el-button>
      </div>
    </div>

    <div class="map-body">
      <div class="map-sidebar" v-if="mapMode !== 'view'">
        <div v-if="mapMode === 'navigate'" class="navigate-panel">
          <h3>路线规划</h3>
          <el-form :model="navigateForm" label-width="60px">
            <el-form-item label="起点">
              <el-select v-model="navigateForm.startId" placeholder="选择起点" filterable clearable @change="onStartChange">
                <el-option
                  v-for="poi in poiList"
                  :key="poi.id"
                  :label="poi.name"
                  :value="poi.id"
                >
                  <span>{{ poi.name }}</span>
                  <span style="float: right; color: #8492a6; font-size: 12px">{{ poi.category }}</span>
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="终点">
              <el-select v-model="navigateForm.endId" placeholder="选择终点" filterable clearable @change="onEndChange">
                <el-option
                  v-for="poi in poiList"
                  :key="poi.id"
                  :label="poi.name"
                  :value="poi.id"
                >
                  <span>{{ poi.name }}</span>
                  <span style="float: right; color: #8492a6; font-size: 12px">{{ poi.category }}</span>
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="方式">
              <el-radio-group v-model="navigateForm.type">
                <el-radio-button value="walking">步行</el-radio-button>
                <el-radio-button value="driving">驾车</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="startNavigate" style="width: 100%" :loading="navigating">
                开始导航
              </el-button>
            </el-form-item>
          </el-form>
          <div v-if="navigateResult" class="navigate-result">
            <div class="result-item">
              <span>距离：</span>
              <strong>{{ navigateResult.distance }}</strong>
            </div>
            <div class="result-item">
              <span>预计时间：</span>
              <strong>{{ navigateResult.duration }}</strong>
            </div>
            <el-button type="danger" size="small" @click="clearRoute" style="margin-top: 8px; width: 100%">
              清除路线
            </el-button>
          </div>
          <div class="navigate-tip">
            <el-alert
              title="提示：也可直接在地图上点击选择起点/终点"
              type="info"
              :closable="false"
              show-icon
              size="small"
            />
          </div>
        </div>

        <div v-if="mapMode === 'search'" class="search-panel">
          <h3>周边搜索</h3>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索地点、设施..."
            clearable
            @keyup.enter="searchNearby"
          >
            <template #append>
              <el-button @click="searchNearby" :loading="searching">搜索</el-button>
            </template>
          </el-input>
          <div class="search-categories">
            <el-tag
              v-for="cat in searchCategories"
              :key="cat.value"
              :type="searchCategory === cat.value ? 'primary' : 'info'"
              @click="selectCategory(cat.value)"
              style="cursor: pointer; margin: 4px"
              effect="plain"
            >
              {{ cat.label }}
            </el-tag>
          </div>
          <div v-if="searchResults.length > 0" class="search-results">
            <div
              v-for="item in searchResults"
              :key="item.id"
              class="search-result-item"
              @click="focusPoi(item)"
            >
              <div class="result-name">{{ item.name }}</div>
              <div class="result-address">{{ item.address }}</div>
              <div class="result-distance">{{ item.distance }}米</div>
            </div>
          </div>
          <div v-else-if="searched" class="no-results">
            <el-empty description="未找到相关结果" />
          </div>
        </div>
      </div>

      <div class="map-wrapper">
        <div v-if="mapLoading" class="map-loading">
          <el-icon class="loading-icon" :size="32"><Loading /></el-icon>
          <span>地图加载中...</span>
        </div>
        <div v-if="mapError" class="map-error">
          <el-result icon="error" title="地图加载失败" :sub-title="mapError">
            <template #extra>
              <el-button type="primary" @click="retryLoadMap">重新加载</el-button>
            </template>
          </el-result>
        </div>
        <div id="campusMap" class="map-container" v-show="!mapLoading && !mapError"></div>
        <div class="map-legend" v-if="!mapLoading && !mapError">
          <div class="legend-title">图例</div>
          <div class="legend-item">
            <span class="legend-dot" style="background: #409eff"></span>
            <span>教学楼</span>
          </div>
          <div class="legend-item">
            <span class="legend-dot" style="background: #67c23a"></span>
            <span>食堂</span>
          </div>
          <div class="legend-item">
            <span class="legend-dot" style="background: #e6a23c"></span>
            <span>宿舍</span>
          </div>
          <div class="legend-item">
            <span class="legend-dot" style="background: #f56c6c"></span>
            <span>图书馆</span>
          </div>
          <div class="legend-item">
            <span class="legend-dot" style="background: #909399"></span>
            <span>运动场</span>
          </div>
          <div class="legend-item">
            <span class="legend-dot user-location-dot"></span>
            <span>当前位置</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { MapLocation, View, Compass, Search, Aim, Loading } from '@element-plus/icons-vue'

const mapMode = ref('view')
const map = ref(null)
const geolocation = ref(null)
const placeSearch = ref(null)
const driving = ref(null)
const walking = ref(null)
const markers = ref([])
const userMarker = ref(null)
const navigateResult = ref(null)
const mapLoading = ref(true)
const mapError = ref('')
const locating = ref(false)
const navigating = ref(false)
const searching = ref(false)
const searched = ref(false)

const navigateForm = ref({
  start: null,
  end: null,
  startId: null,
  endId: null,
  type: 'walking'
})

const searchKeyword = ref('')
const searchCategory = ref('')
const searchResults = ref([])

const searchCategories = [
  { label: '全部', value: '' },
  { label: '食堂', value: '餐饮服务' },
  { label: '超市', value: '购物' },
  { label: '医院', value: '医疗保健' },
  { label: '银行', value: '金融服务' },
  { label: '快递', value: '生活服务' }
]

const poiList = ref([
  { id: 1, name: '图书馆', category: '图书馆', lng: 117.1880, lat: 31.8412 },
  { id: 2, name: '教学楼A座', category: '教学楼', lng: 117.1875, lat: 31.8420 },
  { id: 3, name: '教学楼B座', category: '教学楼', lng: 117.1882, lat: 31.8422 },
  { id: 4, name: '第一食堂', category: '食堂', lng: 117.1868, lat: 31.8415 },
  { id: 5, name: '第二食堂', category: '食堂', lng: 117.1878, lat: 31.8408 },
  { id: 6, name: '学生宿舍1号楼', category: '宿舍', lng: 117.1865, lat: 31.8420 },
  { id: 7, name: '学生宿舍2号楼', category: '宿舍', lng: 117.1868, lat: 31.8425 },
  { id: 8, name: '学生宿舍3号楼', category: '宿舍', lng: 117.1875, lat: 31.8428 },
  { id: 9, name: '运动场', category: '运动场', lng: 117.1860, lat: 31.8410 },
  { id: 10, name: '校医院', category: '其他', lng: 117.1885, lat: 31.8418 },
  { id: 11, name: '行政楼', category: '其他', lng: 117.1870, lat: 31.8405 },
  { id: 12, name: '体育馆', category: '运动场', lng: 117.1858, lat: 31.8415 }
])

const categoryColors = {
  '教学楼': '#409eff',
  '食堂': '#67c23a',
  '宿舍': '#e6a23c',
  '图书馆': '#f56c6c',
  '运动场': '#909399',
  '其他': '#909399'
}

const switchMode = (mode) => {
  mapMode.value = mode
  nextTick(() => {
    if (map.value) {
      map.value.resize()
    }
  })
}

const selectCategory = (value) => {
  searchCategory.value = value
  searchNearby()
}

const onStartChange = (val) => {
  if (val) {
    const poi = poiList.value.find(p => p.id === val)
    if (poi) {
      navigateForm.value.start = poi
      if (map.value) {
        map.value.setCenter([poi.lng, poi.lat])
        map.value.setZoom(17)
      }
    }
  } else {
    navigateForm.value.start = null
  }
}

const onEndChange = (val) => {
  if (val) {
    const poi = poiList.value.find(p => p.id === val)
    if (poi) {
      navigateForm.value.end = poi
      if (map.value) {
        map.value.setCenter([poi.lng, poi.lat])
        map.value.setZoom(17)
      }
    }
  } else {
    navigateForm.value.end = null
  }
}

const createMarker = (poi) => {
  const content = `
    <div style="
      background: ${categoryColors[poi.category] || '#909399'};
      color: white;
      padding: 4px 8px;
      border-radius: 4px;
      font-size: 12px;
      white-space: nowrap;
      box-shadow: 0 2px 6px rgba(0,0,0,0.3);
      cursor: pointer;
    ">${poi.name}</div>
  `
  const marker = new AMap.Marker({
    position: [poi.lng, poi.lat],
    content: content,
    offset: new AMap.Pixel(-30, -15)
  })
  marker.setMap(map.value)
  marker.on('click', () => {
    showPoiInfo(poi)
  })
  markers.value.push(marker)
  return marker
}

const showPoiInfo = (poi) => {
  ElMessage({
    message: `${poi.name}（${poi.category}）`,
    duration: 2000,
    offset: 80
  })
  map.value.setCenter([poi.lng, poi.lat])
  map.value.setZoom(17)
}

const initMap = () => {
  if (typeof AMap === 'undefined') {
    mapError.value = '高德地图API加载失败，请检查网络连接'
    mapLoading.value = false
    return
  }

  try {
    map.value = new AMap.Map('campusMap', {
      zoom: 16,
      center: [117.187295, 31.841708],
      viewMode: '2D',
      mapStyle: 'amap://styles/light',
      resizeEnable: true
    })

    poiList.value.forEach(poi => {
      createMarker(poi)
    })

    geolocation.value = new AMap.Geolocation({
      enableHighAccuracy: true,
      timeout: 10000,
      buttonPosition: 'RB',
      buttonOffset: new AMap.Pixel(10, 20),
      zoomToAccuracy: true
    })
    map.value.addControl(geolocation.value)

    placeSearch.value = new AMap.PlaceSearch({
      pageSize: 10,
      pageIndex: 1,
      city: '合肥',
      citylimit: true
    })

    mapLoading.value = false
  } catch (e) {
    mapError.value = '地图初始化失败：' + e.message
    mapLoading.value = false
  }
}

const locateUser = () => {
  if (!geolocation.value) {
    ElMessage.warning('地图未加载完成')
    return
  }
  locating.value = true
  ElMessage.info('正在获取位置...')
  geolocation.value.getCurrentPosition((status, result) => {
    locating.value = false
    if (status === 'complete') {
      const lng = result.position.lng
      const lat = result.position.lat
      if (userMarker.value) {
        userMarker.value.setPosition([lng, lat])
      } else {
        userMarker.value = new AMap.Marker({
          position: [lng, lat],
          content: `<div style="
            width: 16px;
            height: 16px;
            background: #409eff;
            border-radius: 50%;
            border: 3px solid #fff;
            box-shadow: 0 0 0 2px #409eff, 0 2px 6px rgba(0,0,0,0.3);
          "></div>`,
          offset: new AMap.Pixel(-8, -8)
        })
        userMarker.value.setMap(map.value)
      }
      map.value.setCenter([lng, lat])
      map.value.setZoom(17)
      ElMessage.success('定位成功')
    } else {
      ElMessage.error('定位失败：' + (result.message || '未知错误'))
    }
  })
}

const startNavigate = () => {
  const start = navigateForm.value.start
  const end = navigateForm.value.end
  if (!start || !end) {
    ElMessage.warning('请选择起点和终点')
    return
  }
  if (start.id === end.id) {
    ElMessage.warning('起点和终点不能相同')
    return
  }

  navigating.value = true
  navigateResult.value = null

  const startLngLat = [start.lng, start.lat]
  const endLngLat = [end.lng, end.lat]

  const routeComplete = (status, result) => {
    navigating.value = false
    if (status === 'complete') {
      const route = result.routes[0]
      const distance = route.distance
      const duration = route.time
      navigateResult.value = {
        distance: distance >= 1000 ? (distance / 1000).toFixed(1) + '公里' : distance + '米',
        duration: duration >= 60 ? Math.ceil(duration / 60) + '分钟' : duration + '秒'
      }
      ElMessage.success('路线规划成功')
    } else {
      ElMessage.error('路线规划失败，请重试')
    }
  }

  if (navigateForm.value.type === 'driving') {
    if (!driving.value) {
      driving.value = new AMap.Driving({
        map: map.value,
        policy: 0
      })
    }
    driving.value.search(startLngLat, endLngLat, routeComplete)
  } else {
    if (!walking.value) {
      walking.value = new AMap.Walking({
        map: map.value
      })
    }
    walking.value.search(startLngLat, endLngLat, routeComplete)
  }
}

const clearRoute = () => {
  if (driving.value) {
    driving.value.clear()
  }
  if (walking.value) {
    walking.value.clear()
  }
  navigateResult.value = null
  navigateForm.value.start = null
  navigateForm.value.end = null
  navigateForm.value.startId = null
  navigateForm.value.endId = null
}

const searchNearby = () => {
  const keyword = searchKeyword.value || searchCategory.value
  if (!keyword) {
    ElMessage.warning('请输入搜索关键词或选择分类')
    return
  }

  searching.value = true
  searched.value = true

  const center = map.value.getCenter()
  const lng = center.lng
  const lat = center.lat

  if (!placeSearch.value) {
    placeSearch.value = new AMap.PlaceSearch({
      pageSize: 10,
      pageIndex: 1,
      city: '合肥',
      citylimit: true,
      map: map.value
    })
  }

  placeSearch.value.searchNearBy(keyword, [lng, lat], 1000, (status, result) => {
    searching.value = false
    if (status === 'complete' && result.poiList) {
      searchResults.value = result.poiList.pois.map(poi => ({
        id: poi.id,
        name: poi.name,
        address: poi.address || '暂无地址',
        distance: Math.round(poi.distance || 0),
        location: poi.location
      }))
      if (searchResults.value.length === 0) {
        ElMessage.info('未找到相关结果')
      } else {
        ElMessage.success(`找到 ${searchResults.value.length} 个结果`)
      }
    } else {
      searchResults.value = []
      ElMessage.info('未找到相关结果')
    }
  })
}

const focusPoi = (item) => {
  let lng, lat
  if (typeof item.location === 'object') {
    lng = item.location.lng
    lat = item.location.lat
  } else {
    const parts = item.location.split(',')
    lng = parseFloat(parts[0])
    lat = parseFloat(parts[1])
  }
  map.value.setCenter([lng, lat])
  map.value.setZoom(18)
  const marker = new AMap.Marker({
    position: [lng, lat],
    title: item.name
  })
  marker.setMap(map.value)
  markers.value.push(marker)
}

const retryLoadMap = () => {
  mapError.value = ''
  mapLoading.value = true
  nextTick(() => {
    initMap()
  })
}

onMounted(() => {
  nextTick(() => {
    initMap()
  })
})

onBeforeUnmount(() => {
  if (map.value) {
    map.value.destroy()
  }
})
</script>

<style scoped>
.campus-map-container {
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
  border-radius: 12px;
  overflow: hidden;
}

.map-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: white;
  border-bottom: 1px solid #e4e7ed;
  flex-shrink: 0;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.map-controls {
  display: flex;
  gap: 12px;
  align-items: center;
}

.map-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.map-sidebar {
  width: 320px;
  background: white;
  border-right: 1px solid #e4e7ed;
  padding: 20px;
  overflow-y: auto;
  flex-shrink: 0;
}

.navigate-panel h3,
.search-panel h3 {
  margin: 0 0 16px;
  font-size: 16px;
  color: #303133;
}

.navigate-result {
  margin-top: 16px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.result-item {
  display: flex;
  justify-content: space-between;
  padding: 4px 0;
  color: #606266;
}

.navigate-tip {
  margin-top: 12px;
}

.search-categories {
  margin: 12px 0;
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.search-results {
  margin-top: 16px;
}

.search-result-item {
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.search-result-item:hover {
  border-color: #409eff;
  background: #ecf5ff;
}

.result-name {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.result-address {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.result-distance {
  font-size: 12px;
  color: #409eff;
}

.map-wrapper {
  flex: 1;
  position: relative;
}

.map-container {
  width: 100%;
  height: 100%;
}

.map-loading {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
  font-size: 16px;
  gap: 16px;
  z-index: 1000;
}

.loading-icon {
  animation: rotating 1.5s linear infinite;
}

@keyframes rotating {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.map-error {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  z-index: 1000;
}

.map-legend {
  position: absolute;
  bottom: 20px;
  left: 20px;
  background: white;
  padding: 12px 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.legend-title {
  font-weight: 600;
  margin-bottom: 8px;
  color: #303133;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 2px 0;
  font-size: 12px;
  color: #606266;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 2px;
  display: inline-block;
}

.user-location-dot {
  width: 12px;
  height: 12px;
  background: #409eff;
  border-radius: 50%;
  border: 2px solid #fff;
  box-shadow: 0 0 0 2px #409eff;
}

.no-results {
  margin-top: 20px;
}
</style>