package com.aquaculture.service;

import com.aquaculture.dto.request.StockingRequest;
import com.aquaculture.entity.StockingRecord;

import java.time.LocalDate;
import java.util.List;

public interface StockingService {
    StockingRecord createStocking(Long userId, StockingRequest request);
    List<StockingRecord> getStockingList(Long userId, Long pondId, LocalDate startDate, LocalDate endDate);
    StockingRecord getStockingById(Long id);
    StockingRecord updateStocking(Long id, StockingRequest request);
    void deleteStocking(Long id);
}
