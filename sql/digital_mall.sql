-- ============================================================================
-- 《Java Web 技术与应用》大作业数据库初始化脚本
-- 项目名称：网上数码配件商城设计与实现（星潮数码配件商城）
-- 作者：张三    学号：20240301    班级：2024级3班
-- 数据库版本：MySQL 5.7 / 8.0（字符集 utf8mb4）
-- ============================================================================

CREATE DATABASE IF NOT EXISTS `digital_mall`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `digital_mall`;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `operation_log`;
DROP TABLE IF EXISTS `product_comment`;
DROP TABLE IF EXISTS `favorite`;
DROP TABLE IF EXISTS `banner`;
DROP TABLE IF EXISTS `order_item`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `users`;

-- 1. 用户信息表（users）
CREATE TABLE `users` (
  `user_id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '用户编号，主键自增',
  `user_name` VARCHAR(32) NOT NULL COMMENT '登录用户名，唯一',
  `password` VARCHAR(64) NOT NULL COMMENT '登录密码（MD5加密存储）',
  `real_name` VARCHAR(32) DEFAULT NULL COMMENT '真实姓名/昵称',
  `email` VARCHAR(64) DEFAULT NULL COMMENT '注册邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `address` VARCHAR(128) DEFAULT NULL COMMENT '默认收货地址',
  `role` INT(2) NOT NULL DEFAULT 0 COMMENT '角色：0普通用户，1系统管理员',
  `status` INT(2) NOT NULL DEFAULT 1 COMMENT '状态：1正常，0禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_user_name` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 2. 商品分类表（category）
CREATE TABLE `category` (
  `category_id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '分类编号，主键自增',
  `category_name` VARCHAR(32) NOT NULL COMMENT '分类名称',
  `category_desc` VARCHAR(128) DEFAULT NULL COMMENT '分类简介',
  `sort_order` INT(11) NOT NULL DEFAULT 1 COMMENT '排序权重（越小越靠前）',
  `parent_id` INT(11) DEFAULT 0 COMMENT '上级分类编号（0表示一级分类）',
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 3. 商品信息表（product）
CREATE TABLE `product` (
  `product_id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '商品编号，主键自增',
  `product_name` VARCHAR(64) NOT NULL COMMENT '商品名称',
  `subtitle` VARCHAR(128) DEFAULT NULL COMMENT '商品卖点副标题',
  `category_id` INT(11) NOT NULL COMMENT '所属分类编号，外键',
  `price` DECIMAL(10,2) NOT NULL COMMENT '商品原价（元）',
  `discount` DECIMAL(3,2) NOT NULL DEFAULT 1.00 COMMENT '商品折扣率（0.10~1.00，1.00为无折扣）',
  `stock` INT(11) NOT NULL DEFAULT 100 COMMENT '库存数量',
  `sales` INT(11) NOT NULL DEFAULT 0 COMMENT '累计销量',
  `image` VARCHAR(128) DEFAULT NULL COMMENT '商品主图相对路径',
  `specs` VARCHAR(255) DEFAULT NULL COMMENT '核心规格参数说明',
  `description` TEXT COMMENT '商品详细介绍',
  `is_hot` INT(2) NOT NULL DEFAULT 0 COMMENT '是否推荐热销：1是，0否',
  `status` INT(2) NOT NULL DEFAULT 1 COMMENT '上架状态：1上架，0下架',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`product_id`),
  KEY `idx_product_category` (`category_id`),
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品信息表';

-- 4. 订单主表（orders）
CREATE TABLE `orders` (
  `order_id` VARCHAR(32) NOT NULL COMMENT '订单编号，主键',
  `user_id` INT(11) NOT NULL COMMENT '下单用户编号，外键',
  `total_price` DECIMAL(10,2) NOT NULL COMMENT '订单实付总金额（元）',
  `order_state` INT(2) NOT NULL DEFAULT 0 COMMENT '订单状态：0待发货，1已发货，2已完成，3已取消',
  `consignee` VARCHAR(32) NOT NULL COMMENT '收货人姓名',
  `phone` VARCHAR(20) NOT NULL COMMENT '收货联系电话',
  `address` VARCHAR(255) NOT NULL COMMENT '详细收货地址',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '订单备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后状态更新时间',
  PRIMARY KEY (`order_id`),
  KEY `idx_orders_user` (`user_id`),
  CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- 5. 订单明细表（order_item）
CREATE TABLE `order_item` (
  `item_id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '明细编号，主键自增',
  `order_id` VARCHAR(32) NOT NULL COMMENT '所属订单编号，外键',
  `product_id` INT(11) NOT NULL COMMENT '商品编号，外键',
  `product_name` VARCHAR(64) NOT NULL COMMENT '下单时商品名称快照',
  `product_image` VARCHAR(128) DEFAULT NULL COMMENT '下单时商品图片快照',
  `unit_price` DECIMAL(10,2) NOT NULL COMMENT '下单时折后单价（元）',
  `quantity` INT(11) NOT NULL COMMENT '购买数量',
  `subtotal` DECIMAL(10,2) NOT NULL COMMENT '明细小计金额（元）',
  PRIMARY KEY (`item_id`),
  KEY `idx_item_order` (`order_id`),
  KEY `idx_item_product` (`product_id`),
  CONSTRAINT `fk_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品明细表';

-- 6. 首页轮播广告表（banner）
CREATE TABLE `banner` (
  `banner_id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '轮播图编号，主键自增',
  `title` VARCHAR(64) NOT NULL COMMENT '主标题',
  `subtitle` VARCHAR(128) DEFAULT NULL COMMENT '副标题描述',
  `badge` VARCHAR(32) DEFAULT NULL COMMENT '角标文字',
  `image_url` VARCHAR(128) NOT NULL COMMENT '轮播背景图片路径',
  `link_url` VARCHAR(128) NOT NULL COMMENT '点击跳转链接',
  `sort_order` INT(11) NOT NULL DEFAULT 1 COMMENT '展示顺序',
  `status` INT(2) NOT NULL DEFAULT 1 COMMENT '状态：1启用，0停用',
  PRIMARY KEY (`banner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='首页轮播图配置表';

-- 7. 用户商品收藏表（favorite）
CREATE TABLE `favorite` (
  `fav_id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '收藏编号，主键自增',
  `user_id` INT(11) NOT NULL COMMENT '用户编号',
  `product_id` INT(11) NOT NULL COMMENT '商品编号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`fav_id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
  CONSTRAINT `fk_fav_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_fav_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户商品收藏表';

-- 8. 商品评价表（product_comment）
CREATE TABLE `product_comment` (
  `comment_id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '评价编号，主键自增',
  `product_id` INT(11) NOT NULL COMMENT '商品编号',
  `user_id` INT(11) NOT NULL COMMENT '评价用户编号',
  `rating` INT(2) NOT NULL DEFAULT 5 COMMENT '星级评分（1-5星）',
  `content` VARCHAR(500) NOT NULL COMMENT '评价正文',
  `status` INT(2) NOT NULL DEFAULT 1 COMMENT '审核状态：1正常显示，0已屏蔽',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  PRIMARY KEY (`comment_id`),
  CONSTRAINT `fk_comment_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

-- 9. 后台操作审计日志表（operation_log）
CREATE TABLE `operation_log` (
  `log_id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '日志编号，主键自增',
  `admin_name` VARCHAR(32) NOT NULL COMMENT '操作管理员账号',
  `module_name` VARCHAR(32) NOT NULL COMMENT '所属功能模块',
  `action_desc` VARCHAR(255) NOT NULL COMMENT '具体操作内容',
  `ip_addr` VARCHAR(64) DEFAULT NULL COMMENT '客户端IP地址',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台操作日志表';

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- 初始化测试数据（密码采用标准 MD5：admin123 -> 0192023a7bbd73250516f069df18b500；123456 -> e10adc3949ba59abbe56e057f20f883e）
-- ============================================================================
INSERT INTO `users` (`user_id`, `user_name`, `password`, `real_name`, `email`, `phone`, `address`, `role`, `status`, `create_time`) VALUES
(1, 'admin', '0192023a7bbd73250516f069df18b500', '系统管理员', 'admin@xingchao.com', '13800138000', '北京市海淀区中关村软件园8号楼', 1, 1, '2026-10-01 09:00:00'),
(2, 'zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', 'zhangsan@stu.edu.cn', '13910682024', '信息工程学院学生公寓3号楼402室', 0, 1, '2026-10-15 14:20:00'),
(3, 'linwei', 'e10adc3949ba59abbe56e057f20f883e', '林薇', 'linwei@example.com', '18611928831', '上海市浦东新区张江高科科苑路88号', 0, 1, '2026-10-18 10:35:00'),
(4, 'chenhao', 'e10adc3949ba59abbe56e057f20f883e', '陈浩', 'chenhao@example.com', '13760219950', '深圳市南山区粤海街道科技南十二路', 0, 1, '2026-10-25 19:12:00');

INSERT INTO `category` (`category_id`, `category_name`, `category_desc`, `sort_order`, `parent_id`) VALUES
(1, '声学音频', '旗舰主动降噪头戴耳机、Hi-Res 无损真无线耳塞', 1, 0),
(2, '氮化镓快充', 'PD 3.1 氮化镓桌面充电器、Qi2 磁吸无线移动电源', 2, 0),
(3, '客制化外设', 'CNC 铝合金机械键盘、轻量化人体工学无线鼠标', 3, 0),
(4, '高速扩展存储', '雷电4 全功能扩展坞、40Gbps 移动固态硬盘与编织线材', 4, 0),
(5, '智能穿戴配件', '航空钛金属表带、三合一桌面磁吸快充底座', 5, 0);

INSERT INTO `product` (`product_id`, `product_name`, `subtitle`, `category_id`, `price`, `discount`, `stock`, `sales`, `image`, `specs`, `description`, `is_hot`, `status`) VALUES
(1, 'AeroPods Studio 钛金属主动降噪头戴耳机', '48dB 混合宽频降噪 · LDAC 无损传输 · 60小时续航', 1, 1299.00, 0.90, 46, 182, 'static/images/prod_headphones.jpg', '蓝牙 5.4 / 40mm 镀铍振膜 / USB-C 无损音频 / 265g 轻量化机身', '采用阳极氧化航空铝耳罩与蛋白皮记忆海绵耳垫，搭载双核自适应环境降噪芯片，无论图书馆研习还是通勤旅途均能呈现纯净声场。', 1, 1),
(2, 'PulseBuds Pro 铝镁合金真无线降噪耳塞', 'CNC 金属充电仓 · 双馈主动降噪 · 空间音频追踪', 1, 699.00, 0.85, 78, 265, 'static/images/prod_earbuds.jpg', '蓝牙 5.4 / IP54 防尘抗水 / 单次 9h + 综合 36h 续航 / Qi 无线充电', '精密 CNC 一体成型拉丝金属充电盒，内置 11mm 定制动圈单元与骨传导通话降噪麦克风，兼顾通透高频与澎湃低频表现。', 1, 1),
(3, 'VoltGaN 140W 氮化镓三口PD3.1快充充电器', '第五代氮化镓芯片 · 单口满血 140W · 智能功率分配', 2, 299.00, 0.88, 120, 410, 'static/images/prod_gan140.jpg', '2C1A 三口输出 / PD3.1 & UFCS 融合快充 / 折叠插脚 / 实时温控保护', '体积较传统充电器缩小 42%，可同时为 MacBook Pro、平板与旗舰手机高速补电，内置毫秒级 NTC 控温矩阵，低温快充不伤机。', 1, 1),
(4, 'MagSlate 10000mAh 超薄磁吸无线充电宝', 'Qi2 认证 15W 满速无线充 · 铝合金散热背板', 2, 249.00, 0.92, 95, 328, 'static/images/prod_powerbank.jpg', '10000mAh 高密度电芯 / Qi2 15W 磁吸 + 30W 有线双向快充 / 厚度仅 12.8mm', 'N52 强力磁吸阵列一贴即合，深空灰磨砂金属外壳带来温润握持感，支持边充边放与小电流耳机充电模式。', 1, 1),
(5, 'KeyCraft 75% CNC全铝客制化三模机械键盘', 'Gasket 软弹结构 · 热插拔线性轴 · 多功能金属旋钮', 3, 799.00, 0.85, 35, 146, 'static/images/prod_keyboard.jpg', '81键紧凑配列 / 6063 阳极氧化铝机身 / PBT 双色键帽 / 有线+2.4G+蓝牙三模', '整块国标 6063 铝合金经 CNC 精雕与 220 目喷砂阳极处理，五层消音填充带来纯粹 HiFi 麻将音手感，右上角多媒体金属旋钮支持音量与灯光调节。', 1, 1),
(6, 'KeyCraft Pro 暗夜灰客制化双模静电容机械键盘', '低延迟 1000Hz 回报率 · 全键无冲 · 程序员专属配列', 3, 899.00, 1.00, 28, 89, 'static/images/prod_keyboard_pro.jpg', '84键配列 / 霍尔磁轴触发可调 / PBT 侧刻键帽 / 8000mAh 大电池', '专为高频代码编写与桌面极简美学打造，键程触发点支持 0.2mm~3.8mm 自定义微调，长续航免去频繁充电烦恼。', 0, 1),
(7, 'FlowMaster 3S 人体工学双滚轮无线办公鼠标', '电磁无极疾速滚轮 · 静音微动 · 跨设备多屏流转', 3, 459.00, 0.90, 64, 215, 'static/images/prod_mouse.jpg', '8000 DPI 玻璃追踪传感器 / 拇指横滚轮 / 蓝牙+接收器双连接 / Type-C 快充', '贴合亚洲手型的黄金倾角掌托设计，侧边独立横向滚轮大幅提升宽表与代码阅读效率，静音按键适合宿舍与图书馆安静环境。', 1, 1),
(8, 'OmniDock 10合1 雷电4高速全功能铝合金扩展坞', '40Gbps 满速传输 · 4K@120Hz 视频输出 · 100W PD供电', 4, 599.00, 0.85, 52, 173, 'static/images/prod_dock.jpg', '双 HDMI/DP 异显 / 2.5G 千兆网口 / SD4.0 高速读卡 / 10Gbps USB-A*3', '一根线材即可为轻薄笔记本扩展完整桌面工作站接口，一体式铝合金鳍片高效导热，连续满载传输稳定不掉盘。', 1, 1),
(9, 'SwiftBlade USB4 40Gbps 铝合金移动固态硬盘盒', '兼容 M.2 NVMe 2280 · 最高读取 3800MB/s · 免工具快拆', 4, 369.00, 0.95, 70, 134, 'static/images/prod_ssd.jpg', 'USB4 / 雷电3&4 兼容 / 内置静音涡轮散热片 / 附 40Gbps 数据线', '磁吸滑盖免螺丝安装结构，内置高导热硅胶垫与铝合金散热装甲，轻松胜任 4K 视频剪辑工程库与大型虚拟机随身运行。', 0, 1),
(10, 'FlexWeave 240W E-Marker 凯夫拉编织快充数据线', 'PD3.1 240W 满功率 · 锌合金端子 · 30000次弯折测试', 4, 79.00, 0.80, 260, 580, 'static/images/prod_cable.jpg', '长度 1.5m / 内置 E-Marker 智能芯片 / 48锭高密防缠绕编织线身', '高密度芳纶纤维编织外被柔韧耐拉扯，内置认证 E-Marker 芯片自动匹配设备所需安全电流，笔记本、平板、手机一线通用。', 0, 1),
(11, 'TitanLink 五级航空钛金属智能手表磁吸表带', '超轻亲肤防过敏 · DLC 类钻碳抗刮涂层 · 快拆表耳', 5, 329.00, 0.90, 42, 97, 'static/images/prod_watchband.jpg', '五级钛合金链节 / 隐藏式磁吸折叠扣 / 重量仅 46g / 附赠调表器', '选用高强度轻质钛金属精密研磨，兼顾正装商务质感与全天候无感佩戴体验，卡扣开合顺滑稳固。', 0, 1),
(12, 'HaloPuck 便携不锈钢智能手表磁吸快充底座', '原装级快充模块 · 铝合金CNC外壳 · 差旅随行收纳', 5, 139.00, 1.00, 88, 152, 'static/images/prod_watchcharger.jpg', '5W 无线磁吸快充 / 1.2m 柔肤硅胶编织线 / 过温过流多重防护', '精密微弧凹面自动吸附对位，约 45 分钟即可充入 80% 电量，小巧圆润可轻松放入随身数码收纳包。', 0, 1);

INSERT INTO `banner` (`banner_id`, `title`, `subtitle`, `badge`, `image_url`, `link_url`, `sort_order`, `status`) VALUES
(1, '纯净声学 · 静享灵感时刻', 'AeroPods Studio 钛金属旗舰降噪耳机领衔，声学音频专区限时 8.5 折起', '年度旗舰声学季', 'static/images/banner_audio.jpg', 'products?categoryId=1', 1, 1),
(2, '极简桌面 · 重塑高效生产力', 'CNC 客制化铝坨坨机械键盘与雷电 4 全功能扩展坞，打造理想代码工作台', '桌面美学升级', 'static/images/banner_desk.jpg', 'products?categoryId=3', 2, 1),
(3, '满血氮化镓 · 疾速能量矩阵', '140W PD3.1 三口快充与 Qi2 超薄磁吸充电宝，差旅出行一机多充', '快充新品首发', 'static/images/banner_power.jpg', 'products?categoryId=2', 3, 1);

INSERT INTO `orders` (`order_id`, `user_id`, `total_price`, `order_state`, `consignee`, `phone`, `address`, `remark`, `create_time`, `update_time`) VALUES
('ORD20261118091501', 2, 1432.32, 2, '张三', '13910682024', '信息工程学院学生公寓3号楼402室', '麻烦周末派送，放到宿管站即可', '2026-11-18 09:15:22', '2026-11-19 16:40:00'),
('ORD20261119142802', 3, 1188.30, 2, '林薇', '18611928831', '上海市浦东新区张江高科科苑路88号', '公司前台签收', '2026-11-19 14:28:10', '2026-11-20 11:20:00'),
('ORD20261120160503', 4, 738.30, 1, '陈浩', '13760219950', '深圳市南山区粤海街道科技南十二路', '顺丰包邮谢谢', '2026-11-20 16:05:45', '2026-11-21 09:30:00'),
('ORD20261121201204', 2, 742.35, 1, '张三', '13910682024', '信息工程学院实验楼B座305机房', '实验室急用扩展坞和快充线', '2026-11-21 20:12:33', '2026-11-22 10:15:00'),
('ORD20261122114005', 3, 888.30, 2, '林薇', '18611928831', '上海市浦东新区张江高科科苑路88号', '送同事生日礼物，请妥善包装', '2026-11-22 11:40:18', '2026-11-23 15:05:00'),
('ORD20261123193006', 4, 1092.25, 0, '陈浩', '13760219950', '深圳市南山区粤海街道科技南十二路', '默认配置即可', '2026-11-23 19:30:00', '2026-11-23 19:30:00'),
('ORD20261124100807', 2, 492.23, 0, '张三', '13910682024', '信息工程学院学生公寓3号楼402室', '加购充电组合', '2026-11-24 10:08:12', '2026-11-24 10:08:12');

INSERT INTO `order_item` (`order_id`, `product_id`, `product_name`, `product_image`, `unit_price`, `quantity`, `subtotal`) VALUES
('ORD20261118091501', 1, 'AeroPods Studio 钛金属主动降噪头戴耳机', 'static/images/prod_headphones.jpg', 1169.10, 1, 1169.10),
('ORD20261118091501', 3, 'VoltGaN 140W 氮化镓三口PD3.1快充充电器', 'static/images/prod_gan140.jpg', 263.12, 1, 263.12),
('ORD20261119142802', 5, 'KeyCraft 75% CNC全铝客制化三模机械键盘', 'static/images/prod_keyboard.jpg', 679.15, 1, 679.15),
('ORD20261119142802', 8, 'OmniDock 10合1 雷电4高速全功能铝合金扩展坞', 'static/images/prod_dock.jpg', 509.15, 1, 509.15),
('ORD20261120160503', 2, 'PulseBuds Pro 铝镁合金真无线降噪耳塞', 'static/images/prod_earbuds.jpg', 594.15, 1, 594.15),
('ORD20261120160503', 10, 'FlexWeave 240W E-Marker 凯夫拉编织快充数据线', 'static/images/prod_cable.jpg', 63.20, 2, 126.40),
('ORD20261121201204', 8, 'OmniDock 10合1 雷电4高速全功能铝合金扩展坞', 'static/images/prod_dock.jpg', 509.15, 1, 509.15),
('ORD20261121201204', 4, 'MagSlate 10000mAh 超薄磁吸无线充电宝', 'static/images/prod_powerbank.jpg', 229.08, 1, 229.08),
('ORD20261122114005', 7, 'FlowMaster 3S 人体工学双滚轮无线办公鼠标', 'static/images/prod_mouse.jpg', 413.10, 1, 413.10),
('ORD20261122114005', 11, 'TitanLink 五级航空钛金属智能手表磁吸表带', 'static/images/prod_watchband.jpg', 296.10, 1, 296.10),
('ORD20261122114005', 12, 'HaloPuck 便携不锈钢智能手表磁吸快充底座', 'static/images/prod_watchcharger.jpg', 139.00, 1, 139.00),
('ORD20261123193006', 5, 'KeyCraft 75% CNC全铝客制化三模机械键盘', 'static/images/prod_keyboard.jpg', 679.15, 1, 679.15),
('ORD20261123193006', 7, 'FlowMaster 3S 人体工学双滚轮无线办公鼠标', 'static/images/prod_mouse.jpg', 413.10, 1, 413.10),
('ORD20261124100807', 3, 'VoltGaN 140W 氮化镓三口PD3.1快充充电器', 'static/images/prod_gan140.jpg', 263.12, 1, 263.12),
('ORD20261124100807', 4, 'MagSlate 10000mAh 超薄磁吸无线充电宝', 'static/images/prod_powerbank.jpg', 229.08, 1, 229.08);

INSERT INTO `favorite` (`user_id`, `product_id`, `create_time`) VALUES
(2, 1, '2026-11-18 09:05:00'),
(2, 5, '2026-11-19 21:10:00'),
(2, 8, '2026-11-21 19:55:00'),
(3, 2, '2026-11-20 12:00:00');

INSERT INTO `product_comment` (`product_id`, `user_id`, `rating`, `content`, `status`, `create_time`) VALUES
(1, 2, 5, '降噪深度非常惊艳，在机房和图书馆开启降噪后几乎听不到键盘与风扇杂音，钛金属头梁质感一流！', 1, '2026-11-20 18:30:00'),
(3, 2, 5, '140W 氮化镓体积比原装充电器小太多了，同时充笔记本和手机温度控制得也很稳，非常推荐。', 1, '2026-11-20 18:35:00'),
(5, 3, 5, '铝坨坨分量十足，Gasket 结构敲代码手感软弹不震手，右上角音量旋钮超级实用！', 1, '2026-11-21 10:12:00'),
(8, 2, 5, '雷电4扩展坞外接 4K 显示器满帧无闪烁，铝合金外壳散热靠谱，桌面瞬间整洁了。', 1, '2026-11-22 19:40:00'),
(7, 3, 4, '无极滚轮看长文档和表格太省力了，静音微动在办公室用完全不打扰同事。', 1, '2026-11-23 16:20:00');

INSERT INTO `operation_log` (`admin_name`, `module_name`, `action_desc`, `ip_addr`, `create_time`) VALUES
('admin', '系统认证', '管理员 admin 登录后台管理系统', '127.0.0.1', '2026-11-22 09:00:12'),
('admin', '折扣管理', '调整商品 [KeyCraft 75% CNC全铝客制化三模机械键盘] 折扣率为 0.85（8.5折）', '127.0.0.1', '2026-11-22 09:15:40'),
('admin', '订单管理', '处理订单 [ORD20261121201204] 状态更新为：已发货', '127.0.0.1', '2026-11-22 10:15:00'),
('admin', '轮播图管理', '更新首页轮播广告 [纯净声学 · 静享灵感时刻] 排序权重为 1', '127.0.0.1', '2026-11-23 14:20:05');
