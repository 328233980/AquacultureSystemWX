package com.aquaculture.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DrugRequest {
    private String name;
    private String drugType;
    private String targetDisease;      // 针对病害
    private String unit;
    private BigDecimal defaultPrice;   // 参考单价
    private Integer withdrawalPeriod;  // 休药期(天)
    private String usage;              // 用法用量
    private String remark;
}
