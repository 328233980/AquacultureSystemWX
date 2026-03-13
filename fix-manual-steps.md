# 手动修复剩余表的步骤

## 问题分析

执行结果中，以下表出现错误：

### ❌ 失败的表
1. **farming_log** - 错误: `duplicate key in table`
2. **attachment** - 错误: `duplicate key in table`

### ✅ 成功的表
1. **stocking_record** - 已成功添加 user_id
2. **medication** - 已成功添加 user_id
3. **harvest** - 已成功添加 user_id
4. **water_quality** - 已成功添加 user_id

---

## 修复方案

### 方法1：手动逐步执行（推荐）

请按顺序执行以下SQL语句：

#### 第一步：检查 farming_log 表结构

```sql
-- 查看 farming_log 表的创建语句
SHOW CREATE TABLE farming_log;
```

#### 第二步：为 farming_log 添加 user_id 字段

```sql
-- 2.1 添加字段
ALTER TABLE farming_log
ADD COLUMN user_id INT AFTER id;
```

如果提示字段已存在，跳过此步骤。

```sql
-- 2.2 添加索引
ALTER TABLE farming_log
ADD INDEX idx_farming_log_user_id (user_id);
```

如果提示索引已存在，跳过此步骤。

```sql
-- 2.3 添加外键约束（使用新的约束名称）
ALTER TABLE farming_log
ADD CONSTRAINT fk_farming_log_user_new
FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;
```

如果提示约束名冲突，尝试：
```sql
ALTER TABLE farming_log
ADD CONSTRAINT fk_fl_user_id
FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;
```

```sql
-- 2.4 更新数据
UPDATE farming_log fl
JOIN pond p ON fl.pond_id = p.id
SET fl.user_id = p.user_id
WHERE fl.user_id IS NULL;
```

#### 第三步：为 attachment 表添加 user_id 字段

```sql
-- 3.1 添加字段
ALTER TABLE attachment
ADD COLUMN user_id INT AFTER id;
```

如果提示字段已存在，跳过此步骤。

```sql
-- 3.2 添加索引
ALTER TABLE attachment
ADD INDEX idx_attachment_user_id (user_id);
```

```sql
-- 3.3 添加外键约束（使用新的约束名称）
ALTER TABLE attachment
ADD CONSTRAINT fk_attachment_user_new
FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL;
```

```sql
-- 3.4 根据 upload_by 字段设置 user_id
UPDATE attachment
SET user_id = upload_by
WHERE user_id IS NULL AND upload_by IS NOT NULL;
```

#### 第四步：验证所有表

```sql
-- 检查所有表的 user_id 字段
SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND COLUMN_NAME = 'user_id'
  AND TABLE_NAME IN ('stocking_record', 'medication', 'farming_log', 'harvest', 'water_quality', 'attachment')
ORDER BY TABLE_NAME;
```

```sql
-- 检查数据完整性
SELECT 'stocking_record' AS tbl, COUNT(*) AS total, SUM(CASE WHEN user_id IS NULL THEN 1 ELSE 0 END) AS null_count
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
```

---

## 方法2：使用存储过程（如果支持）

如果数据库支持存储过程，可以执行 `fix_step_by_step.sql` 文件：

```sql
source d:/aiCode2/aquaculture-backend/src/main/resources/db/fix_step_by_step.sql
```

---

## 常见错误处理

### 错误1：字段已存在
```
ERROR 1060 (42S21): Duplicate column name 'user_id'
```
**解决**: 字段已存在，跳过添加字段的步骤，继续执行后续步骤。

### 错误2：索引已存在
```
ERROR 1061 (42000): Duplicate key name 'idx_farming_log_user_id'
```
**解决**: 使用不同的索引名称：
```sql
ALTER TABLE farming_log ADD INDEX idx_fl_user_id (user_id);
```

### 错误3：外键约束已存在
```
ERROR 1022 (23000): Can't write; duplicate key in table
```
**解决**: 使用不同的约束名称：
```sql
ALTER TABLE farming_log
ADD CONSTRAINT fk_fl_user_new
FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;
```

---

## 验证成功的标志

执行完所有步骤后，应该满足：

1. ✅ 所有6个表都有 `user_id` 字段
2. ✅ `null_count` 列显示大部分为 0（attachment 表可能有些为 NULL，这是正常的）
3. ✅ 后端重启后不再出现 `Unknown column 'user_id'` 错误

---

## 执行建议

1. **按顺序执行**: 一条一条执行SQL，观察结果
2. **忽略已存在的错误**: 如果提示字段/索引已存在，继续下一步
3. **灵活调整约束名称**: 如果约束名称冲突，使用新的名称
4. **最后验证**: 执行完所有步骤后，运行验证SQL确认

---

## 重启服务

修复完成后，重启后端服务：

```bash
cd d:/aiCode2/aquaculture-backend
mvn spring-boot:run
```

查看日志确认没有错误。
