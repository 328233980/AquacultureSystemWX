package com.aquaculture.service;

import com.aquaculture.dto.request.FeedingCalculatorRequest;
import com.aquaculture.dto.response.FeedingCalculatorResponse;

public interface CalculatorService {
    FeedingCalculatorResponse calculateFeeding(FeedingCalculatorRequest request);
}
