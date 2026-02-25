package com.aquaculture.service;

import com.aquaculture.dto.request.HarvestRequest;
import com.aquaculture.entity.Harvest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface HarvestService {
    Harvest createHarvest(HarvestRequest request);
    List<Harvest> getHarvestList(Long pondId, LocalDate startDate, LocalDate endDate);
    Harvest getHarvestById(Long id);
    Harvest updateHarvest(Long id, HarvestRequest request);
    void deleteHarvest(Long id);
    Map<String, Object> getStatistics(Long userId, Integer year, Long pondId);
}
