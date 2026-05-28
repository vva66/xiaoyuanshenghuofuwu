-- =============================================
-- 校园迎新服务系统 - 全部数据更新SQL（整合版）
-- 包含：用户头像、社团Logo、图片链接
-- 图片来源：Pexels (CC0协议，免费可商用，无需署名)
-- =============================================

USE campus_service;

-- =============================================
-- 1. 用户头像 (sys_user.avatar)
-- =============================================
UPDATE sys_user SET avatar = 'https://images.pexels.com/photos/9471418/pexels-photo-9471418.jpeg?auto=compress&cs=tinysrgb&w=200' WHERE username = 'admin';
UPDATE sys_user SET avatar = 'https://images.pexels.com/photos/3092559/pexels-photo-3092559.jpeg?auto=compress&cs=tinysrgb&w=200' WHERE username = 'zhangsan';
UPDATE sys_user SET avatar = 'https://images.pexels.com/photos/7366429/pexels-photo-7366429.jpeg?auto=compress&cs=tinysrgb&w=200' WHERE username = 'lisi';
UPDATE sys_user SET avatar = 'https://images.pexels.com/photos/5537357/pexels-photo-5537357.jpeg?auto=compress&cs=tinysrgb&w=200' WHERE username = 'wangwu';
UPDATE sys_user SET avatar = 'https://images.pexels.com/photos/16708356/pexels-photo-16708356.jpeg?auto=compress&cs=tinysrgb&w=200' WHERE username = 'qinghe';
UPDATE sys_user SET avatar = 'https://images.pexels.com/photos/17659442/pexels-photo-17659442.jpeg?auto=compress&cs=tinysrgb&w=200' WHERE username = 'muyu';
UPDATE sys_user SET avatar = 'https://images.pexels.com/photos/21006231/pexels-photo-21006231.jpeg?auto=compress&cs=tinysrgb&w=200' WHERE username = 'wanxi';
UPDATE sys_user SET avatar = 'https://images.pexels.com/photos/13702039/pexels-photo-13702039.jpeg?auto=compress&cs=tinysrgb&w=200' WHERE username = 'chenyu';
UPDATE sys_user SET avatar = 'https://images.pexels.com/photos/34411771/pexels-photo-34411771.jpeg?auto=compress&cs=tinysrgb&w=200' WHERE username = 'shuyue';
UPDATE sys_user SET avatar = 'https://images.pexels.com/photos/11202157/pexels-photo-11202157.jpeg?auto=compress&cs=tinysrgb&w=200' WHERE username = 'jingran';
UPDATE sys_user SET avatar = 'https://images.pexels.com/photos/10638096/pexels-photo-10638096.jpeg?auto=compress&cs=tinysrgb&w=200' WHERE username = 'zhinian';
UPDATE sys_user SET avatar = 'https://images.pexels.com/photos/7747337/pexels-photo-7747337.jpeg?auto=compress&cs=tinysrgb&w=200' WHERE username = 'shuhang';

-- =============================================
-- 2. 社团Logo (club.logo)
-- =============================================
UPDATE club SET logo = 'https://images.pexels.com/photos/1181330/pexels-photo-1181330.jpeg?auto=compress&cs=tinysrgb&w=400' WHERE name = '计算机协会';
UPDATE club SET logo = 'https://images.pexels.com/photos/8783116/pexels-photo-8783116.jpeg?auto=compress&cs=tinysrgb&w=400' WHERE name = '篮球社';
UPDATE club SET logo = 'https://images.pexels.com/photos/1024993/pexels-photo-1024993.jpeg?auto=compress&cs=tinysrgb&w=400' WHERE name = '摄影协会';
UPDATE club SET logo = 'https://images.pexels.com/photos/6646858/pexels-photo-6646858.jpeg?auto=compress&cs=tinysrgb&w=400' WHERE name = '志愿者协会';
UPDATE club SET logo = 'https://images.pexels.com/photos/46798/pexels-photo-46798.jpeg?auto=compress&cs=tinysrgb&w=400' WHERE name = '足球社';

-- =============================================
-- 3. 活动封面 (activity.cover_image)
-- =============================================
UPDATE activity SET cover_image = 'https://images.pexels.com/photos/1181671/pexels-photo-1181671.jpeg?auto=compress&cs=tinysrgb&w=800' WHERE title = 'Python编程入门讲座';
UPDATE activity SET cover_image = 'https://images.pexels.com/photos/1752757/pexels-photo-1752757.jpeg?auto=compress&cs=tinysrgb&w=800' WHERE title = '校园篮球联赛';
UPDATE activity SET cover_image = 'https://images.pexels.com/photos/6646858/pexels-photo-6646858.jpeg?auto=compress&cs=tinysrgb&w=800' WHERE title = '敬老院志愿服务活动';
UPDATE activity SET cover_image = 'https://images.pexels.com/photos/1024993/pexels-photo-1024993.jpeg?auto=compress&cs=tinysrgb&w=800' WHERE title = '毕业季摄影展';
UPDATE activity SET cover_image = 'https://images.pexels.com/photos/159751/pexels-photo-159751.jpeg?auto=compress&cs=tinysrgb&w=800' WHERE title = '考研经验分享会';
UPDATE activity SET cover_image = 'https://images.pexels.com/photos/167636/pexels-photo-167636.jpeg?auto=compress&cs=tinysrgb&w=800' WHERE title = '校园歌手大赛';

-- =============================================
-- 4. 食堂图片 (canteen.image)
-- =============================================
UPDATE canteen SET image = 'https://images.pexels.com/photos/2365946/pexels-photo-2365946.jpeg?auto=compress&cs=tinysrgb&w=800' WHERE name = '第一食堂';
UPDATE canteen SET image = 'https://images.pexels.com/photos/2098110/pexels-photo-2098110.jpeg?auto=compress&cs=tinysrgb&w=800' WHERE name = '第二食堂';
UPDATE canteen SET image = 'https://images.pexels.com/photos/6645928/pexels-photo-6645928.jpeg?auto=compress&cs=tinysrgb&w=800' WHERE name = '清真食堂';

-- =============================================
-- 5. 菜品图片 (canteen_dish.image)
-- =============================================
UPDATE canteen_dish SET image = 'https://images.pexels.com/photos/1907244/pexels-photo-1907244.jpeg?auto=compress&cs=tinysrgb&w=600' WHERE dish_name = '豆浆油条';
UPDATE canteen_dish SET image = 'https://images.pexels.com/photos/2113914/pexels-photo-2113914.jpeg?auto=compress&cs=tinysrgb&w=600' WHERE dish_name = '包子套餐';
UPDATE canteen_dish SET image = 'https://images.pexels.com/photos/29529570/pexels-photo-29529570.jpeg?auto=compress&cs=tinysrgb&w=600' WHERE dish_name = '番茄炒蛋盖饭';
UPDATE canteen_dish SET image = 'https://images.pexels.com/photos/36694549/pexels-photo-36694549.jpeg?auto=compress&cs=tinysrgb&w=600' WHERE dish_name = '宫保鸡丁盖饭';
UPDATE canteen_dish SET image = 'https://images.pexels.com/photos/3660/food-restaurant-dinner-lunch.jpg?auto=compress&cs=tinysrgb&w=600' WHERE dish_name = '红烧肉套餐';
UPDATE canteen_dish SET image = 'https://images.pexels.com/photos/8463449/pexels-photo-8463449.jpeg?auto=compress&cs=tinysrgb&w=600' WHERE dish_name = '麻辣烫';
UPDATE canteen_dish SET image = 'https://images.pexels.com/photos/1907229/pexels-photo-1907229.jpeg?auto=compress&cs=tinysrgb&w=600' WHERE dish_name = '兰州拉面';
UPDATE canteen_dish SET image = 'https://images.pexels.com/photos/32432576/pexels-photo-32432576.jpeg?auto=compress&cs=tinysrgb&w=600' WHERE dish_name = '刀削面';
UPDATE canteen_dish SET image = 'https://images.pexels.com/photos/7298033/pexels-photo-7298033.jpeg?auto=compress&cs=tinysrgb&w=600' WHERE dish_name = '麻婆豆腐';
UPDATE canteen_dish SET image = 'https://images.pexels.com/photos/8250362/pexels-photo-8250362.jpeg?auto=compress&cs=tinysrgb&w=600' WHERE dish_name = '水煮肉片';
UPDATE canteen_dish SET image = 'https://images.pexels.com/photos/8463447/pexels-photo-8463447.jpeg?auto=compress&cs=tinysrgb&w=600' WHERE dish_name = '炸鸡套餐';
UPDATE canteen_dish SET image = 'https://images.pexels.com/photos/29305560/pexels-photo-29305560.jpeg?auto=compress&cs=tinysrgb&w=600' WHERE dish_name = '兰州牛肉拉面';
UPDATE canteen_dish SET image = 'https://images.pexels.com/photos/30635676/pexels-photo-30635676.jpeg?auto=compress&cs=tinysrgb&w=600' WHERE dish_name = '大盘鸡';
UPDATE canteen_dish SET image = 'https://images.pexels.com/photos/16569554/pexels-photo-16569554.jpeg?auto=compress&cs=tinysrgb&w=600' WHERE dish_name = '手抓饭';
UPDATE canteen_dish SET image = 'https://images.pexels.com/photos/37080264/pexels-photo-37080264.jpeg?auto=compress&cs=tinysrgb&w=600' WHERE dish_name = '烤羊肉串';

-- =============================================
-- 6. 菜品评价图片 (canteen_review.images)
-- =============================================
UPDATE canteen_review SET images = 'https://images.pexels.com/photos/29529570/pexels-photo-29529570.jpeg?auto=compress&cs=tinysrgb&w=400' WHERE content LIKE '%番茄炒蛋超级好吃%';
UPDATE canteen_review SET images = 'https://images.pexels.com/photos/36694549/pexels-photo-36694549.jpeg?auto=compress&cs=tinysrgb&w=400' WHERE content LIKE '%宫保鸡丁yyds%';
UPDATE canteen_review SET images = 'https://images.pexels.com/photos/3660/food-restaurant-dinner-lunch.jpg?auto=compress&cs=tinysrgb&w=400' WHERE content LIKE '%红烧肉绝了%';
UPDATE canteen_review SET images = 'https://images.pexels.com/photos/7298033/pexels-photo-7298033.jpeg?auto=compress&cs=tinysrgb&w=400' WHERE content LIKE '%麻婆豆腐太下饭%';
UPDATE canteen_review SET images = 'https://images.pexels.com/photos/30635676/pexels-photo-30635676.jpeg?auto=compress&cs=tinysrgb&w=400' WHERE content LIKE '%大盘鸡分量超足%';

-- =============================================
-- 7. 互助广场帖子图片 (help_post.images)
-- =============================================
UPDATE help_post SET images = 'https://images.pexels.com/photos/915917/pexels-photo-915917.jpeg?auto=compress&cs=tinysrgb&w=400' WHERE title = '失物招领：黑色皮质钱包';
UPDATE help_post SET images = 'https://images.pexels.com/photos/7054499/pexels-photo-7054499.jpeg?auto=compress&cs=tinysrgb&w=400' WHERE title = '求高数期末复习笔记与真题';
UPDATE help_post SET images = 'https://images.pexels.com/photos/18105/pexels-photo.jpg?auto=compress&cs=tinysrgb&w=400' WHERE title = '出闲置联想小新Pro14笔记本';
UPDATE help_post SET images = 'https://images.pexels.com/photos/8566998/pexels-photo-8566998.jpeg?auto=compress&cs=tinysrgb&w=400' WHERE title = '寻一起晨读的小伙伴';
UPDATE help_post SET images = 'https://images.pexels.com/photos/276952/pexels-photo-276952.jpeg?auto=compress&cs=tinysrgb&w=400' WHERE title = '组织周末骑行活动';

SELECT '全部数据更新完成！' AS result;
