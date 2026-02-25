-- 水产养殖管理系统数据库初始化脚本
-- SQLite

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    openid TEXT UNIQUE NOT NULL,
    unionid TEXT,
    nickname TEXT,
    avatar_url TEXT,
    phone TEXT,
    role TEXT DEFAULT 'farmer',
    status INTEGER DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 池塘表
CREATE TABLE IF NOT EXISTS pond (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    pond_name TEXT NOT NULL,
    pond_type TEXT NOT NULL,
    area REAL,
    depth REAL,
    location TEXT,
    status TEXT DEFAULT 'active',
    remark TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES user(id)
);

-- 投放记录表
CREATE TABLE IF NOT EXISTS stocking_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    pond_id INTEGER NOT NULL,
    stocking_date DATE NOT NULL,
    species TEXT NOT NULL,
    quantity INTEGER NOT NULL,
    unit TEXT DEFAULT 'tail',
    avg_size REAL,
    supplier TEXT,
    cost REAL,
    survival_rate REAL,
    remark TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(pond_id) REFERENCES pond(id)
);

-- 养殖日志表
CREATE TABLE IF NOT EXISTS farming_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    pond_id INTEGER NOT NULL,
    log_date DATE NOT NULL,
    weather TEXT,
    temperature REAL,
    feeding_amount REAL,
    feeding_type TEXT,
    mortality INTEGER DEFAULT 0,
    abnormal_behavior TEXT,
    remark TEXT,
    created_by INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(pond_id) REFERENCES pond(id),
    FOREIGN KEY(created_by) REFERENCES user(id)
);

-- 水质指标表
CREATE TABLE IF NOT EXISTS water_quality (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    farming_log_id INTEGER NOT NULL,
    pond_id INTEGER NOT NULL,
    test_time DATETIME NOT NULL,
    water_temp REAL,
    ph_value REAL,
    dissolved_oxygen REAL,
    ammonia_nitrogen REAL,
    nitrite REAL,
    salinity REAL,
    transparency REAL,
    remark TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(farming_log_id) REFERENCES farming_log(id),
    FOREIGN KEY(pond_id) REFERENCES pond(id)
);

-- 用药记录表
CREATE TABLE IF NOT EXISTS medication (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    pond_id INTEGER NOT NULL,
    medication_date DATE NOT NULL,
    drug_name TEXT NOT NULL,
    drug_type TEXT,
    dosage REAL,
    dosage_unit TEXT,
    purpose TEXT,
    target_disease TEXT,
    withdrawal_period INTEGER,
    withdrawal_end_date DATE,
    operator TEXT,
    remark TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(pond_id) REFERENCES pond(id)
);

-- 捕捞记录表
CREATE TABLE IF NOT EXISTS harvest (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    pond_id INTEGER NOT NULL,
    harvest_date DATE NOT NULL,
    harvest_type TEXT DEFAULT 'full',
    quantity REAL NOT NULL,
    avg_weight REAL,
    total_count INTEGER,
    grade_a REAL,
    grade_b REAL,
    grade_c REAL,
    price_per_kg REAL,
    total_revenue REAL,
    buyer TEXT,
    destination TEXT,
    remark TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(pond_id) REFERENCES pond(id)
);

-- 附件表
CREATE TABLE IF NOT EXISTS attachment (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    related_type TEXT NOT NULL,
    related_id INTEGER NOT NULL,
    file_name TEXT NOT NULL,
    file_path TEXT NOT NULL,
    file_type TEXT,
    file_size INTEGER,
    upload_by INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(upload_by) REFERENCES user(id)
);

-- 待办提醒表
CREATE TABLE IF NOT EXISTS reminder (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    pond_id INTEGER,
    reminder_type TEXT NOT NULL,
    title TEXT NOT NULL,
    content TEXT,
    remind_date DATE NOT NULL,
    status TEXT DEFAULT 'pending',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES user(id),
    FOREIGN KEY(pond_id) REFERENCES pond(id)
);

-- 系统配置表
CREATE TABLE IF NOT EXISTS sys_config (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    config_key TEXT UNIQUE NOT NULL,
    config_value TEXT,
    config_type TEXT,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_pond_user_id ON pond(user_id);
CREATE INDEX IF NOT EXISTS idx_stocking_pond_id ON stocking_record(pond_id);
CREATE INDEX IF NOT EXISTS idx_farming_log_pond_id ON farming_log(pond_id);
CREATE INDEX IF NOT EXISTS idx_farming_log_date ON farming_log(log_date);
CREATE INDEX IF NOT EXISTS idx_water_quality_log_id ON water_quality(farming_log_id);
CREATE INDEX IF NOT EXISTS idx_medication_pond_id ON medication(pond_id);
CREATE INDEX IF NOT EXISTS idx_medication_date ON medication(medication_date);
CREATE INDEX IF NOT EXISTS idx_harvest_pond_id ON harvest(pond_id);
CREATE INDEX IF NOT EXISTS idx_attachment_related ON attachment(related_type, related_id);
CREATE INDEX IF NOT EXISTS idx_reminder_user_id ON reminder(user_id);
CREATE INDEX IF NOT EXISTS idx_reminder_date ON reminder(remind_date);
