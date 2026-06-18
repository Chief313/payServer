-- ============================================================
-- payServer 数据库初始化脚本
-- MySQL 8.4 | 端口 3306 | 字符集 utf8mb4
-- ============================================================
CREATE DATABASE IF NOT EXISTS payserver_db
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE payserver_db;

CREATE TABLE IF NOT EXISTS sys_user (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  username    VARCHAR(50)  NOT NULL COMMENT '登录用户名',
  password    VARCHAR(64)  NOT NULL COMMENT 'MD5加密密码',
  nickname    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
  phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  email       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  role        VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
  status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB COMMENT='系统用户表';

CREATE TABLE IF NOT EXISTS user_address (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  user_id     BIGINT       NOT NULL COMMENT '用户ID',
  receiver    VARCHAR(50)  NOT NULL COMMENT '收货人',
  phone       VARCHAR(20)  NOT NULL COMMENT '联系电话',
  province    VARCHAR(50)  NOT NULL,
  city        VARCHAR(50)  NOT NULL,
  district    VARCHAR(50)  NOT NULL,
  detail      VARCHAR(200) NOT NULL COMMENT '详细地址',
  is_default  TINYINT      NOT NULL DEFAULT 0 COMMENT '是否默认',
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_user_id (user_id)
) ENGINE=InnoDB COMMENT='用户收货地址';

CREATE TABLE IF NOT EXISTS category (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  name        VARCHAR(50)  NOT NULL COMMENT '分类名称',
  parent_id   BIGINT       NOT NULL DEFAULT 0 COMMENT '父分类ID',
  sort        INT          NOT NULL DEFAULT 0,
  status      TINYINT      NOT NULL DEFAULT 1,
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='商品分类';

CREATE TABLE IF NOT EXISTS product (
  id          BIGINT        NOT NULL AUTO_INCREMENT,
  category_id BIGINT        NOT NULL,
  name        VARCHAR(100)  NOT NULL COMMENT '商品名称',
  description TEXT          COMMENT '商品描述',
  cover_url   VARCHAR(500)  DEFAULT NULL COMMENT '封面图URL',
  price       DECIMAL(10,2) NOT NULL COMMENT '售价',
  status      TINYINT       NOT NULL DEFAULT 1 COMMENT '1上架 0下架',
  create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_category_id (category_id)
) ENGINE=InnoDB COMMENT='商品表';

CREATE TABLE IF NOT EXISTS sku (
  id           BIGINT        NOT NULL AUTO_INCREMENT,
  product_id   BIGINT        NOT NULL,
  sku_name     VARCHAR(100)  NOT NULL COMMENT '规格名称',
  price        DECIMAL(10,2) NOT NULL,
  stock        INT           NOT NULL DEFAULT 0 COMMENT '可用库存',
  locked_stock INT           NOT NULL DEFAULT 0 COMMENT '预占库存',
  create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_product_id (product_id)
) ENGINE=InnoDB COMMENT='商品SKU';

CREATE TABLE IF NOT EXISTS cart_item (
  id          BIGINT   NOT NULL AUTO_INCREMENT,
  user_id     BIGINT   NOT NULL,
  sku_id      BIGINT   NOT NULL,
  quantity    INT      NOT NULL DEFAULT 1,
  selected    TINYINT  NOT NULL DEFAULT 1,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_sku (user_id, sku_id)
) ENGINE=InnoDB COMMENT='购物车';

CREATE TABLE IF NOT EXISTS t_order (
  id               BIGINT        NOT NULL AUTO_INCREMENT,
  order_no         VARCHAR(32)   NOT NULL COMMENT '订单号',
  user_id          BIGINT        NOT NULL,
  total_amount     DECIMAL(10,2) NOT NULL COMMENT '订单总额',
  pay_amount       DECIMAL(10,2) DEFAULT NULL COMMENT '实付金额',
  status           VARCHAR(20)   NOT NULL DEFAULT 'PENDING_PAY' COMMENT '订单状态',
  address_snapshot JSON          NOT NULL COMMENT '收货地址快照',
  pay_time         DATETIME      DEFAULT NULL,
  ship_time        DATETIME      DEFAULT NULL,
  complete_time    DATETIME      DEFAULT NULL,
  create_time      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_order_no (order_no),
  KEY idx_user_id (user_id)
) ENGINE=InnoDB COMMENT='订单表';

CREATE TABLE IF NOT EXISTS order_item (
  id           BIGINT        NOT NULL AUTO_INCREMENT,
  order_id     BIGINT        NOT NULL,
  product_id   BIGINT        NOT NULL,
  sku_id       BIGINT        NOT NULL,
  product_name VARCHAR(100)  NOT NULL,
  sku_name     VARCHAR(100)  NOT NULL,
  price        DECIMAL(10,2) NOT NULL,
  quantity     INT           NOT NULL,
  PRIMARY KEY (id),
  KEY idx_order_id (order_id)
) ENGINE=InnoDB COMMENT='订单明细';

CREATE TABLE IF NOT EXISTS payment_record (
  id          BIGINT        NOT NULL AUTO_INCREMENT,
  order_no    VARCHAR(32)   NOT NULL,
  pay_channel VARCHAR(20)   NOT NULL DEFAULT 'ALIPAY' COMMENT '支付渠道',
  trade_no    VARCHAR(64)   DEFAULT NULL COMMENT '支付宝交易号',
  amount      DECIMAL(10,2) NOT NULL,
  status      VARCHAR(20)   NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/SUCCESS/FAILED',
  pay_time    DATETIME      DEFAULT NULL,
  notify_data TEXT          COMMENT '异步通知原文',
  create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_order_no (order_no)
) ENGINE=InnoDB COMMENT='支付记录';

CREATE TABLE IF NOT EXISTS chat_session (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  user_id     BIGINT       DEFAULT NULL COMMENT '用户ID，匿名为空',
  title       VARCHAR(100) DEFAULT '新对话',
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_user_id (user_id)
) ENGINE=InnoDB COMMENT='AI客服会话';

CREATE TABLE IF NOT EXISTS chat_message (
  id          BIGINT      NOT NULL AUTO_INCREMENT,
  session_id  BIGINT      NOT NULL,
  role        VARCHAR(20) NOT NULL COMMENT 'user/assistant/system',
  content     TEXT        NOT NULL,
  create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_session_id (session_id)
) ENGINE=InnoDB COMMENT='AI客服消息';

CREATE TABLE IF NOT EXISTS knowledge_document (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  title         VARCHAR(200) NOT NULL COMMENT '文档标题',
  file_name     VARCHAR(200) NOT NULL COMMENT '原始文件名',
  file_type     VARCHAR(20)  NOT NULL COMMENT 'txt/doc/pdf/markdown',
  file_path     VARCHAR(500) NOT NULL COMMENT '存储路径',
  file_size     BIGINT       NOT NULL DEFAULT 0,
  chunk_count   INT          NOT NULL DEFAULT 0 COMMENT '分块数量',
  vector_status VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/DONE/FAILED',
  create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='AI知识库文档';

INSERT INTO sys_user (username, password, nickname, phone, role) VALUES
('admin',  'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '13800000001', 'ADMIN'),
('user01', 'e10adc3949ba59abbe56e057f20f883e', '测试用户甲', '13800000002', 'USER'),
('user02', 'e10adc3949ba59abbe56e057f20f883e', '测试用户乙', '13800000003', 'USER');

INSERT INTO user_address (user_id, receiver, phone, province, city, district, detail, is_default) VALUES
(2, '张三', '13800000002', '广东省', '深圳市', '南山区', '科技园南路1号', 1),
(3, '李四', '13800000003', '北京市', '北京市', '朝阳区', '望京街道10号', 1);

INSERT INTO category (name, parent_id, sort) VALUES
('数码电子', 0, 1), ('家用电器', 0, 2), ('服装鞋包', 0, 3), ('食品生鲜', 0, 4), ('美妆护肤', 0, 5), ('家居生活', 0, 6);

INSERT INTO category (name, parent_id, sort) VALUES
('手机通讯', 1, 1), ('电脑办公', 1, 2), ('平板设备', 1, 3), ('耳机音响', 1, 4), ('智能穿戴', 1, 5),
('空调制冷', 2, 1), ('冰箱冷柜', 2, 2), ('洗衣烘干', 2, 3), ('厨房电器', 2, 4), ('清洁电器', 2, 5),
('男装', 3, 1), ('女装', 3, 2), ('运动鞋', 3, 3), ('箱包配饰', 3, 4),
('新鲜水果', 4, 1), ('休闲零食', 4, 2), ('饮料冲调', 4, 3),
('面部护肤', 5, 1), ('彩妆香水', 5, 2),
('床上用品', 6, 1), ('收纳整理', 6, 2);

INSERT INTO product (category_id, name, description, cover_url, price, status) VALUES
(7,  '旗舰智能手机 Pro Max', '6.7英寸 OLED 屏，旗舰芯片，5000mAh 大电池', '', 4999.00, 1),
(7,  '轻薄智能手机 SE', '6.1英寸小屏旗舰，手感轻盈', '', 2999.00, 1),
(8,  '轻薄商务笔记本', '14英寸 2.8K 屏，16G+512G，续航 12 小时', '', 5999.00, 1),
(8,  '游戏性能笔记本', 'RTX 独显，144Hz 高刷', '', 8999.00, 1),
(9,  'Pro 平板电脑', '11英寸，支持手写笔', '', 3999.00, 1),
(10, '降噪无线耳机', '主动降噪，40 小时续航', '', 899.00, 1),
(10, '蓝牙音箱 Mini', '360° 环绕音效，IPX7 防水', '', 399.00, 1),
(11, '智能运动手表', '心率血氧监测，100+ 运动模式', '', 1299.00, 1),
(11, '智能手环 8', '睡眠监测，NFC 门禁', '', 249.00, 1),
(12, '变频空调 1.5匹', '新一级能效，自清洁', '', 2799.00, 1),
(13, '对开门冰箱 500L', '风冷无霜，双变频', '', 3599.00, 1),
(14, '滚筒洗衣机 10kg', '蒸汽除菌，智能投放', '', 2499.00, 1),
(15, '空气炸锅 5L', '无油健康烹饪', '', 299.00, 1),
(15, '破壁料理机', '35000 转/分钟，静音设计', '', 599.00, 1),
(16, '无线吸尘器', '25000Pa 大吸力，60 分钟续航', '', 1299.00, 1),
(16, '空气净化器', 'CADR 600+，H13 级滤网', '', 1599.00, 1),
(17, '男士休闲夹克', '防风透气，简约百搭', '', 399.00, 1),
(18, '女士针织连衣裙', '柔软亲肤，修身显瘦', '', 259.00, 1),
(19, '跑步运动鞋', '缓震中底，透气网面', '', 599.00, 1),
(20, '商务双肩背包', '大容量多隔层，防泼水', '', 199.00, 1),
(21, '智利进口车厘子 2斤', 'JJJ 级大果，冷链直达', '', 128.00, 1),
(22, '每日坚果礼盒', '8 种坚果混合，独立小包装', '', 89.00, 1),
(23, '有机纯牛奶 整箱', '3.6g 优质蛋白，24 盒装', '', 69.00, 1),
(24, '玻尿酸保湿套装', '洁面+水+乳+霜四件套', '', 299.00, 1),
(25, '哑光唇釉套装', '6 色热门色号', '', 159.00, 1),
(26, '全棉四件套', '60 支长绒棉，亲肤透气', '', 399.00, 1),
(27, '真空收纳袋 10件', '节省 75% 空间', '', 49.00, 1);

INSERT INTO sku (product_id, sku_name, price, stock) VALUES
(1, '黑色 256G', 4999.00, 80), (1, '白色 512G', 5499.00, 50),
(2, '蓝色 128G', 2999.00, 120), (2, '粉色 256G', 3299.00, 60),
(3, '银色 16G+512G', 5999.00, 40), (4, '黑色 RTX4060', 8999.00, 30),
(5, 'WiFi 128G', 3999.00, 60), (6, '黑色', 899.00, 200),
(7, '经典黑', 399.00, 300), (8, '黑色 46mm', 1299.00, 80),
(9, '标准版', 249.00, 500), (10, '1.5匹 冷暖', 2799.00, 40),
(11, '银色 500L', 3599.00, 30), (12, '10kg 白色', 2499.00, 35),
(13, '5L 经典白', 299.00, 200), (14, '单机版', 599.00, 100),
(15, '标准版', 1299.00, 60), (16, 'Pro 版', 1599.00, 45),
(17, 'L 码 深灰', 399.00, 150), (18, 'M 码 黑色', 259.00, 120),
(19, '42 码 黑白', 599.00, 90), (20, '黑色 15.6寸', 199.00, 200),
(21, '2斤装', 128.00, 100), (22, '750g 礼盒', 89.00, 300),
(23, '250ml×24盒', 69.00, 500), (24, '四件套', 299.00, 150),
(25, '6色套装', 159.00, 200), (26, '1.8m 床四件套', 399.00, 80),
(27, '10件组合装', 49.00, 400);

INSERT INTO cart_item (user_id, sku_id, quantity) VALUES (2, 1, 1), (2, 4, 2);

INSERT INTO t_order (order_no, user_id, total_amount, pay_amount, status, address_snapshot, pay_time, create_time) VALUES
('ORD20250615001', 2, 4999.00, 4999.00, 'COMPLETED', '{"receiver":"张三","phone":"13800000002","detail":"科技园南路1号"}', '2025-06-10 10:00:00', '2025-06-10 09:55:00'),
('ORD20250615002', 2, 3198.00, 3198.00, 'PAID',       '{"receiver":"张三","phone":"13800000002","detail":"科技园南路1号"}', '2025-06-14 15:30:00', '2025-06-14 15:25:00'),
('ORD20250615003', 3, 5999.00, NULL,    'PENDING_PAY','{"receiver":"李四","phone":"13800000003","detail":"望京街道10号"}', NULL, '2025-06-15 08:00:00');

INSERT INTO order_item (order_id, product_id, sku_id, product_name, sku_name, price, quantity) VALUES
(1, 1, 1, '旗舰智能手机 Pro Max', '黑色 256G', 4999.00, 1),
(2, 16, 32, '空气净化器', 'Pro 版', 1599.00, 2),
(3, 3, 5, '轻薄商务笔记本', '银色 16G+512G', 5999.00, 1);
