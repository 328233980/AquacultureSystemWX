package com.aquaculture.dto.request;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FarmingLogRequest {
    @NotNull(message = "池塘ID不能为空")
    private Long pondId;
    
    @NotNull(message = "日志日期不能为空")
    private LocalDate logDate;
    
    private String weather;
    private BigDecimal temperature;
    private BigDecimal feedingAmount;
    private String feedingType;
    private BigDecimal feedCost;
    private Integer mortality = 0;
    private String abnormalBehavior;
    private String remark;
    
    // 水质数据
    private WaterQualityData waterQuality;
    
    @Data
    public static class WaterQualityData {
        private BigDecimal waterTemp;
        private BigDecimal phValue;
        private BigDecimal dissolvedOxygen;
        private BigDecimal ammoniaNitrogen;
        private BigDecimal nitrite;
        private BigDecimal salinity;
        private BigDecimal transparency;
        private String remark;
    }
}
