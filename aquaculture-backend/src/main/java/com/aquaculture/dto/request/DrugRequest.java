package com.aquaculture.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DrugRequest {
    private String name;
    private String drugType;
    private String unit;
    private BigDecimal defaultPrice;
    private String remark;
}
