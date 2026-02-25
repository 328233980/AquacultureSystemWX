package com.aquaculture.controller;

import com.aquaculture.dto.response.ApiResponse;
import com.aquaculture.dto.response.DashboardResponse;
import com.aquaculture.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public ApiResponse<DashboardResponse> getDashboard(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        DashboardResponse data = dashboardService.getDashboardData(userId);
        return ApiResponse.success(data);
    }
}
