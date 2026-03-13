package com.aquaculture.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Harvest {
    private Long id;
    private Long userId;
    private Long pondId;
    private LocalDate harvestDate;
    private String harvestType;
    private BigDecimal quantity;
    private BigDecimal avgWeight;
    private Integer totalCount;
    private Integer mortality;  // 捕捞死亡数量
    private BigDecimal gradeA;
    private BigDecimal gradeB;
    private BigDecimal gradeC;
    private BigDecimal pricePerKg;
    private BigDecimal totalRevenue;
    private String buyer;
    private String destination;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
