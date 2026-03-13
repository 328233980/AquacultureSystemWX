# 数据库修复说明

## 问题描述

错误信息：`Unknown column 'user_id' in 'where clause'`

**根本原因**: 数据库表 `stocking_record`（以及其他几个业务表）缺少 `user_id` 字段，但代码中的SQL查询试图使用该字段进行筛选。

## 解决方案

### 方法1：执行数据库迁移脚本（推荐）

1. **连接到MySQL数据库**
   ```bash
   mysql -h sh-cynosdbmysql-grp-jfavuxao.sql.tencentcdb.com -P 24565 -u zhengzj -p
   # 输入密码: Aa62770212
   ```

2. **选择数据库**
   ```sql
   USE aquaculture;
   ```

3. **执行迁移脚本**
   ```sql
   -- 复制 migration_add_user_id.sql 中的内容并执行
   source d:/aiCode2/aquaculture-backend/src/main/resources/db/migration_add_user_id.sql
   ```

### 方法2：手动执行SQL语句

如果无法直接执行脚本文件，可以手动执行以下SQL：

```sql
-- 1. 为 stocking_record 表添加 user_id 字段
ALTER TABLE stocking_record
ADD COLUMN user_id INT AFTER id;

-- 添加索引和外键
ALTER TABLE stocking_record
ADD INDEX idx_stocking_user_id (user_id),
ADD CONSTRAINT fk_stocking_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

-- 更新现有数据：从 pond 表获取 user_id
UPDATE stocking_record sr
JOIN pond p ON sr.pond_id = p.id
SET sr.user_id = p.user_id
WHERE sr.user_id IS NULL;

-- 2. 为 medication 表添加 user_id 字段
ALTER TABLE medication
ADD COLUMN user_id INT AFTER id;

ALTER TABLE medication
ADD INDEX idx_medication_user_id (user_id),
ADD CONSTRAINT fk_medication_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

UPDATE medication m
JOIN pond p ON m.pond_id = p.id
SET m.user_id = p.user_id
WHERE m.user_id IS NULL;

-- 3. 为 farming_log 表添加 user_id 字段
ALTER TABLE farming_log
ADD COLUMN user_id INT AFTER id;

ALTER TABLE farming_log
ADD INDEX idx_farming_log_user_id (user_id),
ADD CONSTRAINT fk_farming_log_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

UPDATE farming_log fl
JOIN pond p ON fl.pond_id = p.id
SET fl.user_id = p.user_id
WHERE fl.user_id IS NULL;

-- 4. 为 harvest 表添加 user_id 字段
ALTER TABLE harvest
ADD COLUMN user_id INT AFTER id;

ALTER TABLE harvest
ADD INDEX idx_harvest_user_id (user_id),
ADD CONSTRAINT fk_harvest_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

UPDATE harvest h
JOIN pond p ON h.pond_id = p.id
SET h.user_id = p.user_id
WHERE h.user_id IS NULL;

-- 5. 为 water_quality 表添加 user_id 字段
ALTER TABLE water_quality
ADD COLUMN user_id INT AFTER id;

ALTER TABLE water_quality
ADD INDEX idx_water_quality_user_id (user_id),
ADD CONSTRAINT fk_water_quality_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;

UPDATE water_quality wq
JOIN pond p ON wq.pond_id = p.id
SET wq.user_id = p.user_id
WHERE wq.user_id IS NULL;
```

### 方法3：使用数据库管理工具

如果您使用 Navicat、DBeaver 或其他数据库管理工具：

1. 连接到数据库
2. 打开查询窗口
3. 复制粘贴上面的SQL语句
4. 执行所有语句

## 验证修复

执行以下SQL验证迁移是否成功：

```sql
-- 检查各表是否还有 NULL 的 user_id
SELECT 'stocking_record' AS table_name, COUNT(*) AS records_without_user_id
FROM stocking_record WHERE user_id IS NULL
UNION ALL
SELECT 'medication', COUNT(*) FROM medication WHERE user_id IS NULL
UNION ALL
SELECT 'farming_log', COUNT(*) FROM farming_log WHERE user_id IS NULL
UNION ALL
SELECT 'harvest', COUNT(*) FROM harvest WHERE user_id IS NULL;
```

所有结果应该为 0。

## 重启服务

修复数据库后，需要重启后端服务：

```bash
cd d:/aiCode2/aquaculture-backend
mvn spring-boot:run
```

或者如果您使用 jar 包运行：

```bash
java -jar target/aquaculture-backend-1.0.0.jar
```

## 预期结果

修复后：
1. 500 错误应该消失
2. 支出数据可以正常加载
3. 所有API调用正常工作

## 注意事项

1. **备份数据**：执行ALTER TABLE前建议先备份数据库
2. **停机维护**：大量数据更新可能需要短暂停机
3. **测试验证**：修复后在测试环境验证所有功能

## 快速修复命令

如果您使用腾讯云数据库，可以在云控制台执行SQL，或者：

```bash
# Windows 用户
mysql -h sh-cynosdbmysql-grp-jfavuxao.sql.tencentcdb.com -P 24565 -u zhengzj -pAa62770212 aquaculture < d:/aiCode2/aquaculture-backend/src/main/resources/db/migration_add_user_id.sql
```
