-- 数据库迁移脚本：为用户创建的记录添加用户ID关联
-- 执行此脚本前请先备份数据库

-- 1. 投放记录表：添加 user_id 字段
ALTER TABLE stocking_record 
ADD COLUMN user_id INT NULL AFTER id,
ADD INDEX idx_stocking_user_id (user_id),
ADD CONSTRAINT fk_stocking_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL;

-- 根据池塘关联更新现有记录的 user_id
UPDATE stocking_record sr 
JOIN pond p ON sr.pond_id = p.id 
SET sr.user_id = p.user_id 
WHERE sr.user_id IS NULL;

-- 2. 捕捞记录表：添加 user_id 字段
ALTER TABLE harvest 
ADD COLUMN user_id INT NULL AFTER id,
ADD INDEX idx_harvest_user_id (user_id),
ADD CONSTRAINT fk_harvest_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL;

-- 根据池塘关联更新现有记录的 user_id
UPDATE harvest h 
JOIN pond p ON h.pond_id = p.id 
SET h.user_id = p.user_id 
WHERE h.user_id IS NULL;

-- 3. 用药记录表：添加 user_id 字段
ALTER TABLE medication 
ADD COLUMN user_id INT NULL AFTER id,
ADD INDEX idx_medication_user_id (user_id),
ADD CONSTRAINT fk_medication_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL;

-- 根据池塘关联更新现有记录的 user_id
UPDATE medication m 
JOIN pond p ON m.pond_id = p.id 
SET m.user_id = p.user_id 
WHERE m.user_id IS NULL;

-- 4. 水质记录表：添加 user_id 字段
ALTER TABLE water_quality 
ADD COLUMN user_id INT NULL AFTER id,
ADD INDEX idx_water_quality_user_id (user_id),
ADD CONSTRAINT fk_water_quality_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL;

-- 根据池塘关联更新现有记录的 user_id
UPDATE water_quality wq 
JOIN pond p ON wq.pond_id = p.id 
SET wq.user_id = p.user_id 
WHERE wq.user_id IS NULL;

-- 5. 养殖日志表：将 created_by 重命名为 user_id
-- 先添加新字段
ALTER TABLE farming_log 
ADD COLUMN user_id INT NULL AFTER remark;

-- 迁移数据
UPDATE farming_log SET user_id = created_by WHERE user_id IS NULL;

-- 删除旧外键和字段
ALTER TABLE farming_log DROP FOREIGN KEY fk_farming_log_user;
ALTER TABLE farming_log DROP COLUMN created_by;

-- 添加索引和外键
ALTER TABLE farming_log 
ADD INDEX idx_farming_log_user_id (user_id),
ADD CONSTRAINT fk_farming_log_user_new FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL;
