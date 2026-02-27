package com.aquaculture.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class PondRequest {
    @NotBlank(message = "池塘名称不能为空")
    private String pondName;

    @NotBlank(message = "池塘类型不能为空")
    private String pondType;

    private BigDecimal area;
    private BigDecimal depth;
    private String location;
    private String remark;
    private String status;
}
