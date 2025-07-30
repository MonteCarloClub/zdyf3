-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS pudong CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE pudong;

-- 用户表
CREATE TABLE IF NOT EXISTS atp_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL UNIQUE COMMENT '用户名',
    name VARCHAR(100) COMMENT '显示名称',
    password VARCHAR(255) COMMENT '密码哈希',
    user_type VARCHAR(50) COMMENT '用户类型',
    channel VARCHAR(100) COMMENT '渠道',
    public_key TEXT COMMENT '公钥',
    private_key TEXT COMMENT '私钥',
    upk TEXT COMMENT 'UPK',
    apk_map TEXT COMMENT 'APK映射(JSON)',
    ask_map TEXT COMMENT 'ASK映射(JSON)',
    opk_map TEXT COMMENT 'OPK映射(JSON)',
    osk_map TEXT COMMENT 'OSK映射(JSON)',
    egg_alpha TEXT COMMENT 'EggAlpha',
    alpha TEXT COMMENT 'Alpha',
    g_alpha TEXT COMMENT 'GAlpha',
    applied_attr_map TEXT COMMENT '已申请属性映射(JSON)',
    privacy_attr_map TEXT COMMENT '隐私属性映射(JSON)',
    expire_date VARCHAR(50) COMMENT '过期日期',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_name (user_name),
    INDEX idx_user_type (user_type),
    INDEX idx_channel (channel)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 组织表
CREATE TABLE IF NOT EXISTS atp_organizations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    org_name VARCHAR(100) NOT NULL UNIQUE COMMENT '组织名称',
    display_name VARCHAR(200) COMMENT '显示名称',
    description VARCHAR(500) COMMENT '描述',
    org_type VARCHAR(50) COMMENT '组织类型',
    public_key TEXT COMMENT '公钥',
    private_key TEXT COMMENT '私钥',
    upk TEXT COMMENT 'UPK',
    apk_map TEXT COMMENT 'APK映射(JSON)',
    ask_map TEXT COMMENT 'ASK映射(JSON)',
    opk_map TEXT COMMENT 'OPK映射(JSON)',
    osk_map TEXT COMMENT 'OSK映射(JSON)',
    egg_alpha TEXT COMMENT 'EggAlpha',
    alpha TEXT COMMENT 'Alpha',
    g_alpha TEXT COMMENT 'GAlpha',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_org_name (org_name),
    INDEX idx_org_type (org_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织信息表';

-- 密钥存储表（可选，用于存储RSA等其他类型的密钥）
CREATE TABLE IF NOT EXISTS atp_keys (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_name VARCHAR(100) NOT NULL COMMENT '所有者名称',
    owner_type ENUM('USER', 'ORGANIZATION') NOT NULL COMMENT '所有者类型',
    key_type VARCHAR(50) NOT NULL COMMENT '密钥类型(RSA, DABE等)',
    key_name VARCHAR(100) NOT NULL COMMENT '密钥名称',
    public_key TEXT COMMENT '公钥',
    private_key TEXT COMMENT '私钥',
    key_data TEXT COMMENT '其他密钥数据(JSON)',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态',
    expire_date DATETIME COMMENT '过期时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_owner_key (owner_name, owner_type, key_type, key_name),
    INDEX idx_owner (owner_name, owner_type),
    INDEX idx_key_type (key_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='密钥存储表';

-- -- 插入一些测试数据（可选）
-- INSERT IGNORE INTO atp_users (user_name, name, password, user_type, channel) VALUES
-- ('admin', '管理员', MD5('admin123'), 'ADMIN', 'default'),
-- ('test_user', '测试用户', MD5('123456'), 'USER', 'default');
--
-- INSERT IGNORE INTO atp_organizations (org_name, display_name, description, org_type) VALUES
-- ('default_org', '默认组织', '系统默认组织', 'DEFAULT'),
-- ('test_org', '测试组织', '用于测试的组织', 'TEST');