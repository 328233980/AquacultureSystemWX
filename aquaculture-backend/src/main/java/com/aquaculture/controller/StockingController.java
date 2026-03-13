package com.aquaculture.controller;

import com.aquaculture.dto.request.StockingRequest;
import com.aquaculture.dto.response.ApiResponse;
import com.aquaculture.entity.Attachment;
import com.aquaculture.entity.StockingRecord;
import com.aquaculture.service.FileService;
import com.aquaculture.service.StockingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocking")
public class StockingController {

    @Autowired
    private StockingService stockingService;

    @Autowired
    private FileService fileService;

    @PostMapping
    public ApiResponse<StockingRecord> createStocking(HttpServletRequest request,
                                                       @Valid @RequestBody StockingRequest stockingRequest) {
        Long userId = (Long) request.getAttribute("userId");
        StockingRecord record = stockingService.createStocking(userId, stockingRequest);
        return ApiResponse.success("投放记录创建成功", record);
    }

    @GetMapping
    public ApiResponse<List<StockingRecord>> getStockingList(HttpServletRequest request,
            @RequestParam(required = false) Long pondId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = (Long) request.getAttribute("userId");
        List<StockingRecord> records = stockingService.getStockingList(userId, pondId, startDate, endDate);
        return ApiResponse.success(records);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getStockingDetail(@PathVariable Long id) {
        StockingRecord record = stockingService.getStockingById(id);
        List<Attachment> attachments = fileService.getAttachments("stocking", id);

        Map<String, Object> result = new HashMap<>();
        result.put("record", record);
        result.put("attachments", attachments);

        return ApiResponse.success(result);
    }

    @PutMapping("/{id}")
    public ApiResponse<StockingRecord> updateStocking(@PathVariable Long id, @Valid @RequestBody StockingRequest request) {
        StockingRecord record = stockingService.updateStocking(id, request);
        return ApiResponse.success("投放记录更新成功", record);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteStocking(@PathVariable Long id) {
        stockingService.deleteStocking(id);
        return ApiResponse.success("投放记录删除成功", null);
    }
}
