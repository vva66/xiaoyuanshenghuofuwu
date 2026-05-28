-- =============================================
-- 校园迎新服务系统 - 统一测试数据
-- 生成时间: 2026-05-19
-- 说明: 本文件合并了所有模块的测试数据，包含完整的数据库结构和测试数据
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE campus_service;

-- =============================================
-- 1. 用户基础信息表
-- =============================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    student_no VARCHAR(20) UNIQUE NOT NULL COMMENT '学号',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    gender TINYINT COMMENT '性别 0-女 1-男',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    college VARCHAR(100) COMMENT '学院',
    major VARCHAR(100) COMMENT '专业',
    class_name VARCHAR(50) COMMENT '班级',
    enrollment_year INT COMMENT '入学年份',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_student_no (student_no),
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户基础信息表';

-- 插入用户数据（密码均为123456）
INSERT INTO sys_user (student_no, username, password, real_name, gender, phone, email, college, major, class_name, enrollment_year) VALUES
('2024001', 'admin', '123456', '系统管理员', 1, '13800138000', 'admin@campus.edu.cn', '计算机学院', '计算机科学与技术', '2024级1班', 2024),
('2024002', 'zhangsan', '123456', '张三', 1, '13800138001', 'zhangsan@campus.edu.cn', '计算机学院', '软件工程', '2024级2班', 2024),
('2024003', 'lisi', '123456', '李四', 0, '13800138002', 'lisi@campus.edu.cn', '文学院', '汉语言文学', '2024级1班', 2024),
('2024004', 'wangwu', '123456', '王五', 1, '13800138003', 'wangwu@campus.edu.cn', '理学院', '数学与应用数学', '2024级3班', 2024),
('2023001001', 'qinghe', '123456', '沈清和', 1, '13800138004', 'qinghe@campus.edu', '信息工程学院', '计算机科学与技术', '2023级计科1班', 2023),
('2023001002', 'muyu', '123456', '苏沐宇', 1, '13800138005', 'muyu@campus.edu', '信息工程学院', '计算机科学与技术', '2023级计科1班', 2023),
('2023001003', 'wanxi', '123456', '林晚溪', 0, '13800138006', 'wanxi@campus.edu', '信息工程学院', '软件工程', '2023级软工2班', 2023),
('2023002001', 'chenyu', '123456', '江辰屿', 1, '13800138007', 'chenyu@campus.edu', '机械工程学院', '机械设计制造', '2023级机设1班', 2023),
('2023003001', 'shuyue', '123456', '顾书月', 0, '13800138008', 'shuyue@campus.edu', '外国语学院', '英语', '2023级英语1班', 2023),
('2022001001', 'jingran', '123456', '陆景然', 1, '13800138009', 'jingran@campus.edu', '信息工程学院', '计算机科学与技术', '2022级计科2班', 2022),
('2022002001', 'zhinian', '123456', '许知年', 0, '13800138010', 'zhinian@campus.edu', '商学院', '会计学', '2022级会计1班', 2022),
('2023001008', 'shuhang', '123456', '温书珩', 1, '13800138011', 'shuhang@campus.edu', '信息工程学院', '计算机科学与技术', '2023级计科1班', 2023);

-- =============================================
-- 2. 角色表
-- =============================================
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) UNIQUE NOT NULL COMMENT '角色编码',
    description VARCHAR(255) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

INSERT INTO sys_role (role_name, role_code, description) VALUES
('学生', 'STUDENT', '普通学生角色'),
('社团负责人', 'CLUB_LEADER', '社团负责人角色'),
('管理员', 'ADMIN', '系统管理员角色');

-- =============================================
-- 3. 用户角色关联表
-- =============================================
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 3),  -- admin -> 管理员
(2, 1),  -- zhangsan -> 学生
(3, 1),  -- lisi -> 学生
(4, 1),  -- wangwu -> 学生
(5, 1),  -- qinghe -> 学生
(6, 1),  -- muyu -> 学生
(7, 1),  -- wanxi -> 学生
(8, 1),  -- chenyu -> 学生
(9, 1),  -- shuyue -> 学生
(10, 1), (10, 2),  -- jingran -> 学生+社团负责人
(11, 1), (11, 2),  -- zhinian -> 学生+社团负责人
(12, 3); -- shuhang -> 管理员

-- =============================================
-- 4. 菜单权限表
-- =============================================
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    menu_type TINYINT COMMENT '菜单类型 1-目录 2-菜单 3-按钮',
    path VARCHAR(200) COMMENT '路由路径',
    component VARCHAR(200) COMMENT '组件路径',
    perms VARCHAR(100) COMMENT '权限标识',
    icon VARCHAR(100) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    visible TINYINT DEFAULT 1 COMMENT '是否显示 0-隐藏 1-显示',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

INSERT INTO sys_menu (parent_id, menu_name, menu_type, path, component, perms, icon, sort_order) VALUES
(0, '系统管理', 1, '/system', NULL, NULL, 'el-icon-setting', 1),
(1, '用户管理', 2, '/system/user', 'system/user/index', 'system:user:list', 'el-icon-user', 1),
(1, '角色管理', 2, '/system/role', 'system/role/index', 'system:role:list', 'el-icon-s-custom', 2),
(1, '菜单管理', 2, '/system/menu', 'system/menu/index', 'system:menu:list', 'el-icon-menu', 3),
(0, '迎新服务', 1, '/freshman', NULL, NULL, 'el-icon-school', 2),
(5, '迎新指南', 2, '/freshman/guide', 'freshman/guide/index', 'freshman:guide:list', 'el-icon-document', 1),
(5, '报到流程', 2, '/freshman/process', 'freshman/process/index', 'freshman:process:list', 'el-icon-s-order', 2),
(0, '校园活动', 1, '/activity', NULL, NULL, 'el-icon-celebration', 3),
(8, '活动列表', 2, '/activity/list', 'activity/list/index', 'activity:list', 'el-icon-s-management', 1),
(8, '活动发布', 2, '/activity/publish', 'activity/publish/index', 'activity:publish', 'el-icon-plus', 2),
(0, '课表查询', 1, '/course', NULL, NULL, 'el-icon-calendar', 4),
(11, '我的课表', 2, '/course/schedule', 'course/schedule/index', 'course:schedule', 'el-icon-date', 1),
(0, '食堂推荐', 1, '/canteen', NULL, NULL, 'el-icon-food', 5),
(13, '食堂列表', 2, '/canteen/list', 'canteen/list/index', 'canteen:list', 'el-icon-menu', 1),
(0, '校园导航', 1, '/navigation', NULL, NULL, 'el-icon-map-location', 6),
(15, '地图导航', 2, '/navigation/map', 'navigation/map/index', 'navigation:map', 'el-icon-location', 1),
(0, '社团活动', 1, '/club', NULL, NULL, 'el-icon-flag', 7),
(17, '社团列表', 2, '/club/list', 'club/list/index', 'club:list', 'el-icon-s-home', 1),
(0, '互助广场', 1, '/help', NULL, NULL, 'el-icon-chat-dot-round', 8),
(19, '求助列表', 2, '/help/list', 'help/list/index', 'help:list', 'el-icon-s-comment', 1);

-- =============================================
-- 5. 角色菜单关联表
-- =============================================
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    INDEX idx_role_id (role_id),
    INDEX idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 为管理员角色分配所有菜单权限
INSERT INTO sys_role_menu (role_id, menu_id) 
SELECT 3, id FROM sys_menu;

-- =============================================
-- 6. 校园活动表
-- =============================================
DROP TABLE IF EXISTS activity;
CREATE TABLE activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '活动ID',
    title VARCHAR(200) NOT NULL COMMENT '活动标题',
    cover_image VARCHAR(255) COMMENT '封面图片',
    category VARCHAR(50) COMMENT '活动分类 学术讲座/文体活动/志愿服务/其他',
    content TEXT COMMENT '活动内容',
    location VARCHAR(200) COMMENT '活动地点',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    max_participants INT COMMENT '最大参与人数',
    current_participants INT DEFAULT 0 COMMENT '当前参与人数',
    publisher_id BIGINT NOT NULL COMMENT '发布者ID',
    publisher_name VARCHAR(50) COMMENT '发布者姓名',
    status TINYINT DEFAULT 0 COMMENT '状态 0-报名中 1-进行中 2-已结束 3-已取消',
    is_recommend TINYINT DEFAULT 0 COMMENT '是否推荐 0-否 1-是',
    tags VARCHAR(255) COMMENT '标签(逗号分隔)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='校园活动表';

INSERT INTO activity (title, cover_image, category, content, location, start_time, end_time, max_participants, current_participants, publisher_id, publisher_name, status, is_recommend, tags) VALUES
('Python编程入门讲座', 'https://example.com/cover/python.jpg', '学术讲座', '本次讲座将从Python基础语法开始，讲解变量、循环、函数等核心概念，适合零基础同学参加。现场提供上机练习，还有精美礼品赠送。', '教学楼A101', '2026-05-20 19:00:00', '2026-05-20 21:00:00', 120, 86, 10, '陆景然', 0, 1, 'Python,编程,入门,讲座'),
('校园篮球联赛', 'https://example.com/cover/basketball.jpg', '文体活动', '一年一度的校园篮球联赛即将开赛！各学院代表队将展开激烈角逐，冠军队伍将获得奖杯和奖金。欢迎同学们到场观赛加油。', '学校体育馆', '2026-05-25 14:00:00', '2026-06-10 18:00:00', 200, 150, 6, '苏沐宇', 0, 1, '篮球,联赛,体育,比赛'),
('敬老院志愿服务活动', 'https://example.com/cover/volunteer.jpg', '志愿服务', '志愿者协会将组织前往阳光敬老院开展志愿服务活动，内容包括陪伴老人聊天、打扫卫生、表演节目等。', '阳光敬老院', '2026-05-18 08:30:00', '2026-05-18 12:00:00', 30, 28, 11, '许知年', 0, 0, '志愿服务,敬老院,爱心'),
('毕业季摄影展', 'https://example.com/cover/photo.jpg', '文体活动', '记录青春，定格美好。本次摄影展将展出同学们拍摄的校园风光、毕业瞬间等作品，欢迎大家前来参观。', '图书馆一楼展厅', '2026-06-01 09:00:00', '2026-06-07 17:00:00', 500, 0, 9, '顾书月', 0, 0, '摄影,毕业季,展览'),
('考研经验分享会', 'https://example.com/cover/kaoyan.jpg', '学术讲座', '邀请2026届考研成功的学长学姐分享备考经验、复习方法和院校选择技巧，帮助准备考研的同学少走弯路。', '教学楼B202', '2026-05-22 19:00:00', '2026-05-22 21:30:00', 150, 120, 7, '林晚溪', 0, 1, '考研,经验分享,升学'),
('校园歌手大赛', 'https://example.com/cover/singer.jpg', '文体活动', '唱出青春，放飞梦想！校园歌手大赛报名开始啦！无论你是专业歌手还是业余爱好者，都可以报名参加，展示你的歌喉。', '大学生活动中心', '2026-05-30 18:00:00', '2026-05-30 22:00:00', 1000, 350, 8, '江辰屿', 0, 1, '歌手大赛,音乐,才艺');

-- =============================================
-- 7. 活动报名表
-- =============================================
DROP TABLE IF EXISTS activity_registration;
CREATE TABLE activity_registration (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '报名ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户姓名',
    student_no VARCHAR(20) COMMENT '学号',
    contact_info VARCHAR(100) COMMENT '联系方式',
    status TINYINT DEFAULT 0 COMMENT '状态 0-已报名 1-已签到 2-已取消',
    check_in_time DATETIME COMMENT '签到时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_activity_user (activity_id, user_id),
    INDEX idx_activity_id (activity_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名表';

INSERT INTO activity_registration (activity_id, user_id, user_name, student_no, contact_info, status, check_in_time) VALUES
(1, 6, '苏沐宇', '2023001002', '13800138005', 0, NULL),
(1, 7, '林晚溪', '2023001003', '13800138006', 0, NULL),
(1, 8, '江辰屿', '2023002001', '13800138007', 0, NULL),
(1, 9, '顾书月', '2023003001', '13800138008', 0, NULL),
(2, 6, '苏沐宇', '2023001002', '13800138005', 0, NULL),
(2, 8, '江辰屿', '2023002001', '13800138007', 0, NULL),
(3, 6, '苏沐宇', '2023001002', '13800138005', 0, NULL),
(3, 7, '林晚溪', '2023001003', '13800138006', 0, NULL),
(3, 9, '顾书月', '2023003001', '13800138008', 0, NULL),
(5, 6, '苏沐宇', '2023001002', '13800138005', 0, NULL),
(5, 7, '林晚溪', '2023001003', '13800138006', 0, NULL),
(5, 10, '陆景然', '2022001001', '13800138009', 0, NULL),
(6, 7, '林晚溪', '2023001003', '13800138006', 0, NULL),
(6, 9, '顾书月', '2023003001', '13800138008', 0, NULL);

-- =============================================
-- 8. 课表数据表
-- =============================================
DROP TABLE IF EXISTS course_schedule;
CREATE TABLE course_schedule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    teacher VARCHAR(50) COMMENT '教师',
    classroom VARCHAR(100) COMMENT '教室',
    day_of_week TINYINT NOT NULL COMMENT '星期几 1-7',
    start_section TINYINT NOT NULL COMMENT '开始节次',
    end_section TINYINT NOT NULL COMMENT '结束节次',
    start_time TIME COMMENT '上课时间',
    end_time TIME COMMENT '下课时间',
    week_start INT COMMENT '开始周',
    week_end INT COMMENT '结束周',
    semester VARCHAR(50) COMMENT '学期',
    academic_year VARCHAR(20) COMMENT '学年',
    color VARCHAR(20) COMMENT '显示颜色',
    status TINYINT DEFAULT 1 COMMENT '状态 0-停用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_day_of_week (day_of_week),
    INDEX idx_semester (semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课表数据表';

INSERT INTO course_schedule (user_id, course_name, teacher, classroom, day_of_week, start_section, end_section, start_time, end_time, week_start, week_end, semester, academic_year, color) VALUES
(6, '高等数学', '李教授', '教学楼A301', 1, 1, 2, '08:00:00', '09:40:00', 1, 16, '2025-2026学年第二学期', '2025-2026', '#409EFF'),
(6, '大学英语', '王老师', '教学楼B203', 1, 3, 4, '10:00:00', '11:40:00', 1, 16, '2025-2026学年第二学期', '2025-2026', '#67C23A'),
(6, '计算机组成原理', '张教授', '教学楼C102', 1, 5, 6, '14:00:00', '15:40:00', 1, 16, '2025-2026学年第二学期', '2025-2026', '#E6A23C'),
(6, '数据结构', '刘老师', '教学楼A205', 2, 1, 2, '08:00:00', '09:40:00', 1, 16, '2025-2026学年第二学期', '2025-2026', '#F56C6C'),
(6, 'Java程序设计', '陈老师', '实验楼302', 2, 3, 4, '10:00:00', '11:40:00', 1, 16, '2025-2026学年第二学期', '2025-2026', '#909399'),
(6, '线性代数', '赵教授', '教学楼A302', 3, 1, 2, '08:00:00', '09:40:00', 1, 16, '2025-2026学年第二学期', '2025-2026', '#409EFF'),
(6, '大学物理', '孙老师', '教学楼B101', 3, 5, 6, '14:00:00', '15:40:00', 1, 16, '2025-2026学年第二学期', '2025-2026', '#67C23A'),
(6, '计算机组成原理实验', '张教授', '实验楼201', 4, 3, 4, '10:00:00', '11:40:00', 1, 16, '2025-2026学年第二学期', '2025-2026', '#E6A23C'),
(6, '数据结构实验', '刘老师', '实验楼301', 5, 1, 2, '08:00:00', '09:40:00', 1, 16, '2025-2026学年第二学期', '2025-2026', '#F56C6C'),
(7, '软件工程', '周教授', '教学楼A401', 1, 1, 2, '08:00:00', '09:40:00', 1, 16, '2025-2026学年第二学期', '2025-2026', '#409EFF'),
(7, '大学英语', '王老师', '教学楼B204', 1, 3, 4, '10:00:00', '11:40:00', 1, 16, '2025-2026学年第二学期', '2025-2026', '#67C23A'),
(7, '数据库原理', '吴老师', '教学楼C201', 2, 1, 2, '08:00:00', '09:40:00', 1, 16, '2025-2026学年第二学期', '2025-2026', '#E6A23C');

-- =============================================
-- 9. 食堂信息表
-- =============================================
DROP TABLE IF EXISTS canteen;
CREATE TABLE canteen (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '食堂ID',
    name VARCHAR(100) NOT NULL COMMENT '食堂名称',
    address VARCHAR(200) COMMENT '地址',
    opening_hours VARCHAR(200) COMMENT '营业时间',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    image VARCHAR(255) COMMENT '图片',
    description TEXT COMMENT '描述',
    latitude DECIMAL(10, 6) COMMENT '纬度',
    longitude DECIMAL(10, 6) COMMENT '经度',
    rating DECIMAL(3, 2) DEFAULT 0.00 COMMENT '评分',
    status TINYINT DEFAULT 1 COMMENT '状态 0-停用 1-正常',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食堂信息表';

INSERT INTO canteen (name, address, opening_hours, contact_phone, description, latitude, longitude, rating) VALUES
('第一食堂', '校园东区', '06:30-21:00', '010-12345678', '提供各类中餐和小吃，环境优美', 39.9042, 116.4074, 4.5),
('第二食堂', '校园西区', '06:30-21:00', '010-12345679', '特色窗口众多，口味丰富', 39.9052, 116.4084, 4.3),
('清真食堂', '校园南区', '07:00-20:00', '010-12345680', '清真食品，健康卫生', 39.9032, 116.4064, 4.6);

-- =============================================
-- 10. 食堂菜品表
-- =============================================
DROP TABLE IF EXISTS canteen_dish;
CREATE TABLE canteen_dish (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜品ID',
    canteen_id BIGINT NOT NULL COMMENT '食堂ID',
    window_name VARCHAR(100) COMMENT '窗口名称',
    dish_name VARCHAR(100) NOT NULL COMMENT '菜品名称',
    image VARCHAR(255) COMMENT '图片',
    price DECIMAL(10, 2) COMMENT '价格',
    description TEXT COMMENT '描述',
    rating DECIMAL(3, 2) DEFAULT 0.00 COMMENT '评分',
    review_count INT DEFAULT 0 COMMENT '评价数量',
    sales_count INT DEFAULT 0 COMMENT '销量',
    is_recommend TINYINT DEFAULT 0 COMMENT '是否推荐 0-否 1-是',
    status TINYINT DEFAULT 1 COMMENT '状态 0-下架 1-上架',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_canteen_id (canteen_id),
    INDEX idx_is_recommend (is_recommend)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食堂菜品表';

INSERT INTO canteen_dish (canteen_id, window_name, dish_name, image, price, description, rating, review_count, sales_count, is_recommend) VALUES
(1, '早餐窗口', '豆浆油条', 'https://example.com/dish/doujiangyoutiao.jpg', 3.50, '现磨豆浆配酥脆油条，经典早餐组合', 4.6, 120, 850, 1),
(1, '早餐窗口', '包子套餐', 'https://example.com/dish/baozi.jpg', 5.00, '两个肉包+一个菜包+小米粥', 4.4, 98, 620, 0),
(1, '中餐窗口1', '番茄炒蛋盖饭', 'https://example.com/dish/fanqiechaodan.jpg', 12.00, '酸甜可口，营养丰富', 4.5, 156, 1200, 1),
(1, '中餐窗口1', '宫保鸡丁盖饭', 'https://example.com/dish/gongbaojiding.jpg', 15.00, '经典川菜，鸡肉嫩滑', 4.7, 189, 1500, 1),
(1, '中餐窗口2', '红烧肉套餐', 'https://example.com/dish/hongshaorou.jpg', 18.00, '肥而不腻，入口即化', 4.8, 210, 980, 1),
(1, '晚餐窗口', '麻辣烫', 'https://example.com/dish/malatang.jpg', 15.00, '自选菜品，汤底浓郁', 4.3, 167, 1100, 0),
(2, '特色面食', '兰州拉面', 'https://example.com/dish/lamian.jpg', 10.00, '手工拉面，汤鲜味美', 4.5, 134, 950, 1),
(2, '特色面食', '刀削面', 'https://example.com/dish/daoxiaomian.jpg', 11.00, '劲道爽滑，浇头丰富', 4.4, 112, 780, 0),
(2, '川味窗口', '麻婆豆腐', 'https://example.com/dish/mapodoufu.jpg', 10.00, '麻辣鲜香，下饭神器', 4.6, 145, 1050, 1),
(2, '川味窗口', '水煮肉片', 'https://example.com/dish/shuizhuroupian.jpg', 16.00, '肉片滑嫩，麻辣过瘾', 4.7, 178, 890, 1),
(2, '快餐窗口', '炸鸡套餐', 'https://example.com/dish/zhaji.jpg', 20.00, '外酥里嫩，配薯条可乐', 4.2, 96, 650, 0),
(3, '清真窗口', '兰州牛肉拉面', 'https://example.com/dish/qingzhenlamian.jpg', 12.00, '正宗清真拉面，牛肉分量足', 4.8, 205, 1300, 1),
(3, '清真窗口', '大盘鸡', 'https://example.com/dish/dapanji.jpg', 38.00, '两人份，配皮带面', 4.9, 187, 520, 1),
(3, '清真窗口', '手抓饭', 'https://example.com/dish/shouzhua.jpg', 15.00, '新疆特色，香气扑鼻', 4.6, 123, 760, 0),
(3, '清真窗口', '烤羊肉串', 'https://example.com/dish/yangrouchuan.jpg', 5.00, '现烤现卖，外焦里嫩', 4.7, 156, 2100, 1);

-- =============================================
-- 11. 菜品评价表
-- =============================================
DROP TABLE IF EXISTS canteen_review;
CREATE TABLE canteen_review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    dish_id BIGINT NOT NULL COMMENT '菜品ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户姓名',
    rating TINYINT NOT NULL COMMENT '评分 1-5',
    content TEXT COMMENT '评价内容',
    images VARCHAR(500) COMMENT '图片(逗号分隔)',
    status TINYINT DEFAULT 1 COMMENT '状态 0-待审核 1-已通过 2-已拒绝',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_dish_id (dish_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品评价表';

INSERT INTO canteen_review (dish_id, user_id, user_name, rating, content, images, status) VALUES
(3, 6, '苏沐宇', 5, '番茄炒蛋超级好吃，酸甜度刚刚好，米饭也很香，强烈推荐！', 'https://example.com/review/fanqie1.jpg,https://example.com/review/fanqie2.jpg', 1),
(3, 7, '林晚溪', 4, '味道还不错，就是鸡蛋有点少，希望下次能多放一点。', '', 1),
(4, 8, '江辰屿', 5, '宫保鸡丁yyds！每次来第一食堂必点，鸡肉很嫩，花生也脆。', 'https://example.com/review/gongbao1.jpg', 1),
(5, 9, '顾书月', 5, '红烧肉绝了！肥而不腻，入口即化，配米饭能吃两碗。', 'https://example.com/review/hongshao1.jpg', 1),
(7, 10, '陆景然', 4, '兰州拉面味道很正宗，就是排队的人太多了，每次都要等好久。', '', 1),
(9, 11, '许知年', 5, '麻婆豆腐太下饭了！麻辣鲜香，价格也实惠，性价比超高。', 'https://example.com/review/mapo1.jpg', 1),
(12, 6, '苏沐宇', 5, '清真食堂的牛肉拉面真的好吃，牛肉给的很多，汤也很鲜。', '', 1),
(13, 7, '林晚溪', 5, '大盘鸡分量超足，两个人吃不完，味道也很正宗，推荐！', 'https://example.com/review/dapanji1.jpg,https://example.com/review/dapanji2.jpg', 1),
(15, 8, '江辰屿', 4, '羊肉串味道不错，就是有点小贵，偶尔吃一次还可以。', '', 1);

-- =============================================
-- 12. 社团信息表
-- =============================================
DROP TABLE IF EXISTS club;
CREATE TABLE club (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '社团ID',
    name VARCHAR(100) NOT NULL COMMENT '社团名称',
    logo VARCHAR(255) COMMENT 'Logo',
    category VARCHAR(50) COMMENT '分类 学术类/体育类/艺术类/公益类/其他',
    description TEXT COMMENT '社团简介',
    established_date DATE COMMENT '成立日期',
    president_id BIGINT COMMENT '社长ID',
    president_name VARCHAR(50) COMMENT '社长姓名',
    member_count INT DEFAULT 0 COMMENT '成员数量',
    contact_info VARCHAR(200) COMMENT '联系方式',
    status TINYINT DEFAULT 0 COMMENT '状态 0-待审核 1-正常 2-已解散',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社团信息表';

INSERT INTO club (name, category, description, established_date, president_id, president_name, member_count, status) VALUES
('计算机协会', '学术类', '致力于计算机技术学习和交流', '2020-09-01', 10, '陆景然', 150, 1),
('篮球社', '体育类', '热爱篮球运动的同学们的组织', '2019-05-15', 8, '江辰屿', 80, 1),
('摄影协会', '艺术类', '记录美好瞬间，分享摄影技巧', '2021-03-20', 9, '顾书月', 60, 1),
('志愿者协会', '公益类', '奉献爱心，服务社会', '2018-10-10', 11, '许知年', 200, 1);

-- =============================================
-- 13. 社团成员表
-- =============================================
DROP TABLE IF EXISTS club_member;
CREATE TABLE club_member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '成员ID',
    club_id BIGINT NOT NULL COMMENT '社团ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户姓名',
    student_no VARCHAR(20) COMMENT '学号',
    role VARCHAR(50) COMMENT '角色 社长/副社长/部长/干事/成员',
    join_date DATE COMMENT '加入日期',
    status TINYINT DEFAULT 1 COMMENT '状态 0-已退出 1-在任',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_club_user (club_id, user_id),
    INDEX idx_club_id (club_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社团成员表';

INSERT INTO club_member (club_id, user_id, user_name, student_no, role, join_date, status) VALUES
(1, 10, '陆景然', '2022001001', '社长', '2022-09-15', 1),
(1, 6, '苏沐宇', '2023001002', '技术部部长', '2023-09-20', 1),
(1, 7, '林晚溪', '2023001003', '干事', '2023-09-20', 1),
(1, 8, '江辰屿', '2023002001', '成员', '2023-09-21', 1),
(2, 8, '江辰屿', '2023002001', '社长', '2022-09-10', 1),
(2, 6, '苏沐宇', '2023001002', '副社长', '2023-09-15', 1),
(2, 10, '陆景然', '2022001001', '成员', '2022-09-10', 1),
(3, 9, '顾书月', '2023003001', '社长', '2023-09-18', 1),
(3, 11, '许知年', '2022002001', '成员', '2023-09-20', 1),
(4, 11, '许知年', '2022002001', '社长', '2022-09-05', 1),
(4, 7, '林晚溪', '2023001003', '外联部部长', '2023-09-10', 1),
(4, 9, '顾书月', '2023003001', '成员', '2023-09-12', 1);

-- =============================================
-- 14. 社团活动表
-- =============================================
DROP TABLE IF EXISTS club_activity;
CREATE TABLE club_activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '活动ID',
    club_id BIGINT NOT NULL COMMENT '社团ID',
    club_name VARCHAR(100) COMMENT '社团名称',
    title VARCHAR(200) NOT NULL COMMENT '活动标题',
    description TEXT COMMENT '活动简介',
    location VARCHAR(200) COMMENT '活动地点',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    max_participants INT DEFAULT 100 COMMENT '最大参与人数',
    current_participants INT DEFAULT 0 COMMENT '当前参与人数',
    cover_image VARCHAR(255) COMMENT '封面图片',
    status TINYINT DEFAULT 1 COMMENT '状态 0-已取消 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_club_id (club_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社团活动表';

INSERT INTO club_activity (club_id, club_name, title, description, location, start_time, end_time, max_participants, current_participants, status) VALUES
(1, '计算机协会', 'Python编程入门讲座', '从零开始学习Python编程语言，适合编程零基础的同学', '教学楼A栋301室', '2024-10-20 14:00:00', '2024-10-20 16:00:00', 50, 35, 1),
(1, '计算机协会', '算法竞赛培训', '备战ACM程序设计竞赛，讲解经典算法和解题技巧', '图书馆报告厅', '2024-10-25 10:00:00', '2024-10-25 12:00:00', 30, 28, 1),
(1, '计算机协会', 'AI技术分享会', '探讨人工智能最新发展趋势和应用案例', '科技楼会议室', '2024-11-01 19:00:00', '2024-11-01 21:00:00', 80, 62, 1),
(2, '篮球社', '新生篮球友谊赛', '欢迎新成员加入，以球会友', '学校体育馆', '2024-10-18 15:00:00', '2024-10-18 17:00:00', 20, 20, 1),
(2, '篮球社', '篮球技巧训练营', '基础运球、投篮技巧训练', '室外篮球场', '2024-10-22 09:00:00', '2024-10-22 11:00:00', 25, 18, 1),
(2, '篮球社', '校际篮球联赛', '与其他高校进行友谊比赛', '学校体育馆', '2024-10-28 14:00:00', '2024-10-28 17:00:00', 12, 12, 1),
(3, '摄影协会', '秋季校园摄影大赛', '捕捉校园秋日美景，赢取精美奖品', '校园内', '2024-10-15 00:00:00', '2024-11-15 23:59:00', 100, 45, 1),
(3, '摄影协会', '人像摄影讲座', '学习人像摄影技巧和后期处理', '艺术楼205室', '2024-10-21 14:00:00', '2024-10-21 16:00:00', 40, 32, 1),
(3, '摄影协会', '外拍活动-城市夜景', '集体外出拍摄城市夜景', '市中心', '2024-10-26 18:00:00', '2024-10-26 21:00:00', 25, 20, 1),
(4, '志愿者协会', '敬老院爱心探访', '陪伴孤寡老人，送去温暖', '阳光敬老院', '2024-10-19 09:00:00', '2024-10-19 12:00:00', 30, 25, 1),
(4, '志愿者协会', '校园环保行动', '清理校园垃圾，美化校园环境', '校园内', '2024-10-24 14:00:00', '2024-10-24 16:00:00', 50, 42, 1),
(4, '志愿者协会', '社区支教活动', '为社区儿童提供课后辅导', '幸福社区', '2024-10-30 09:00:00', '2024-10-30 12:00:00', 20, 15, 1);

-- =============================================
-- 15. 社团任务表
-- =============================================
DROP TABLE IF EXISTS club_task;
CREATE TABLE club_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    club_id BIGINT NOT NULL COMMENT '社团ID',
    title VARCHAR(200) NOT NULL COMMENT '任务标题',
    description TEXT COMMENT '任务描述',
    assignee_id BIGINT COMMENT '负责人ID',
    assignee_name VARCHAR(50) COMMENT '负责人姓名',
    created_by BIGINT COMMENT '创建者ID',
    created_by_name VARCHAR(50) COMMENT '创建者姓名',
    status TINYINT DEFAULT 0 COMMENT '状态 0-待完成 1-已完成',
    deadline DATETIME COMMENT '截止时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_club_id (club_id),
    INDEX idx_assignee_id (assignee_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社团任务表';

-- =============================================
-- 16. 社团活动签到表
-- =============================================
DROP TABLE IF EXISTS club_activity_sign_in;
CREATE TABLE club_activity_sign_in (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '签到ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户姓名',
    student_no VARCHAR(20) COMMENT '学号',
    sign_in_time DATETIME COMMENT '签到时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_activity_id (activity_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社团活动签到表';

-- =============================================
-- 17. 求助/活动帖子表
-- =============================================
DROP TABLE IF EXISTS help_post;
CREATE TABLE help_post (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '帖子ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    category VARCHAR(50) COMMENT '分类 失物招领/学习求助/二手交易/活动组织/其他',
    images VARCHAR(500) COMMENT '图片(逗号分隔)',
    publisher_id BIGINT NOT NULL COMMENT '发布者ID',
    publisher_name VARCHAR(50) COMMENT '发布者姓名',
    contact_info VARCHAR(100) COMMENT '联系方式',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    reply_count INT DEFAULT 0 COMMENT '回复次数',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶 0-否 1-是',
    is_essence TINYINT DEFAULT 0 COMMENT '是否加精 0-否 1-是',
    status TINYINT DEFAULT 0 COMMENT '状态 0-正常 1-已解决 2-已关闭 3-已删除',
    expire_time DATETIME COMMENT '过期时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category),
    INDEX idx_publisher_id (publisher_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='求助/活动帖子表';

INSERT INTO help_post (title, content, category, images, publisher_id, publisher_name, contact_info, view_count, reply_count, is_top, is_essence, status, expire_time) VALUES
('失物招领：黑色皮质钱包', '今日午后在第一食堂靠窗位置捡到一个黑色皮质钱包，内有身份证、校园卡及少量现金，请失主凭有效证件联系我认领。', '失物招领', 'https://example.com/post/qianbao.jpg', 6, '苏沐宇', '13800138005', 256, 12, 0, 0, 0, '2026-05-25 00:00:00'),
('求高数期末复习笔记与真题', '临近期末，高数复习进度缓慢，求学长学姐分享复习笔记和往年真题，可有偿，万分感谢！', '学习求助', '', 7, '林晚溪', '13800138006', 189, 23, 0, 0, 0, '2026-06-10 00:00:00'),
('出闲置联想小新Pro14笔记本', '出一台2023款联想小新Pro14，配置i5-13500H+16G+512G，成色95新，无拆无修，平时仅用于写论文，价格3500可小刀。', '二手交易', 'https://example.com/post/laptop1.jpg,https://example.com/post/laptop2.jpg', 8, '江辰屿', '13800138007', 321, 18, 0, 0, 0, '2026-06-01 00:00:00'),
('寻一起晨读的小伙伴', '想找几个同学一起在图书馆晨读，每天早上7点到8点，互相监督，共同进步，有意者联系我。', '其他', '', 9, '顾书月', '13800138008', 156, 9, 0, 0, 0, '2026-05-30 00:00:00'),
('组织周末骑行活动', '本周六组织骑行去郊外湿地公园，全程约20公里，沿途风景优美，有兴趣的同学可以一起报名。', '活动组织', 'https://example.com/post/qixing.jpg', 10, '陆景然', '13800138009', 210, 15, 0, 0, 0, '2026-05-17 00:00:00');

-- =============================================
-- 18. 评论表
-- =============================================
DROP TABLE IF EXISTS comment;
CREATE TABLE comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父评论ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户姓名',
    content TEXT NOT NULL COMMENT '评论内容',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态 0-待审核 1-正常 2-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_post_id (post_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

INSERT INTO comment (post_id, parent_id, user_id, user_name, content, like_count, status) VALUES
(1, 0, 7, '林晚溪', '我昨天也在第一食堂丢了钱包，请问里面有一张粉色的校园卡吗？', 5, 1),
(1, 1, 6, '苏沐宇', '有的，你可以联系我确认一下其他信息。', 3, 1),
(2, 0, 10, '陆景然', '我有去年的高数真题和复习笔记，可以发给你，不用钱。', 12, 1),
(2, 3, 7, '林晚溪', '太感谢了！我加你微信吧。', 2, 1),
(3, 0, 9, '顾书月', '电脑还在吗？可以看看实物吗？', 4, 1),
(4, 0, 11, '许知年', '我也想参加晨读，已经加你微信了。', 6, 1),
(5, 0, 6, '苏沐宇', '我报名！正好周末没事做。', 8, 1);

-- =============================================
-- 19. 消息通知表
-- =============================================
DROP TABLE IF EXISTS message;
CREATE TABLE message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    type TINYINT COMMENT '类型 1-系统通知 2-活动提醒 3-私信 4-公告',
    sender_id BIGINT COMMENT '发送者ID',
    sender_name VARCHAR(50) COMMENT '发送者姓名',
    related_id BIGINT COMMENT '关联ID(活动ID/帖子ID等)',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读 0-未读 1-已读',
    read_time DATETIME COMMENT '阅读时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

INSERT INTO message (user_id, title, content, type, sender_id, sender_name, related_id, is_read) VALUES
(6, '活动报名成功通知', '您好，您已成功报名"Python编程入门讲座"，活动时间为2026-05-20 19:00，请准时参加。', 2, 10, '陆景然', 1, 0),
(7, '活动报名成功通知', '您好，您已成功报名"敬老院志愿服务活动"，活动时间为2026-05-18 08:30，请准时在学校南门集合。', 2, 11, '许知年', 3, 0),
(8, '评论回复通知', '您好，您发布的帖子"出闲置联想小新Pro14笔记本"收到了新的评论。', 1, 9, '顾书月', 3, 0),
(9, '系统通知', '您好，您的账号已成功注册，欢迎使用校园服务系统。', 1, 1, '系统管理员', NULL, 1),
(10, '社团申请通过通知', '您好，您申请加入计算机协会已通过审核，欢迎加入我们！', 1, 10, '陆景然', 1, 1);

-- =============================================
-- 20. 校园地图POI点表
-- =============================================
DROP TABLE IF EXISTS campus_poi;
CREATE TABLE campus_poi (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'POI ID',
    name VARCHAR(100) NOT NULL COMMENT '地点名称',
    category VARCHAR(50) COMMENT '分类 教学楼/宿舍/食堂/图书馆/运动场/其他',
    latitude DECIMAL(10, 6) NOT NULL COMMENT '纬度',
    longitude DECIMAL(10, 6) NOT NULL COMMENT '经度',
    address VARCHAR(200) COMMENT '地址',
    description TEXT COMMENT '描述',
    image VARCHAR(255) COMMENT '图片',
    opening_hours VARCHAR(200) COMMENT '开放时间',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT DEFAULT 1 COMMENT '状态 0-停用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='校园地图POI点表';

INSERT INTO campus_poi (name, category, latitude, longitude, address, description, opening_hours, contact_phone) VALUES
('图书馆', '图书馆', 39.9045, 116.4078, '校园中心区', '学校图书馆，藏书丰富，提供自习室和电子阅览室', '08:00-22:00', '010-12345681'),
('教学楼A座', '教学楼', 39.9040, 116.4070, '校园东区', '主要教学楼，共5层，设有多媒体教室和阶梯教室', '07:00-22:00', '010-12345682'),
('宿舍楼1号楼', '宿舍', 39.9055, 116.4080, '校园北区', '男生宿舍楼，共6层，每层设有公共卫生间和洗衣房', '全天开放', '010-12345683'),
('运动场', '运动场', 39.9035, 116.4060, '校园南区', '标准田径场和足球场，周边设有篮球场、网球场和羽毛球场', '06:00-22:00', '010-12345684'),
('校医院', '其他', 39.9048, 116.4085, '校园西区', '提供基础医疗服务和药品售卖', '08:00-20:00', '010-12345685');

-- =============================================
-- 21. 迎新指南内容表
-- =============================================
DROP TABLE IF EXISTS freshman_guide;
CREATE TABLE freshman_guide (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '指南ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    category VARCHAR(50) COMMENT '分类 报到流程/宿舍分配/缴费指引/校园介绍/其他',
    content TEXT NOT NULL COMMENT '内容',
    images VARCHAR(500) COMMENT '图片(逗号分隔)',
    video_url VARCHAR(255) COMMENT '视频链接',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_important TINYINT DEFAULT 0 COMMENT '是否重要 0-否 1-是',
    status TINYINT DEFAULT 1 COMMENT '状态 0-隐藏 1-显示',
    publish_time DATETIME COMMENT '发布时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='迎新指南内容表';

INSERT INTO freshman_guide (title, category, content, sort_order, is_important, status, publish_time) VALUES
('新生报到流程详解', '报到流程', '1. 到达学校南门，在报到处领取报到单；2. 前往各学院接待点办理入学手续；3. 到宿舍管理中心领取宿舍钥匙；4. 前往财务处缴纳学费；5. 领取军训服装和生活用品。', 1, 1, 1, '2026-08-01 09:00:00'),
('宿舍分配及入住须知', '宿舍分配', '宿舍按照学院和班级统一分配，4人一间，上床下桌。入住时请携带身份证和录取通知书，宿舍内禁止使用大功率电器。', 2, 1, 1, '2026-08-01 10:00:00'),
('学费缴纳方式说明', '缴费指引', '学费可通过线上银行转账、微信支付、支付宝支付或现场刷卡缴纳。请在报到前完成缴费，以免影响入学手续办理。', 3, 1, 1, '2026-08-01 11:00:00'),
('校园生活指南', '校园介绍', '学校设有3个食堂、1个超市、1个校医院和多个运动场。校园内有共享单车和校园巴士，出行方便。', 4, 0, 1, '2026-08-02 09:00:00'),
('军训注意事项', '其他', '军训时间为9月1日至9月14日，地点在学校运动场。请准备好防晒用品、水杯和舒适的鞋子。', 5, 0, 1, '2026-08-02 10:00:00');

