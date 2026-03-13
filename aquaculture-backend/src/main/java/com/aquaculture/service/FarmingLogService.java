package com.aquaculture.service;

import com.aquaculture.dto.request.FarmingLogRequest;
import com.aquaculture.entity.FarmingLog;
import com.aquaculture.entity.WaterQuality;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FarmingLogService {
    FarmingLog createFarmingLog(Long userId, FarmingLogRequest request);
    Map<String, Object> getFarmingLogList(Long userId, Long pondId, LocalDate startDate, LocalDate endDate, int page, int pageSize);
    FarmingLog getFarmingLogById(Long id);
    WaterQuality getWaterQualityByLogId(Long logId);
    FarmingLog updateFarmingLog(Long id, FarmingLogRequest request);
    void deleteFarmingLog(Long id);
    List<WaterQuality> getWaterQualityTrend(Long pondId, int days);
}
