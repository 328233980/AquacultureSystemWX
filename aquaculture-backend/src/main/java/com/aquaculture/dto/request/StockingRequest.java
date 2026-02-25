package com.aquaculture.dto.request;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StockingRequest {
    @NotNull(message = "池塘ID不能为空")
    private Long pondId;
    
    @NotNull(message = "投放日期不能为空")
    private LocalDate stockingDate;
    
    @NotNull(message = "品种不能为空")
    private String species;
    
    @NotNull(message = "数量不能为空")
    private Integer quantity;
    
    private String unit = "tail";
    private BigDecimal avgSize;
    private String supplier;
    private BigDecimal cost;
    private BigDecimal survivalRate;
    private String remark;
}
