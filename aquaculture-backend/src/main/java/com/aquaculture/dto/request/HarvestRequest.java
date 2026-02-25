package com.aquaculture.dto.request;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HarvestRequest {
    @NotNull(message = "池塘ID不能为空")
    private Long pondId;
    
    @NotNull(message = "捕捞日期不能为空")
    private LocalDate harvestDate;
    
    private String harvestType = "full";
    
    @NotNull(message = "捕捞量不能为空")
    private BigDecimal quantity;
    
    private BigDecimal avgWeight;
    private Integer totalCount;
    private BigDecimal gradeA;
    private BigDecimal gradeB;
    private BigDecimal gradeC;
    private BigDecimal pricePerKg;
    private BigDecimal totalRevenue;
    private String buyer;
    private String destination;
    private String remark;
}
