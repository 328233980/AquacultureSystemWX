package com.aquaculture.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StockingRecord {
    private Long id;
    private Long userId;
    private Long pondId;
    private LocalDate stockingDate;
    private String species;
    private Integer quantity;
    private String unit;
    private BigDecimal avgSize;
    private String supplier;
    private BigDecimal cost;
    private BigDecimal survivalRate;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
