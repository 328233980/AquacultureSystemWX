package com.aquaculture.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FeedingCalculatorResponse {
    private BigDecimal recommendedAmount;
    private BigDecimal feedingRate;
    private int feedingTimes;
    private String notes;
}
