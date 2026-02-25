package com.aquaculture.dto.request;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class FeedingCalculatorRequest {
    @NotNull(message = "池塘面积不能为空")
    private BigDecimal pondArea;
    
    @NotNull(message = "品种不能为空")
    private String species;
    
    @NotNull(message = "生物量不能为空")
    private BigDecimal biomass;
    
    @NotNull(message = "生长阶段不能为空")
    private String growthStage;
    
    private BigDecimal waterTemp;
}
