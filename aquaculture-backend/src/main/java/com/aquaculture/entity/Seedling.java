package com.aquaculture.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Seedling {
    private Long id;
    private Long userId;
    private String name;
    private String category;       // 分类：fish, shrimp, crab, other
    private String species;        // 品种
    private String supplier;
    private BigDecimal defaultPrice;
    private Integer feedingCycle;  // 每日投喂次数
    private BigDecimal avgWeight;  // 平均重量(克/尾)
    private BigDecimal tempMin;    // 最低水温
    private BigDecimal tempMax;    // 最高水温
    private BigDecimal phMin;      // 最低pH
    private BigDecimal phMax;      // 最高pH
    private BigDecimal doMin;      // 最低溶氧量
    private BigDecimal doMax;      // 最高溶氧量
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
