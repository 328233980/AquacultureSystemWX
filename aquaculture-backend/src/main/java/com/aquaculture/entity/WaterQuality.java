package com.aquaculture.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WaterQuality {
    private Long id;
    private Long farmingLogId;
    private Long pondId;
    private LocalDateTime testTime;
    private BigDecimal waterTemp;
    private BigDecimal phValue;
    private BigDecimal dissolvedOxygen;
    private BigDecimal ammoniaNitrogen;
    private BigDecimal nitrite;
    private BigDecimal salinity;
    private BigDecimal transparency;
    private String remark;
    private LocalDateTime createdAt;
}
