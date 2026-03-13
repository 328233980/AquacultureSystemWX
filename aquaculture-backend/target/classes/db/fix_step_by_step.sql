-- ============================================
-- 分步修复脚本 - 避免重复键错误
-- ============================================

-- ===== 第一步：修复 farming_log 表 =====

-- 1.1 检查 farming_log 表结构
SHOW CREATE TABLE farming_log;

-- 1.2 添加 user_id 字段（如果不存在）
-- 使用存储过程来判断字段是否存在
DELIMITER $$

DROP PROCEDURE IF EXISTS add_user_id_to_farming_log$$

CREATE PROCEDURE add_user_id_to_farming_log()
BEGIN
    DECLARE column_exists INT DEFAULT 0;

    SELECT COUNT(*) INTO column_exists
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'farming_log'
      AND COLUMN_NAME = 'user_id';

    IF column_exists = 0 THEN
        -- 添加字段
        ALTER TABLE farming_log ADD COLUMN user_id INT AFTER id;

        -- 添加索引
        ALTER TABLE farming_log ADD INDEX idx_farming_log_user_id_new (user_id);

        -- 添加外键（使用唯一名称）
        ALTER TABLE farming_log
        ADD CONSTRAINT fk_farming_log_user_id_new
        FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

        -- 更新数据
        UPDATE farming_log fl
        JOIN pond p ON fl.pond_id = p.id
        SET fl.user_id = p.user_id
        WHERE fl.user_id IS NULL;

        SELECT 'farming_log 表修复成功' AS result;
    ELSE
        SELECT 'farming_log 表已存在 user_id 字段' AS result;
    END IF;
END$$

DELIMITER ;

-- 执行存储过程
CALL add_user_id_to_farming_log();

-- ===== 第二步：修复 attachment 表 =====

DELIMITER $$

DROP PROCEDURE IF EXISTS add_user_id_to_attachment$$

CREATE PROCEDURE add_user_id_to_attachment()
BEGIN
    DECLARE column_exists INT DEFAULT 0;

    SELECT COUNT(*) INTO column_exists
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'attachment'
      AND COLUMN_NAME = 'user_id';

    IF column_exists = 0 THEN
        -- 添加字段
        ALTER TABLE attachment ADD COLUMN user_id INT AFTER id;

        -- 添加索引
        ALTER TABLE attachment ADD INDEX idx_attachment_user_id_new (user_id);

        -- 添加外键（使用唯一名称）
        ALTER TABLE attachment
        ADD CONSTRAINT fk_attachment_user_id_new
        FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL;

        -- 根据 upload_by 字段设置 user_id
        UPDATE attachment
        SET user_id = upload_by
        WHERE user_id IS NULL AND upload_by IS NOT NULL;

        SELECT 'attachment 表修复成功' AS result;
    ELSE
        SELECT 'attachment 表已存在 user_id 字段' AS result;
    END IF;
END$$

DELIMITER ;

-- 执行存储过程
CALL add_user_id_to_attachment();

-- ===== 第三步：验证所有表 =====

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

-- ===== 第四步：检查数据完整性 =====

SELECT
    'stocking_record' AS table_name,
    COUNT(*) AS total_records,
    SUM(CASE WHEN user_id IS NULL THEN 1 ELSE 0 END) AS null_user_id_count
FROM stocking_record
UNION ALL
SELECT 'medication', COUNT(*), SUM(CASE WHEN user_id IS NULL THEN 1 ELSE 0 END) FROM medication
UNION ALL
SELECT 'farming_log', COUNT(*), SUM(CASE WHEN user_id IS NULL THEN 1 ELSE 0 END) FROM farming_log
UNION ALL
SELECT 'harvest', COUNT(*), SUM(CASE WHEN user_id IS NULL THEN 1 ELSE 0 END) FROM harvest
UNION ALL
SELECT 'water_quality', COUNT(*), SUM(CASE WHEN user_id IS NULL THEN 1 ELSE 0 END) FROM water_quality
UNION ALL
SELECT 'attachment', COUNT(*), SUM(CASE WHEN user_id IS NULL THEN 1 ELSE 0 END) FROM attachment;

-- ===== 清理存储过程 =====
DROP PROCEDURE IF EXISTS add_user_id_to_farming_log;
DROP PROCEDURE IF EXISTS add_user_id_to_attachment;
