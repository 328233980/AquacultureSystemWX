package com.aquaculture.service;

import com.aquaculture.dto.response.DashboardResponse;

public interface DashboardService {
    DashboardResponse getDashboardData(Long userId);
}
