package com.aquaculture.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Equipment {
    private Long id;
    private Long userId;
    private Long pondId;
    private String pondName;
    private String name;
    private BigDecimal originalValue;
    private BigDecimal monthlyDepreciation;
    private LocalDate purchaseDate;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
