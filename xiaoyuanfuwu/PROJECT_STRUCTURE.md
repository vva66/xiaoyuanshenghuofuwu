# 项目文件清单

## 完整的项目文件结构

```
校园迎新服务/
│
├── database.sql                              # 数据库SQL脚本（包含18张表和初始数据）
├── README.md                                 # 项目说明文档
├── start.bat                                 # Windows快速启动脚本
│
├── backend/                                  # Spring Boot后端项目
│   ├── pom.xml                              # Maven配置文件
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/campus/
│           │       ├── CampusServiceApplication.java      # 主启动类
│           │       ├── common/
│           │       │   ├── Result.java                    # 统一返回结果
│           │       │   └── PageResult.java                # 分页返回结果
│           │       ├── config/
│           │       │   ├── CorsConfig.java                # 跨域配置
│           │       │   ├── MybatisPlusConfig.java         # MyBatis Plus配置
│           │       │   └── JwtUtil.java                   # JWT工具类
│           │       ├── controller/
│           │       │   ├── UserController.java            # 用户控制器
│           │       │   ├── ActivityController.java        # 活动控制器
│           │       │   ├── CourseController.java          # 课表控制器
│           │       │   ├── CanteenController.java         # 食堂控制器
│           │       │   ├── ClubController.java            # 社团控制器
│           │       │   └── HelpPostController.java        # 求助帖子控制器
│           │       ├── entity/
│           │       │   ├── User.java                      # 用户实体
│           │       │   ├── Activity.java                  # 活动实体
│           │       │   ├── CourseSchedule.java            # 课表实体
│           │       │   ├── Canteen.java                   # 食堂实体
│           │       │   ├── CanteenDish.java               # 菜品实体
│           │       │   ├── Club.java                      # 社团实体
│           │       │   └── HelpPost.java                  # 求助帖子实体
│           │       ├── mapper/
│           │       │   ├── UserMapper.java                # 用户Mapper
│           │       │   ├── ActivityMapper.java            # 活动Mapper
│           │       │   ├── CourseScheduleMapper.java      # 课表Mapper
│           │       │   ├── CanteenMapper.java             # 食堂Mapper
│           │       │   ├── CanteenDishMapper.java         # 菜品Mapper
│           │       │   ├── ClubMapper.java                # 社团Mapper
│           │       │   └── HelpPostMapper.java            # 求助帖子Mapper
│           │       └── service/
│           │           ├── UserService.java               # 用户服务
│           │           ├── ActivityService.java           # 活动服务
│           │           ├── CourseScheduleService.java     # 课表服务
│           │           ├── CanteenService.java            # 食堂服务
│           │           ├── ClubService.java               # 社团服务
│           │           └── HelpPostService.java           # 求助帖子服务
│           └── resources/
│               └── application.yml                        # Spring Boot配置文件
│
└── frontend/                                 # Vue3前端项目
    ├── index.html                            # HTML入口文件
    ├── package.json                          # NPM依赖配置
    ├── vite.config.js                        # Vite配置文件
    └── src/
        ├── main.js                           # 主入口文件
        ├── App.vue                           # 根组件
        ├── api/                              # API接口文件
        │   ├── user.js                       # 用户API
        │   ├── activity.js                   # 活动API
        │   ├── course.js                     # 课表API
        │   ├── canteen.js                    # 食堂API
        │   ├── club.js                       # 社团API
        │   └── help.js                       # 求助API
        ├── router/
        │   └── index.js                      # 路由配置
        ├── utils/
        │   └── request.js                    # Axios请求封装
        ├── layouts/
        │   └── MainLayout.vue                # 主布局组件
        └── views/                            # 页面组件
            ├── Login.vue                     # 登录页面
            ├── Register.vue                  # 注册页面
            ├── Home.vue                      # 首页
            ├── Profile.vue                   # 个人中心
            ├── freshman/
            │   └── Guide.vue                 # 迎新指南
            ├── activity/
            │   ├── List.vue                  # 活动列表
            │   └── Detail.vue                # 活动详情
            ├── course/
            │   └── Schedule.vue              # 课表查询
            ├── canteen/
            │   └── List.vue                  # 食堂列表
            ├── club/
            │   └── List.vue                  # 社团列表
            └── help/
                ├── List.vue                  # 互助广场列表
                └── Publish.vue               # 发布求助
```

## 数据库表说明

### 系统管理表
1. **sys_user** - 用户基础信息表
2. **sys_role** - 角色表
3. **sys_user_role** - 用户角色关联表
4. **sys_menu** - 菜单权限表
5. **sys_role_menu** - 角色菜单关联表

### 业务功能表
6. **activity** - 校园活动表
7. **activity_registration** - 活动报名表
8. **course_schedule** - 课表数据表
9. **canteen** - 食堂信息表
10. **canteen_dish** - 食堂菜品表
11. **canteen_review** - 菜品评价表
12. **club** - 社团信息表
13. **club_member** - 社团成员表
14. **help_post** - 求助/活动帖子表
15. **comment** - 评论表
16. **message** - 消息通知表
17. **campus_poi** - 校园地图POI点表
18. **freshman_guide** - 迎新指南内容表

## 核心功能实现

### 后端实现
- ✅ RESTful API设计
- ✅ JWT身份认证
- ✅ MyBatis Plus数据访问层
- ✅ 统一异常处理
- ✅ 分页查询支持
- ✅ BCrypt密码加密
- ✅ 跨域配置

### 前端实现
- ✅ Vue3 Composition API
- ✅ Vue Router路由管理
- ✅ Element Plus UI组件库
- ✅ Axios请求拦截器
- ✅ 路由守卫（登录验证）
- ✅ 响应式布局
- ✅ 组件化开发

## 已完成的页面

### 认证相关
- ✅ 登录页面
- ✅ 注册页面

### 主要功能页面
- ✅ 首页（展示推荐活动）
- ✅ 迎新指南页面（包含报到流程、宿舍查询、缴费指引等）
- ✅ 活动列表页面
- ✅ 活动详情页面
- ✅ 课表查询页面（支持周视图/日视图）
- ✅ 食堂推荐页面
- ✅ 社团列表页面
- ✅ 互助广场列表页面
- ✅ 求助发布页面
- ✅ 个人中心页面

## 启动说明

### 方式一：使用快速启动脚本
双击运行 `start.bat` 文件

### 方式二：手动启动

#### 1. 初始化数据库
```bash
mysql -u root -p < database.sql
```

#### 2. 启动后端
```bash
cd backend
mvn spring-boot:run
```

#### 3. 启动前端
```bash
cd frontend
npm install
npm run dev
```

## 访问地址
- 前端：http://localhost:3000
- 后端：http://localhost:8080/api

## 注意事项
1. 确保已安装Node.js和Java 17+
2. 确保MySQL数据库已启动
3. 首次运行需要执行database.sql初始化数据
4. 可以根据实际需求修改application.yml中的配置
