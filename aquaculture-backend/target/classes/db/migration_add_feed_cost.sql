-- 给 farming_log 表添加 feed_cost 字段
-- 执行方式: sqlite3 data/aquaculture.db < src/main/resources/db/migration_add_feed_cost.sql

-- 检查字段是否已存在,如果不存在则添加
ALTER TABLE farming_log ADD COLUMN feed_cost REAL;
