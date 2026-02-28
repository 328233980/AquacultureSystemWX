package com.aquaculture.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Drug {
    private Long id;
    private Long userId;
    private String name;
    private String drugType;
    private String targetDisease;      // 针对病害
    private String unit;
    private BigDecimal defaultPrice;   // 参考单价
    private Integer withdrawalPeriod;  // 休药期(天)
    private String usage;              // 用法用量
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
