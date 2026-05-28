# 校园服务系统

## 项目简介

这是一个完整的校园服务系统，包含前端Vue3和后端Spring Boot两个部分。

### 功能模块

### 学生端
- **用户中心**：注册/登录、个人信息管理、密码管理
- **迎新服务**：迎新指南、报到流程、宿舍分配查询、缴费指引
- **课表查询**：课表展示（周/月视图）、课程详情
- **校园活动**：活动列表、活动详情、报名参与
- **食堂推荐**：食堂窗口展示、菜品推荐、评分评论
- **校园导航**：地图展示、地点搜索、路线规划
- **社团活动**：社团列表、社团详情、活动发布
- **互助广场**：求助发布、活动组织、报名参与
- **消息中心**：系统通知、活动提醒

### 管理后台
- **数据统计**：用户总数、活动总数、帖子总数、社团总数等
- **用户管理**：用户列表查询、用户状态管理、信息编辑
- **活动管理**：活动审核、活动删除、活动查看
- **帖子审核**：帖子审核（通过/拒绝）、帖子查看
- **社团管理**：社团列表、社团删除、社团查看
- **食堂管理**：食堂信息管理、食堂状态管理
- **系统设置**：基础设置、通知设置、系统信息查看

## 技术栈

### 后端
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- MySQL 8.0
- JWT认证
- Lombok

### 前端
- Vue 3.4.0
- Vite 5.0
- Element Plus 2.5.0
- Vue Router 4.2.5
- Pinia 2.1.7
- Axios 1.6.2

## 安装与启动

### 数据库配置

1. 创建MySQL数据库并执行 `database.sql` 脚本初始化数据

```bash
mysql -u root -p < database.sql
```

### 后端启动

1. 进入后端目录
```bash
cd backend
```

2. 修改配置文件 `src/main/resources/application.yml` 中的数据库连接信息

3. 使用Maven编译运行
```bash
mvn clean install
mvn spring-boot:run
```

或者直接运行主类 `CampusServiceApplication`

后端服务将在 `http://localhost:8080/api` 启动

### 前端启动

1. 进入前端目录
```bash
cd frontend
```

2. 安装依赖
```bash
npm install
```

3. 启动开发服务器
```bash
npm run dev
```

前端服务将在 `http://localhost:3000` 启动

## 默认账号

**所有用户密码统一为：123456**

管理员账号：
- 用户名：admin
- 学号：2024001
- 密码：123456

学生账号：
- 用户名：zhangsan / lisi / wangwu
- 学号：2024002 / 2024003 / 2024004
- 密码：123456

或直接使用注册功能创建新账号（密码自动设置为123456）

## 项目结构

```
校园迎新服务/
├── database.sql                    # 数据库脚本
├── backend/                        # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/campus/
│   │   │   │       ├── CampusServiceApplication.java
│   │   │   │       ├── common/
│   │   │   │       ├── config/
│   │   │   │       ├── controller/
│   │   │   │       ├── entity/
│   │   │   │       ├── mapper/
│   │   │   │       └── service/
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── pom.xml
│   └── ...
└── frontend/                       # 前端项目
    ├── src/
    │   ├── api/                   # API接口
    │   ├── layouts/               # 布局组件
    │   ├── router/                # 路由配置
    │   ├── utils/                 # 工具函数
    │   ├── views/                 # 页面组件
    │   ├── App.vue
    │   └── main.js
    ├── index.html
    ├── package.json
    └── vite.config.js
```

## API接口说明

### 用户相关
- POST `/api/user/register` - 用户注册
- POST `/api/user/login` - 用户登录
- GET `/api/user/info/{userId}` - 获取用户信息
- PUT `/api/user/update` - 更新用户信息
- POST `/api/user/changePassword` - 修改密码

### 活动相关
- GET `/api/activity/list` - 获取活动列表
- GET `/api/activity/recommend` - 获取推荐活动
- GET `/api/activity/detail/{id}` - 获取活动详情
- POST `/api/activity/create` - 创建活动
- PUT `/api/activity/update` - 更新活动
- DELETE `/api/activity/delete/{id}` - 删除活动

### 课表相关
- GET `/api/course/list/{userId}` - 获取课程列表
- POST `/api/course/add` - 添加课程
- PUT `/api/course/update` - 更新课程
- DELETE `/api/course/delete/{id}` - 删除课程

### 食堂相关
- GET `/api/canteen/list` - 获取食堂列表
- GET `/api/canteen/detail/{id}` - 获取食堂详情
- GET `/api/canteen/dishes/{canteenId}` - 获取菜品列表
- GET `/api/canteen/recommend` - 获取推荐菜品

### 社团相关
- GET `/api/club/list` - 获取社团列表
- GET `/api/club/detail/{id}` - 获取社团详情
- POST `/api/club/create` - 创建社团

### 求助相关
- GET `/api/help/list` - 获取帖子列表
- GET `/api/help/detail/{id}` - 获取帖子详情
- POST `/api/help/publish` - 发布帖子
- PUT `/api/help/update` - 更新帖子
- DELETE `/api/help/delete/{id}` - 删除帖子

## 注意事项

1. 确保MySQL数据库已启动并可访问
2. 首次运行需要执行database.sql脚本初始化数据
3. 前端代理配置在 `vite.config.js` 中，将 `/api` 请求转发到后端
4. JWT密钥配置在后端 `application.yml` 中

## 开发说明

### 后端开发规范
- 使用RESTful API设计规范
- 统一返回Result格式
- 使用JWT进行身份验证
- 密码使用BCrypt加密

### 前端开发规范
- 使用Vue3 Composition API
- 组件采用 `<script setup>` 语法
- 使用Element Plus UI组件库
- 路由守卫实现登录验证

## 许可证

MIT License
