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
    private String unit;
    private BigDecimal defaultPrice;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
