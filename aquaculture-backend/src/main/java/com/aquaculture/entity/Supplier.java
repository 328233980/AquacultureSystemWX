package com.aquaculture.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Supplier {
    private Long id;
    private Long userId;
    private String name;
    private String phone;
    private String supplyTypes;  // 供应种类，逗号分隔：seedling,drug,equipment
    private String address;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
