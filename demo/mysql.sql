-- Electric Equipment Procurement Platform
-- Baseline: MySQL 8.0

CREATE DATABASE IF NOT EXISTS `electric_equipment_platform`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE `electric_equipment_platform`;
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `delivery_address` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `user_id` bigint NOT NULL COMMENT 'Procurement user ID',
    `consignee` varchar(50) DEFAULT NULL COMMENT 'Recipient',
    `sex` varchar(2) DEFAULT NULL COMMENT 'Gender',
    `phone` varchar(20) NOT NULL COMMENT 'Phone number',
    `province_code` varchar(12) DEFAULT NULL COMMENT 'Province code',
    `province_name` varchar(32) DEFAULT NULL COMMENT 'Province name',
    `city_code` varchar(12) DEFAULT NULL COMMENT 'City code',
    `city_name` varchar(32) DEFAULT NULL COMMENT 'City name',
    `district_code` varchar(12) DEFAULT NULL COMMENT 'District code',
    `district_name` varchar(32) DEFAULT NULL COMMENT 'District name',
    `detail` varchar(200) DEFAULT NULL COMMENT 'Detailed address',
    `label` varchar(100) DEFAULT NULL COMMENT 'Address label',
    `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Default address flag',
    PRIMARY KEY (`id`),
    KEY `idx_delivery_address_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Delivery addresses';

CREATE TABLE IF NOT EXISTS `category` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `type` int NOT NULL COMMENT '1 equipment category, 2 equipment package category',
    `name` varchar(64) NOT NULL COMMENT 'Category name',
    `sort` int NOT NULL DEFAULT '0' COMMENT 'Display order',
    `status` int NOT NULL DEFAULT '1' COMMENT '0 disabled, 1 enabled',
    `create_time` datetime DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `create_user` bigint DEFAULT NULL,
    `update_user` bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_type_name` (`type`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Equipment and package categories';

CREATE TABLE IF NOT EXISTS `equipment` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `code` varchar(64) NOT NULL COMMENT 'Equipment or SKU code',
    `name` varchar(128) NOT NULL COMMENT 'Equipment name',
    `category_id` bigint NOT NULL COMMENT 'Equipment category ID',
    `brand` varchar(64) DEFAULT NULL COMMENT 'Brand',
    `model` varchar(128) DEFAULT NULL COMMENT 'Model',
    `unit` varchar(32) DEFAULT NULL COMMENT 'Pricing unit',
    `price` decimal(18,2) NOT NULL DEFAULT '0.00' COMMENT 'Unit price',
    `stock_quantity` int NOT NULL DEFAULT '0' COMMENT 'Available quantity',
    `image` varchar(500) DEFAULT NULL COMMENT 'Primary image URL or object key',
    `description` varchar(1000) DEFAULT NULL COMMENT 'Description',
    `status` int NOT NULL DEFAULT '1' COMMENT '0 offline, 1 online',
    `create_time` datetime DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `create_user` bigint DEFAULT NULL,
    `update_user` bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_equipment_code` (`code`),
    KEY `idx_equipment_category` (`category_id`),
    KEY `idx_equipment_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Electrical equipment';

CREATE TABLE IF NOT EXISTS `equipment_spec` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `equipment_id` bigint NOT NULL COMMENT 'Equipment ID',
    `name` varchar(64) NOT NULL COMMENT 'Specification name',
    `value` varchar(500) DEFAULT NULL COMMENT 'Specification value',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_equipment_spec` (`equipment_id`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Equipment specifications';

CREATE TABLE IF NOT EXISTS `admin_operator` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `name` varchar(64) NOT NULL COMMENT 'Display name',
    `username` varchar(64) NOT NULL COMMENT 'Login name',
    `password` varchar(255) NOT NULL COMMENT 'PBKDF2 password hash',
    `phone` varchar(20) NOT NULL COMMENT 'Phone number',
    `sex` varchar(2) DEFAULT NULL COMMENT 'Gender',
    `id_number` varchar(32) DEFAULT NULL COMMENT 'Identity number',
    `status` int NOT NULL DEFAULT '1' COMMENT '0 disabled, 1 enabled',
    `create_time` datetime DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `create_user` bigint DEFAULT NULL,
    `update_user` bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_admin_operator_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Platform operators';

CREATE TABLE IF NOT EXISTS `orders` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `number` varchar(64) NOT NULL COMMENT 'Purchase order number',
    `status` int NOT NULL DEFAULT '1'
        COMMENT '1 pending payment, 2 pending confirmation, 3 confirmed, 4 shipped, 5 completed, 6 cancelled, 7 refunded',
    `user_id` bigint NOT NULL COMMENT 'Procurement user ID',
    `delivery_address_id` bigint NOT NULL COMMENT 'Delivery address ID',
    `order_time` datetime NOT NULL,
    `checkout_time` datetime DEFAULT NULL,
    `pay_method` int NOT NULL DEFAULT '1' COMMENT '1 offline transfer, 2 online payment reserved',
    `pay_status` tinyint NOT NULL DEFAULT '0' COMMENT '0 unpaid, 1 paid, 2 refunded',
    `amount` decimal(18,2) NOT NULL COMMENT 'Order amount',
    `remark` varchar(500) DEFAULT NULL,
    `phone` varchar(20) DEFAULT NULL,
    `address` varchar(500) DEFAULT NULL,
    `user_name` varchar(64) DEFAULT NULL,
    `consignee` varchar(64) DEFAULT NULL,
    `cancel_reason` varchar(500) DEFAULT NULL,
    `rejection_reason` varchar(500) DEFAULT NULL,
    `cancel_time` datetime DEFAULT NULL,
    `estimated_delivery_time` datetime DEFAULT NULL,
    `delivery_status` tinyint NOT NULL DEFAULT '1' COMMENT '0 scheduled, 1 as soon as possible',
    `delivery_time` datetime DEFAULT NULL,
    `service_fee` decimal(18,2) NOT NULL DEFAULT '0.00' COMMENT 'Delivery and handling fee',
    `installation_quantity` int NOT NULL DEFAULT '0' COMMENT 'Quantity requiring installation',
    `installation_status` tinyint NOT NULL DEFAULT '0' COMMENT '0 not required, 1 required',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_orders_number` (`number`),
    KEY `idx_orders_user_time` (`user_id`, `order_time`),
    KEY `idx_orders_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Purchase orders';

CREATE TABLE IF NOT EXISTS `order_detail` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `name` varchar(128) DEFAULT NULL COMMENT 'Equipment snapshot name',
    `image` varchar(500) DEFAULT NULL COMMENT 'Equipment snapshot image',
    `order_id` bigint NOT NULL COMMENT 'Order ID',
    `equipment_id` bigint DEFAULT NULL COMMENT 'Equipment ID',
    `package_id` bigint DEFAULT NULL COMMENT 'Equipment package ID',
    `selected_spec` varchar(500) DEFAULT NULL COMMENT 'Selected specification snapshot',
    `number` int NOT NULL DEFAULT '1' COMMENT 'Quantity',
    `amount` decimal(18,2) NOT NULL COMMENT 'Unit price snapshot',
    PRIMARY KEY (`id`),
    KEY `idx_order_detail_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Order details';

CREATE TABLE IF NOT EXISTS `equipment_package` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `category_id` bigint NOT NULL COMMENT 'Package category ID',
    `name` varchar(128) NOT NULL COMMENT 'Package name',
    `price` decimal(18,2) NOT NULL COMMENT 'Package price',
    `status` int NOT NULL DEFAULT '1' COMMENT '0 offline, 1 online',
    `description` varchar(1000) DEFAULT NULL,
    `image` varchar(500) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `create_user` bigint DEFAULT NULL,
    `update_user` bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_equipment_package_name` (`name`),
    KEY `idx_equipment_package_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Equipment packages';

CREATE TABLE IF NOT EXISTS `package_equipment` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `package_id` bigint NOT NULL COMMENT 'Equipment package ID',
    `equipment_id` bigint NOT NULL COMMENT 'Equipment ID',
    `name` varchar(128) DEFAULT NULL COMMENT 'Equipment name snapshot',
    `price` decimal(18,2) DEFAULT NULL COMMENT 'Equipment reference price',
    `quantity` int NOT NULL DEFAULT '1' COMMENT 'Quantity in package',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_package_equipment` (`package_id`, `equipment_id`),
    KEY `idx_package_equipment_equipment` (`equipment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Equipment package composition';

CREATE TABLE IF NOT EXISTS `shopping_cart` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `name` varchar(128) DEFAULT NULL,
    `image` varchar(500) DEFAULT NULL,
    `user_id` bigint NOT NULL,
    `equipment_id` bigint DEFAULT NULL,
    `package_id` bigint DEFAULT NULL,
    `selected_spec` varchar(500) DEFAULT NULL,
    `number` int NOT NULL DEFAULT '1',
    `amount` decimal(18,2) NOT NULL,
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_shopping_cart_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Shopping carts';

CREATE TABLE IF NOT EXISTS `user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `openid` varchar(64) DEFAULT NULL COMMENT 'WeChat openid when mini-program login is enabled',
    `username` varchar(64) DEFAULT NULL COMMENT 'Local H5 login name',
    `password` varchar(255) DEFAULT NULL COMMENT 'PBKDF2 password hash',
    `name` varchar(64) DEFAULT NULL,
    `phone` varchar(20) DEFAULT NULL,
    `sex` varchar(2) DEFAULT NULL,
    `id_number` varchar(32) DEFAULT NULL,
    `avatar` varchar(500) DEFAULT NULL,
    `status` int NOT NULL DEFAULT '1' COMMENT '0 disabled, 1 enabled',
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_openid` (`openid`),
    UNIQUE KEY `uk_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Procurement users';

-- Demo administrator: admin / 123456
-- Change the password immediately after first login in any shared environment.
INSERT IGNORE INTO `admin_operator`
    (`id`, `name`, `username`, `password`, `phone`, `sex`, `id_number`, `status`, `create_time`, `update_time`)
VALUES
    (1, '平台管理员', 'admin',
     'pbkdf2_sha256$120000$ZWxlY3RyaWMtZGVtby0wMQ==$7yOljiuUf4h9iDyRMPR3astxFT2hzK/pRgTrqj4NldU=',
     '13800000000', '男', '110101199001011234', 1, NOW(), NOW());

INSERT IGNORE INTO `category`
    (`id`, `type`, `name`, `sort`, `status`, `create_time`, `update_time`)
VALUES
    (1, 1, '配电设备', 1, 1, NOW(), NOW()),
    (2, 1, '电缆与附件', 2, 1, NOW(), NOW()),
    (3, 1, '监测与保护', 3, 1, NOW(), NOW()),
    (4, 2, '成套设备方案', 1, 1, NOW(), NOW());

INSERT IGNORE INTO `equipment`
    (`id`, `code`, `name`, `category_id`, `brand`, `model`, `unit`, `price`,
     `stock_quantity`, `image`, `description`, `status`, `create_time`, `update_time`)
VALUES
    (1, 'EQ-DG-001', '10kV真空断路器', 1, '示例电气', 'ZW32-12', '台',
     12800.00, 12, NULL, '户外柱上真空断路器，适用于10kV配电线路。', 1, NOW(), NOW()),
    (2, 'EQ-CB-001', 'YJV22铜芯电力电缆', 2, '示例电缆', 'YJV22 3x120', '米',
     168.50, 2000, NULL, '额定电压0.6/1kV，适用于配电线路敷设。', 1, NOW(), NOW()),
    (3, 'EQ-MP-001', '智能接地监测装置', 3, '示例科技', 'GM-200', '套',
     3680.00, 30, NULL, '支持接地状态监测和异常告警。', 1, NOW(), NOW());

INSERT IGNORE INTO `equipment_spec`
    (`id`, `equipment_id`, `name`, `value`)
VALUES
    (1, 1, '额定电压', '10kV'),
    (2, 1, '额定电流', '630A'),
    (3, 2, '导体材质', '铜'),
    (4, 2, '额定电压', '0.6/1kV'),
    (5, 3, '监测通道', '8路'),
    (6, 3, '通信方式', 'RS485');

INSERT IGNORE INTO `equipment_package`
    (`id`, `category_id`, `name`, `price`, `status`, `description`, `image`, `create_time`, `update_time`)
VALUES
    (1, 4, '小型配电工程标准包', 28600.00, 1,
     '包含断路器、监测装置和配套附件，适用于标准化小型配电工程。', NULL, NOW(), NOW());

INSERT IGNORE INTO `package_equipment`
    (`id`, `package_id`, `equipment_id`, `name`, `price`, `quantity`)
VALUES
    (1, 1, 1, '10kV真空断路器', 12800.00, 1),
    (2, 1, 3, '智能接地监测装置', 3680.00, 1);

-- Demo buyer account: buyer / 123456
INSERT IGNORE INTO `user`
    (`id`, `username`, `password`, `name`, `phone`, `sex`, `status`, `create_time`)
VALUES
    (1, 'buyer',
     'pbkdf2_sha256$120000$ZWxlY3RyaWMtZGVtby0wMQ==$7yOljiuUf4h9iDyRMPR3astxFT2hzK/pRgTrqj4NldU=',
     '企业采购员', '13900000000', '男', 1, NOW());

INSERT IGNORE INTO `delivery_address`
    (`id`, `user_id`, `consignee`, `sex`, `phone`, `province_name`, `city_name`,
     `district_name`, `detail`, `label`, `is_default`)
VALUES
    (1, 1, '张工', '男', '13900000000', '上海市', '上海市', '浦东新区',
     '张江路88号示例工业园3号楼', '工程项目', 1);
