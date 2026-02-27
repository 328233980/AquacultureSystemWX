package com.aquaculture.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FarmingLog {
    private Long id;
    private Long pondId;
    private LocalDate logDate;
    private String weather;
    private BigDecimal temperature;
    private BigDecimal feedingAmount;
    private String feedingType;
    private BigDecimal feedCost;
    private Integer mortality;
    private String abnormalBehavior;
    private String remark;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
