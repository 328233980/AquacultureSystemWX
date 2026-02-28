package com.aquaculture.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SeedlingRequest {
    private String name;
    private String species;
    private String supplier;
    private BigDecimal defaultPrice;
    private Integer feedingCycle;
    private String remark;
}
