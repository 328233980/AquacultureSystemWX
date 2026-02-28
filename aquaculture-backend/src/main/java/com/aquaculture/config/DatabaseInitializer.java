package com.aquaculture.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Value("${test.mode:false}")
    private boolean testMode;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // 确保data目录存在
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        // 检查是否需要初始化数据库
        boolean needInit = true;
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='user'",
                Integer.class
            );

            if (count != null && count > 0) {
                needInit = false;
                log.info("数据库已初始化，跳过初始化过程");
            }
        } catch (Exception e) {
            log.info("数据库未初始化或表不存在，开始初始化...");
        }

        if (!needInit) {
            // 执行数据库迁移
            runMigrations();
            return;
        }

        // 读取并执行schema.sql
        ClassPathResource resource = new ClassPathResource("db/schema.sql");
        String sql = FileCopyUtils.copyToString(
            new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
        );
        
        // 更安全的SQL语句分割方式
        List<String> statements = parseSqlStatements(sql);
        int successCount = 0;
        int errorCount = 0;
                
        for (String statement : statements) {
            String trimmed = statement.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                try {
                    jdbcTemplate.execute(trimmed);
                    successCount++;
                    log.debug("执行SQL成功: {}", trimmed.length() > 50 ? trimmed.substring(0, 50) + "..." : trimmed);
                } catch (Exception e) {
                    errorCount++;
                    log.warn("执行SQL失败: {} - 错误: {}", trimmed, e.getMessage());
                    // 对于索引创建失败的情况，继续执行其他语句
                    if (!trimmed.toUpperCase().startsWith("CREATE INDEX")) {
                        throw e; // 非索引创建的错误则抛出异常
                    }
                }
            }
        }

        log.info("数据库初始化完成 - 成功: {}, 失败: {}", successCount, errorCount);

        // 测试模式下创建测试用户
        if (testMode) {
            createTestUser();
        }
    }
    
    private void createTestUser() {
        try {
            // 检查测试用户是否存在
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user WHERE id = 1",
                Integer.class
            );
            
            if (count != null && count > 0) {
                log.info("测试用户已存在，跳过创建");
                return;
            }
            
            // 创建测试用户
            String sql = "INSERT INTO user (id, openid, nickname, avatarUrl, role, status, created_at, updated_at) " +
                       "VALUES (1, 'test_openid', '测试用户', 'https://example.com/test-avatar.png', 'farmer', 1, datetime('now'), datetime('now'))";
            jdbcTemplate.execute(sql);
            log.info("测试用户创建成功");
        } catch (Exception e) {
            log.error("创建测试用户失败", e);
        }
    }

    /**
     * 执行数据库迁移
     */
    private void runMigrations() {
        // 迁移1: 给 farming_log 表添加 feed_cost 字段
        addColumnIfNotExists("farming_log", "feed_cost", "REAL");
        // 迁移2: 给 medication 表添加 cost 字段
        addColumnIfNotExists("medication", "cost", "REAL");
        // 迁移3: 给 pond 表添加 cycle_days 字段 (养殖周期)
        addColumnIfNotExists("pond", "cycle_days", "INTEGER");
        // 迁移4: 给 pond 表添加 density 字段 (养殖密度)
        addColumnIfNotExists("pond", "density", "INTEGER");
        // 迁移5: 给 harvest 表添加 mortality 字段 (捕捞死亡数量)
        addColumnIfNotExists("harvest", "mortality", "INTEGER");
        // 迁移6: 创建 equipment 表
        createTableIfNotExists("equipment", 
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "pond_id INTEGER, " +
            "pond_name TEXT, " +
            "name TEXT NOT NULL, " +
            "original_value REAL, " +
            "monthly_depreciation REAL, " +
            "purchase_date DATE, " +
            "remark TEXT, " +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP");
        // 迁移7: 创建 seedling 表 (种苗配置)
        createTableIfNotExists("seedling",
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "name TEXT, " +
            "species TEXT, " +
            "supplier TEXT, " +
            "default_price REAL, " +
            "feeding_cycle INTEGER, " +
            "remark TEXT, " +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP");
        // 迁移8: 创建 drug 表 (药品配置)
        createTableIfNotExists("drug",
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "name TEXT, " +
            "drug_type TEXT, " +
            "unit TEXT, " +
            "default_price REAL, " +
            "remark TEXT, " +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP");
        // 迁移9: 创建 customer 表 (客户配置)
        createTableIfNotExists("customer",
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "name TEXT, " +
            "phone TEXT, " +
            "address TEXT, " +
            "remark TEXT, " +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP");
        // 迁移10: 创建 expense 表 (其他支出)
        createTableIfNotExists("expense",
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "category TEXT, " +
            "category_label TEXT, " +
            "amount REAL, " +
            "expense_date DATE, " +
            "description TEXT, " +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP");
    }

    /**
     * 检查并添加列
     */
    private void addColumnIfNotExists(String tableName, String columnName, String columnType) {
        try {
            // 检查列是否存在
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pragma_table_info(?) WHERE name = ?",
                Integer.class, tableName, columnName
            );
            
            if (count != null && count == 0) {
                String sql = String.format("ALTER TABLE %s ADD COLUMN %s %s", tableName, columnName, columnType);
                jdbcTemplate.execute(sql);
                log.info("成功添加列: {}.{}", tableName, columnName);
            } else {
                log.debug("列已存在: {}.{}", tableName, columnName);
            }
        } catch (Exception e) {
            log.warn("添加列 {}.{} 失败: {}", tableName, columnName, e.getMessage());
        }
    }

    /**
     * 检查并创建表
     */
    private void createTableIfNotExists(String tableName, String columns) {
        try {
            // 检查表是否存在
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=?",
                Integer.class, tableName
            );
            
            if (count != null && count == 0) {
                String sql = String.format("CREATE TABLE %s (%s)", tableName, columns);
                jdbcTemplate.execute(sql);
                log.info("成功创建表: {}", tableName);
            } else {
                log.debug("表已存在: {}", tableName);
            }
        } catch (Exception e) {
            log.warn("创建表 {} 失败: {}", tableName, e.getMessage());
        }
    }
    
    /**
     * 解析SQL语句，正确处理多行语句和分号
     */
    private List<String> parseSqlStatements(String sql) {
        List<String> statements = new ArrayList<>();
        StringBuilder currentStatement = new StringBuilder();
        boolean inString = false;
        char stringDelimiter = 0;
        
        for (int i = 0; i < sql.length(); i++) {
            char ch = sql.charAt(i);
            
            // 处理字符串
            if (!inString && (ch == '"' || ch == '\'')) {
                inString = true;
                stringDelimiter = ch;
                currentStatement.append(ch);
                continue;
            }
            
            if (inString && ch == stringDelimiter) {
                // 检查是否是转义字符
                if (i > 0 && sql.charAt(i - 1) == '\\') {
                    currentStatement.append(ch);
                    continue;
                }
                inString = false;
                currentStatement.append(ch);
                continue;
            }
            
            // 在字符串内直接添加字符
            if (inString) {
                currentStatement.append(ch);
                continue;
            }
            
            // 处理注释
            if (ch == '-' && i + 1 < sql.length() && sql.charAt(i + 1) == '-') {
                // 单行注释，跳到行尾
                while (i < sql.length() && sql.charAt(i) != '\n') {
                    i++;
                }
                continue;
            }
            
            // 处理分号
            if (ch == ';') {
                String statement = currentStatement.toString().trim();
                if (!statement.isEmpty()) {
                    statements.add(statement);
                }
                currentStatement = new StringBuilder();
                continue;
            }
            
            currentStatement.append(ch);
        }
        
        // 处理最后一个语句（如果没有分号结尾）
        String lastStatement = currentStatement.toString().trim();
        if (!lastStatement.isEmpty()) {
            statements.add(lastStatement);
        }
        
        return statements;
    }
}
