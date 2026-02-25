package com.aquaculture.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Medication {
    private Long id;
    private Long pondId;
    private LocalDate medicationDate;
    private String drugName;
    private String drugType;
    private BigDecimal dosage;
    private String dosageUnit;
    private String purpose;
    private String targetDisease;
    private Integer withdrawalPeriod;
    private LocalDate withdrawalEndDate;
    private String operator;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
