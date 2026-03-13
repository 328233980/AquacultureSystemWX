package com.aquaculture.controller;

import com.aquaculture.dto.request.PondRequest;
import com.aquaculture.dto.response.ApiResponse;
import com.aquaculture.entity.Pond;
import com.aquaculture.entity.StockingRecord;
import com.aquaculture.entity.WaterQuality;
import com.aquaculture.service.FarmingLogService;
import com.aquaculture.service.PondService;
import com.aquaculture.service.StockingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ponds")
public class PondController {

    @Autowired
    private PondService pondService;

    @Autowired
    private StockingService stockingService;

    @Autowired
    private FarmingLogService farmingLogService;

    @PostMapping
    public ApiResponse<Pond> createPond(HttpServletRequest request, @Valid @RequestBody PondRequest pondRequest) {
        Long userId = (Long) request.getAttribute("userId");
        Pond pond = pondService.createPond(userId, pondRequest);
        return ApiResponse.success("池塘创建成功", pond);
    }

    @GetMapping
    public ApiResponse<List<Pond>> getPondList(HttpServletRequest request,
                                               @RequestParam(required = false) String status,
                                               @RequestParam(required = false) String type) {
        Long userId = (Long) request.getAttribute("userId");
        List<Pond> ponds = pondService.getPondList(userId, status, type);
        return ApiResponse.success(ponds);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getPondDetail(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        Pond pond = pondService.getPondById(id);
        List<StockingRecord> stockingRecords = stockingService.getStockingList(userId, id, null, null);
        List<WaterQuality> waterQualityTrend = farmingLogService.getWaterQualityTrend(userId, id, 7);

        Map<String, Object> result = new HashMap<>();
        result.put("pond", pond);
        result.put("stockingRecords", stockingRecords);
        result.put("waterQualityTrend", waterQualityTrend);

        return ApiResponse.success(result);
    }

    @PutMapping("/{id}")
    public ApiResponse<Pond> updatePond(@PathVariable Long id, @Valid @RequestBody PondRequest pondRequest) {
        Pond pond = pondService.updatePond(id, pondRequest);
        return ApiResponse.success("池塘更新成功", pond);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePond(@PathVariable Long id) {
        pondService.deletePond(id);
        return ApiResponse.success("池塘删除成功", null);
    }
}
