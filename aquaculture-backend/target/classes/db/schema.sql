-- 水产养殖管理系统数据库初始化脚本
-- MySQL 8.0+

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    openid VARCHAR(128) UNIQUE NOT NULL,
    unionid VARCHAR(128),
    nickname VARCHAR(100),
    avatar_url VARCHAR(500),
    phone VARCHAR(20),
    role VARCHAR(20) DEFAULT 'farmer',
    status INT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_openid (openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 池塘表
CREATE TABLE IF NOT EXISTS pond (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    pond_name VARCHAR(100) NOT NULL,
    pond_type VARCHAR(50) NOT NULL,
    area DECIMAL(10,2),
    depth DECIMAL(10,2),
    location VARCHAR(200),
    status VARCHAR(20) DEFAULT 'active',
    remark TEXT,
    cycle_days INT,
    density INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_pond_user_id (user_id),
    CONSTRAINT fk_pond_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='池塘表';

-- 投放记录表
CREATE TABLE IF NOT EXISTS stocking_record (
    id INT PRIMARY KEY AUTO_INCREMENT,
    pond_id INT NOT NULL,
    stocking_date DATE NOT NULL,
    species VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    unit VARCHAR(20) DEFAULT 'tail',
    avg_size DECIMAL(10,2),
    supplier VARCHAR(100),
    cost DECIMAL(12,2),
    survival_rate DECIMAL(5,2),
    remark TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_stocking_pond_id (pond_id),
    INDEX idx_stocking_date (stocking_date),
    CONSTRAINT fk_stocking_pond FOREIGN KEY (pond_id) REFERENCES pond(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投放记录表';

-- 养殖日志表
CREATE TABLE IF NOT EXISTS farming_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    pond_id INT NOT NULL,
    log_date DATE NOT NULL,
    weather VARCHAR(50),
    temperature DECIMAL(5,2),
    feeding_amount DECIMAL(10,2),
    feeding_type VARCHAR(100),
    feed_cost DECIMAL(12,2),
    mortality INT DEFAULT 0,
    abnormal_behavior TEXT,
    remark TEXT,
    created_by INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_farming_log_pond_id (pond_id),
    INDEX idx_farming_log_date (log_date),
    CONSTRAINT fk_farming_log_pond FOREIGN KEY (pond_id) REFERENCES pond(id) ON DELETE CASCADE,
    CONSTRAINT fk_farming_log_user FOREIGN KEY (created_by) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='养殖日志表';

-- 水质指标表
CREATE TABLE IF NOT EXISTS water_quality (
    id INT PRIMARY KEY AUTO_INCREMENT,
    farming_log_id INT NOT NULL,
    pond_id INT NOT NULL,
    test_time DATETIME NOT NULL,
    water_temp DECIMAL(5,2),
    ph_value DECIMAL(5,2),
    dissolved_oxygen DECIMAL(10,2),
    ammonia_nitrogen DECIMAL(10,4),
    nitrite DECIMAL(10,4),
    salinity DECIMAL(10,2),
    transparency DECIMAL(10,2),
    remark TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_water_quality_log_id (farming_log_id),
    INDEX idx_water_quality_pond_id (pond_id),
    CONSTRAINT fk_water_quality_log FOREIGN KEY (farming_log_id) REFERENCES farming_log(id) ON DELETE CASCADE,
    CONSTRAINT fk_water_quality_pond FOREIGN KEY (pond_id) REFERENCES pond(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='水质指标表';

-- 用药记录表
CREATE TABLE IF NOT EXISTS medication (
    id INT PRIMARY KEY AUTO_INCREMENT,
    pond_id INT NOT NULL,
    medication_date DATE NOT NULL,
    drug_name VARCHAR(100) NOT NULL,
    drug_type VARCHAR(50),
    dosage DECIMAL(10,2),
    dosage_unit VARCHAR(20),
    cost DECIMAL(12,2),
    purpose TEXT,
    target_disease VARCHAR(200),
    withdrawal_period INT,
    withdrawal_end_date DATE,
    operator VARCHAR(50),
    remark TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_medication_pond_id (pond_id),
    INDEX idx_medication_date (medication_date),
    CONSTRAINT fk_medication_pond FOREIGN KEY (pond_id) REFERENCES pond(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用药记录表';

-- 捕捞记录表
CREATE TABLE IF NOT EXISTS harvest (
    id INT PRIMARY KEY AUTO_INCREMENT,
    pond_id INT NOT NULL,
    harvest_date DATE NOT NULL,
    harvest_type VARCHAR(20) DEFAULT 'full',
    quantity DECIMAL(12,2) NOT NULL,
    avg_weight DECIMAL(10,2),
    total_count INT,
    grade_a DECIMAL(12,2),
    grade_b DECIMAL(12,2),
    grade_c DECIMAL(12,2),
    price_per_kg DECIMAL(10,2),
    total_revenue DECIMAL(14,2),
    buyer VARCHAR(100),
    destination VARCHAR(200),
    mortality INT,
    remark TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_harvest_pond_id (pond_id),
    INDEX idx_harvest_date (harvest_date),
    CONSTRAINT fk_harvest_pond FOREIGN KEY (pond_id) REFERENCES pond(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='捕捞记录表';

-- 附件表
CREATE TABLE IF NOT EXISTS attachment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    related_type VARCHAR(50) NOT NULL,
    related_id INT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(50),
    file_size INT,
    upload_by INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_attachment_related (related_type, related_id),
    CONSTRAINT fk_attachment_user FOREIGN KEY (upload_by) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='附件表';

-- 待办提醒表
CREATE TABLE IF NOT EXISTS reminder (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    pond_id INT,
    reminder_type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    remind_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_reminder_user_id (user_id),
    INDEX idx_reminder_date (remind_date),
    CONSTRAINT fk_reminder_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_reminder_pond FOREIGN KEY (pond_id) REFERENCES pond(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='待办提醒表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS sys_config (
    id INT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT,
    config_type VARCHAR(50),
    description VARCHAR(500),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX idx_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 设备表
CREATE TABLE IF NOT EXISTS equipment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    pond_id INT,
    pond_name VARCHAR(100),
    name VARCHAR(100) NOT NULL,
    original_value DECIMAL(12,2),
    monthly_depreciation DECIMAL(10,2),
    purchase_date DATE,
    remark TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_equipment_user_id (user_id),
    INDEX idx_equipment_pond_id (pond_id),
    CONSTRAINT fk_equipment_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_equipment_pond FOREIGN KEY (pond_id) REFERENCES pond(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备表';

-- 供应商表
CREATE TABLE IF NOT EXISTS supplier (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    supply_types VARCHAR(200),
    address VARCHAR(300),
    remark TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_supplier_user_id (user_id),
    CONSTRAINT fk_supplier_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='供应商表';

-- 种苗配置表
CREATE TABLE IF NOT EXISTS seedling (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(100),
    species VARCHAR(100),
    supplier VARCHAR(100),
    default_price DECIMAL(10,2),
    feeding_cycle INT,
    remark TEXT,
    category VARCHAR(50),
    avg_weight DECIMAL(10,2),
    temp_min DECIMAL(5,2),
    temp_max DECIMAL(5,2),
    ph_min DECIMAL(5,2),
    ph_max DECIMAL(5,2),
    do_min DECIMAL(5,2),
    do_max DECIMAL(5,2),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_seedling_user_id (user_id),
    CONSTRAINT fk_seedling_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='种苗配置表';

-- 药品配置表
CREATE TABLE IF NOT EXISTS drug (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(100),
    drug_type VARCHAR(50),
    unit VARCHAR(20),
    default_price DECIMAL(10,2),
    target_disease VARCHAR(200),
    withdrawal_period INT,
    usage_desc TEXT,
    remark TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_drug_user_id (user_id),
    CONSTRAINT fk_drug_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='药品配置表';

-- 客户配置表
CREATE TABLE IF NOT EXISTS customer (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(300),
    remark TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer_user_id (user_id),
    CONSTRAINT fk_customer_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户配置表';

-- 支出记录表
CREATE TABLE IF NOT EXISTS expense (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    category VARCHAR(50),
    category_label VARCHAR(100),
    amount DECIMAL(12,2),
    expense_date DATE,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_expense_user_id (user_id),
    INDEX idx_expense_date (expense_date),
    CONSTRAINT fk_expense_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支出记录表';
