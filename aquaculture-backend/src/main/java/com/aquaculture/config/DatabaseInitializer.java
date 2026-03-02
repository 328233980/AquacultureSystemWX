package com.aquaculture.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

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

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // 检查是否需要初始化数据库
        boolean needInit = true;
        try {
            // MySQL 检查表是否存在
            String dbName = extractDatabaseName();
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = 'user'",
                Integer.class, dbName
            );

            if (count != null && count > 0) {
                needInit = false;
                log.info("数据库已初始化，跳过初始化过程");
            }
        } catch (Exception e) {
            log.info("数据库未初始化或表不存在，开始初始化...");
        }

        if (!needInit) {
            log.info("数据库结构已存在，跳过初始化");
            // 测试模式下检查测试用户
            if (testMode) {
                createTestUser();
            }
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
                    // 对于已存在的表/索引，继续执行其他语句
                    String upperStmt = trimmed.toUpperCase();
                    if (!upperStmt.contains("ALREADY EXISTS") && !upperStmt.contains("DUPLICATE")) {
                        // 非重复创建的错误则记录但继续
                        log.error("SQL执行错误", e);
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

    /**
     * 从数据源URL中提取数据库名
     */
    private String extractDatabaseName() {
        try {
            // jdbc:mysql://localhost:3306/aquaculture?...
            int start = datasourceUrl.lastIndexOf('/') + 1;
            int end = datasourceUrl.indexOf('?');
            if (end > start) {
                return datasourceUrl.substring(start, end);
            }
            return datasourceUrl.substring(start);
        } catch (Exception e) {
            return "aquaculture";
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
            
            // 创建测试用户 (MySQL 使用 NOW() 函数)
            String sql = "INSERT INTO user (id, openid, nickname, avatar_url, role, status, created_at, updated_at) " +
                       "VALUES (1, 'test_openid', '测试用户', 'https://example.com/test-avatar.png', 'farmer', 1, NOW(), NOW())";
            jdbcTemplate.execute(sql);
            log.info("测试用户创建成功");
        } catch (Exception e) {
            log.error("创建测试用户失败", e);
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
            
            // 处理单行注释 --
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
