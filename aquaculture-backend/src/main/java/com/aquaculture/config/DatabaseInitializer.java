package com.aquaculture.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

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
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='user'", 
                Integer.class
            );
            
            if (count != null && count > 0) {
                return; // 数据库已初始化
            }
        } catch (Exception e) {
            // 表不存在，继续初始化
        }

        // 读取并执行schema.sql
        ClassPathResource resource = new ClassPathResource("db/schema.sql");
        String sql = FileCopyUtils.copyToString(
            new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
        );

        // 按分号分割并执行每条SQL
        String[] statements = sql.split(";");
        for (String statement : statements) {
            String trimmed = statement.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                jdbcTemplate.execute(trimmed);
            }
        }
    }
}
