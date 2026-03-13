-- 数据库迁移脚本：为相关表添加 user_id 字段
-- 执行日期：2026-03-13
-- 目的：修复 Unknown column 'user_id' 错误

-- 1. 为 stocking_record 表添加 user_id 字段
ALTER TABLE stocking_record 
ADD COLUMN user_id INT AFTER id,
ADD INDEX idx_stocking_user_id (user_id),
ADD CONSTRAINT fk_stocking_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- 更新现有数据：从 pond 表获取 user_id
UPDATE stocking_record sr
JOIN pond p ON sr.pond_id = p.id
SET sr.user_id = p.user_id
WHERE sr.user_id IS NULL;

-- 2. 为 medication 表添加 user_id 字段（如果尚未添加）
ALTER TABLE medication 
ADD COLUMN user_id INT AFTER id,
ADD INDEX idx_medication_user_id (user_id),
ADD CONSTRAINT fk_medication_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- 更新现有数据：从 pond 表获取 user_id
UPDATE medication m
JOIN pond p ON m.pond_id = p.id
SET m.user_id = p.user_id
WHERE m.user_id IS NULL;

-- 3. 为 farming_log 表添加 user_id 字段（如果尚未添加）
ALTER TABLE farming_log 
ADD COLUMN user_id INT AFTER id,
ADD INDEX idx_farming_log_user_id (user_id),
ADD CONSTRAINT fk_farming_log_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- 更新现有数据：从 pond 表获取 user_id
UPDATE farming_log fl
JOIN pond p ON fl.pond_id = p.id
SET fl.user_id = p.user_id
WHERE fl.user_id IS NULL;

-- 4. 为 harvest 表添加 user_id 字段（如果尚未添加）
ALTER TABLE harvest 
ADD COLUMN user_id INT AFTER id,
ADD INDEX idx_harvest_user_id (user_id),
ADD CONSTRAINT fk_harvest_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- 更新现有数据：从 pond 表获取 user_id
UPDATE harvest h
JOIN pond p ON h.pond_id = p.id
SET h.user_id = p.user_id
WHERE h.user_id IS NULL;

-- 5. 为 water_quality 表添加 user_id 字段（如果尚未添加）
ALTER TABLE water_quality 
ADD COLUMN user_id INT AFTER id,
ADD INDEX idx_water_quality_user_id (user_id),
ADD CONSTRAINT fk_water_quality_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- 更新现有数据：从 pond 表获取 user_id
UPDATE water_quality wq
JOIN pond p ON wq.pond_id = p.id
SET wq.user_id = p.user_id
WHERE wq.user_id IS NULL;

-- 6. 为 attachment 表添加 user_id 字段（如果尚未添加）
ALTER TABLE attachment 
ADD COLUMN user_id INT AFTER id,
ADD INDEX idx_attachment_user_id (user_id),
ADD CONSTRAINT fk_attachment_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- 验证迁移结果
SELECT 'stocking_record' AS table_name, COUNT(*) AS records_without_user_id FROM stocking_record WHERE user_id IS NULL
UNION ALL
SELECT 'medication', COUNT(*) FROM medication WHERE user_id IS NULL
UNION ALL
SELECT 'farming_log', COUNT(*) FROM farming_log WHERE user_id IS NULL
UNION ALL
SELECT 'harvest', COUNT(*) FROM harvest WHERE user_id IS NULL
UNION ALL
SELECT 'water_quality', COUNT(*) FROM water_quality WHERE user_id IS NULL;
