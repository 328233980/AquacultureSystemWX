-- 添加捕捞死亡数量字段
-- SQLite

ALTER TABLE harvest ADD COLUMN mortality INTEGER DEFAULT 0;
