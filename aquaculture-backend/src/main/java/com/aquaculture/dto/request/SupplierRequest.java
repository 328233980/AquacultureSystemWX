package com.aquaculture.dto.request;

import lombok.Data;

@Data
public class SupplierRequest {
    private String name;
    private String phone;
    private String supplyTypes;  // 供应种类，逗号分隔：seedling,drug,equipment
    private String address;
    private String remark;
}
