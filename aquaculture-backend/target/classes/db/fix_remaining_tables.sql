-- 修复剩余表的 user_id 字段
-- 针对 farming_log 和 attachment 表的特殊处理

-- ============================================
-- 1. 修复 farming_log 表
-- ============================================

-- 1.1 先检查是否已有 user_id 字段
SELECT COUNT(*) AS has_user_id_column
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'farming_log'
  AND COLUMN_NAME = 'user_id';

-- 1.2 如果不存在，则添加（使用不同的外键名称）
ALTER TABLE farming_log
ADD COLUMN user_id INT AFTER id;

-- 1.3 添加索引
ALTER TABLE farming_log
ADD INDEX idx_farming_log_user_id (user_id);

-- 1.4 添加外键约束（使用不同的约束名称）
ALTER TABLE farming_log
ADD CONSTRAINT fk_farming_log_user_new FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- 1.5 更新数据
UPDATE farming_log fl
JOIN pond p ON fl.pond_id = p.id
SET fl.user_id = p.user_id
WHERE fl.user_id IS NULL;

-- ============================================
-- 2. 修复 attachment 表
-- ============================================

-- 2.1 检查是否已有 user_id 字段
SELECT COUNT(*) AS has_user_id_column
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'attachment'
  AND COLUMN_NAME = 'user_id';

-- 2.2 如果不存在，则添加
ALTER TABLE attachment
ADD COLUMN user_id INT AFTER id;

-- 2.3 添加索引
ALTER TABLE attachment
ADD INDEX idx_attachment_user_id (user_id);

-- 2.4 添加外键约束（使用不同的约束名称）
ALTER TABLE attachment
ADD CONSTRAINT fk_attachment_user_new FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL;

-- 2.5 更新数据（attachment 表可能没有 pond_id，需要从其他方式获取 user_id）
-- 暂时设置为 NULL，或者根据 upload_by 字段设置
UPDATE attachment
SET user_id = upload_by
WHERE user_id IS NULL AND upload_by IS NOT NULL;

-- ============================================
-- 3. 验证所有表
-- ============================================

-- 3.1 检查所有表的 user_id 字段
SELECT
    TABLE_NAME,
    COLUMN_NAME,
    IS_NULLABLE,
    COLUMN_TYPE
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND COLUMN_NAME = 'user_id'
  AND TABLE_NAME IN ('stocking_record', 'medication', 'farming_log', 'harvest', 'water_quality', 'attachment')
ORDER BY TABLE_NAME;

-- 3.2 检查是否还有 NULL 的 user_id
SELECT 'stocking_record' AS table_name, COUNT(*) AS records_without_user_id
FROM stocking_record WHERE user_id IS NULL
UNION ALL
SELECT 'medication', COUNT(*) FROM medication WHERE user_id IS NULL
UNION ALL
SELECT 'farming_log', COUNT(*) FROM farming_log WHERE user_id IS NULL
UNION ALL
SELECT 'harvest', COUNT(*) FROM harvest WHERE user_id IS NULL
UNION ALL
SELECT 'water_quality', COUNT(*) FROM water_quality WHERE user_id IS NULL
UNION ALL
SELECT 'attachment', COUNT(*) FROM attachment WHERE user_id IS NULL;
