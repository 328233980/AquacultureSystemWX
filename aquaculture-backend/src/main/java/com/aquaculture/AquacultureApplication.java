package com.aquaculture;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.aquaculture.mapper")
@EnableScheduling
public class AquacultureApplication {

    public static void main(String[] args) {
        SpringApplication.run(AquacultureApplication.class, args);
    }
}
