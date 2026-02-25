package com.aquaculture.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Pond {
    private Long id;
    private Long userId;
    private String pondName;
    private String pondType;
    private BigDecimal area;
    private BigDecimal depth;
    private String location;
    private String status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
