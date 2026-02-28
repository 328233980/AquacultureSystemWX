package com.aquaculture.dto.request;

import lombok.Data;

@Data
public class CustomerRequest {
    private String name;
    private String phone;
    private String address;
    private String remark;
}
