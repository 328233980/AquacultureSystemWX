package com.aquaculture.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EquipmentRequest {
    private Long pondId;
    private String pondName;
    
    @NotBlank(message = "设备名称不能为空")
    private String name;
    
    @NotNull(message = "原值不能为空")
    private BigDecimal originalValue;
    
    @NotNull(message = "月折旧不能为空")
    private BigDecimal monthlyDepreciation;
    
    @NotNull(message = "购入日期不能为空")
    private LocalDate purchaseDate;
    
    private String remark;
}
