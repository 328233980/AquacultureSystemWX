package com.aquaculture.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Seedling {
    private Long id;
    private Long userId;
    private String name;
    private String species;
    private String supplier;
    private BigDecimal defaultPrice;
    private Integer feedingCycle;  // 每日投喂次数
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
