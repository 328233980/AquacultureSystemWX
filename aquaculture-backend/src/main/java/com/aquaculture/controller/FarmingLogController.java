package com.aquaculture.controller;

import com.aquaculture.dto.request.FarmingLogRequest;
import com.aquaculture.dto.response.ApiResponse;
import com.aquaculture.entity.Attachment;
import com.aquaculture.entity.FarmingLog;
import com.aquaculture.entity.WaterQuality;
import com.aquaculture.service.FarmingLogService;
import com.aquaculture.service.FileService;
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
@RequestMapping("/api/farming-logs")
public class FarmingLogController {

    @Autowired
    private FarmingLogService farmingLogService;

    @Autowired
    private FileService fileService;

    @PostMapping
    public ApiResponse<FarmingLog> createFarmingLog(HttpServletRequest request,
                                                     @Valid @RequestBody FarmingLogRequest logRequest) {
        Long userId = (Long) request.getAttribute("userId");
        FarmingLog log = farmingLogService.createFarmingLog(userId, logRequest);
        return ApiResponse.success("养殖日志创建成功", log);
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> getFarmingLogList(HttpServletRequest request,
            @RequestParam(required = false) Long pondId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> result = farmingLogService.getFarmingLogList(userId, pondId, startDate, endDate, page, pageSize);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getFarmingLogDetail(@PathVariable Long id) {
        FarmingLog log = farmingLogService.getFarmingLogById(id);
        WaterQuality waterQuality = farmingLogService.getWaterQualityByLogId(id);
        List<Attachment> attachments = fileService.getAttachments("log", id);

        Map<String, Object> result = new HashMap<>();
        result.put("log", log);
        result.put("waterQuality", waterQuality);
        result.put("attachments", attachments);

        return ApiResponse.success(result);
    }

    @PutMapping("/{id}")
    public ApiResponse<FarmingLog> updateFarmingLog(@PathVariable Long id,
                                                     @Valid @RequestBody FarmingLogRequest logRequest) {
        FarmingLog log = farmingLogService.updateFarmingLog(id, logRequest);
        return ApiResponse.success("养殖日志更新成功", log);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFarmingLog(@PathVariable Long id) {
        farmingLogService.deleteFarmingLog(id);
        return ApiResponse.success("养殖日志删除成功", null);
    }
}
