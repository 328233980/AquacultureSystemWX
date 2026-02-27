package com.aquaculture.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MedicationRequest {
    @NotNull(message = "池塘ID不能为空")
    private Long pondId;
    
    @NotNull(message = "用药日期不能为空")
    private LocalDate medicationDate;
    
    @NotBlank(message = "药品名称不能为空")
    private String drugName;
    
    private String drugType;
    
    @NotNull(message = "用量不能为空")
    private BigDecimal dosage;
    
    private String dosageUnit;
    private BigDecimal cost;
    private String purpose;
    private String targetDisease;
    private Integer withdrawalPeriod;
    private String operator;
    private String remark;
}
