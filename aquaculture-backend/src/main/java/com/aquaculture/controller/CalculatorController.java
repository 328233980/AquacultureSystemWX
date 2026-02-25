package com.aquaculture.controller;

import com.aquaculture.dto.request.FeedingCalculatorRequest;
import com.aquaculture.dto.response.ApiResponse;
import com.aquaculture.dto.response.FeedingCalculatorResponse;
import com.aquaculture.service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/calculator")
public class CalculatorController {

    @Autowired
    private CalculatorService calculatorService;

    @PostMapping("/feeding")
    public ApiResponse<FeedingCalculatorResponse> calculateFeeding(@Valid @RequestBody FeedingCalculatorRequest request) {
        FeedingCalculatorResponse response = calculatorService.calculateFeeding(request);
        return ApiResponse.success(response);
    }
}
