package com.aquaculture.controller;

import com.aquaculture.dto.request.HarvestRequest;
import com.aquaculture.dto.response.ApiResponse;
import com.aquaculture.entity.Attachment;
import com.aquaculture.entity.Harvest;
import com.aquaculture.service.FileService;
import com.aquaculture.service.HarvestService;
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
@RequestMapping("/api/harvests")
public class HarvestController {

    @Autowired
    private HarvestService harvestService;

    @Autowired
    private FileService fileService;

    @PostMapping
    public ApiResponse<Harvest> createHarvest(HttpServletRequest request,
                                               @Valid @RequestBody HarvestRequest harvestRequest) {
        Long userId = (Long) request.getAttribute("userId");
        Harvest harvest = harvestService.createHarvest(userId, harvestRequest);
        return ApiResponse.success("捕捞记录创建成功", harvest);
    }

    @GetMapping
    public ApiResponse<List<Harvest>> getHarvestList(HttpServletRequest request,
            @RequestParam(required = false) Long pondId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = (Long) request.getAttribute("userId");
        List<Harvest> harvests = harvestService.getHarvestList(userId, pondId, startDate, endDate);
        return ApiResponse.success(harvests);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getHarvestDetail(@PathVariable Long id) {
        Harvest harvest = harvestService.getHarvestById(id);
        List<Attachment> attachments = fileService.getAttachments("harvest", id);

        Map<String, Object> result = new HashMap<>();
        result.put("harvest", harvest);
        result.put("attachments", attachments);

        return ApiResponse.success(result);
    }

    @PutMapping("/{id}")
    public ApiResponse<Harvest> updateHarvest(@PathVariable Long id, @Valid @RequestBody HarvestRequest request) {
        Harvest harvest = harvestService.updateHarvest(id, request);
        return ApiResponse.success("捕捞记录更新成功", harvest);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteHarvest(@PathVariable Long id) {
        harvestService.deleteHarvest(id);
        return ApiResponse.success("捕捞记录删除成功", null);
    }

    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics(HttpServletRequest request,
                                                           @RequestParam(required = false) Integer year,
                                                           @RequestParam(required = false) Long pondId) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> stats = harvestService.getStatistics(userId, year, pondId);
        return ApiResponse.success(stats);
    }
}
