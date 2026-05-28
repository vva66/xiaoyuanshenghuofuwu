# 校园服务系统 - 快速开始指南

## 🚀 5分钟快速启动

### 前置要求
- ✅ Java 17 或更高版本
- ✅ Node.js 16+ 和 npm
- ✅ MySQL 8.0 数据库

### 步骤1：初始化数据库

打开MySQL命令行或可视化工具（如Navicat），执行：

```sql
-- 方式一：使用命令行
mysql -u root -p < database.sql

-- 方式二：在MySQL客户端中直接运行database.sql文件内容
```

### 步骤2：配置后端

1. 进入后端目录：`cd backend`
2. 编辑配置文件：`src/main/resources/application.yml`
3. 修改数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_service?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root        # 修改为你的数据库用户名
    password: root        # 修改为你的数据库密码
```

### 步骤3：启动后端服务

```bash
cd backend
mvn spring-boot:run
```

看到以下提示表示启动成功：
```
====================================
校园服务系统启动成功！
访问地址: http://localhost:8080/api
====================================
```

### 步骤4：启动前端服务

打开新的命令行窗口：

```bash
cd frontend
npm install
npm run dev
```

看到以下提示表示启动成功：
```
VITE v5.x.x ready in xxx ms
➜  Local:   http://localhost:3000/
```

### 步骤5：访问系统

浏览器打开：http://localhost:3000

## 📱 功能演示

### 1. 用户注册与登录

**注册流程：**
1. 点击"立即注册"
2. 填写用户名、学号、密码等信息
3. 点击"注册"按钮

**登录流程：**
1. 输入用户名和密码
2. 点击"登录"按钮
3. 自动跳转到首页

### 2. 迎新指南

- **报到流程**：查看分步骤的报到指引
- **宿舍查询**：输入学号查询宿舍分配结果
- **缴费指引**：查看缴费项目和方式

### 3. 校园活动

- **浏览活动**：在"校园活动"页面查看所有活动
- **活动详情**：点击活动卡片查看详情
- **活动报名**：在活动详情页点击"立即报名"

### 4. 课表查询

- **周视图**：以表格形式展示一周的课程
- **日视图**：以时间轴形式展示某天的课程
- **添加课程**：点击"添加课程"按钮手动录入

### 5. 食堂推荐

- **查看食堂**：浏览所有食堂及评分
- **查看菜品**：点击食堂查看推荐菜品
- **搜索菜品**：使用搜索功能查找特定菜品

### 6. 社团活动

- **浏览社团**：查看所有社团信息
- **加入社团**：点击"加入社团"按钮申请加入

### 7. 互助广场

- **浏览帖子**：查看各类求助和 activity 帖子
- **发布帖子**：点击"发布求助"按钮发布新帖子
- **分类筛选**：按分类筛选帖子

## 🎯 常见问题

### Q1: 启动后端时出现"找不到或无法加载主类"？
**解决方案：**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Q2: 前端启动时报错"Cannot find module"？
**解决方案：**
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
npm run dev
```

### Q3: 登录后无法访问其他页面？
**解决方案：**
- 检查浏览器控制台是否有错误
- 确认后端服务正常运行
- 清除浏览器缓存后重试

### Q4: 数据库连接失败？
**解决方案：**
- 确认MySQL服务已启动
- 检查application.yml中的数据库用户名和密码
- 确认数据库campus_service已创建

## 🛠️ 开发工具推荐

### 后端开发
- **IDEA** - IntelliJ IDEA（推荐）
- **Eclipse** - Eclipse IDE for Java Developers
- **数据库工具** - Navicat Premium / MySQL Workbench

### 前端开发
- **VS Code** - Visual Studio Code（推荐）
- **WebStorm** - JetBrains WebStorm
- **浏览器** - Chrome / Edge（推荐）

## 📊 技术栈说明

### 后端技术
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.0 | Web框架 |
| MyBatis Plus | 3.5.5 | ORM框架 |
| JWT | 0.12.3 | Token认证 |
| MySQL | 8.0 | 数据库 |
| Lombok | - | 简化代码 |

### 前端技术
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4.0 | 渐进式框架 |
| Vite | 5.0 | 构建工具 |
| Element Plus | 2.5.0 | UI组件库 |
| Vue Router | 4.2.5 | 路由管理 |
| Pinia | 2.1.7 | 状态管理 |
| Axios | 1.6.2 | HTTP客户端 |

## 📝 下一步操作

1. **完善个人信息**
   - 进入"个人中心"
   - 修改头像、手机号、邮箱等

2. **体验各个功能模块**
   - 迎新指南
   - 校园活动
   - 课表查询
   - 食堂推荐
   - 社团活动
   - 互助广场

3. **自定义配置**
   - 修改食堂数据
   - 添加社团信息
   - 发布校园活动

## 💡 使用技巧

1. **快捷键**
   - `Ctrl + R` - 刷新页面
   - `Ctrl + F5` - 强制刷新（清除缓存）

2. **数据测试**
   - 可以使用SQL脚本中的示例数据
   - 也可以自己注册新账号测试

3. **移动端适配**
   - 系统采用响应式设计
   - 可在手机浏览器中访问

## 📞 技术支持

如遇到问题，请检查：
1. README.md - 项目说明文档
2. PROJECT_STRUCTURE.md - 项目结构文档
3. 浏览器控制台 - 查看前端错误
4. 后端日志 - 查看Java异常信息

---

**祝您使用愉快！** 🎉
