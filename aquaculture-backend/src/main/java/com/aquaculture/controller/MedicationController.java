package com.aquaculture.controller;

import com.aquaculture.dto.request.MedicationRequest;
import com.aquaculture.dto.response.ApiResponse;
import com.aquaculture.entity.Medication;
import com.aquaculture.service.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medications")
public class MedicationController {

    @Autowired
    private MedicationService medicationService;

    @PostMapping
    public ApiResponse<Map<String, Object>> createMedication(HttpServletRequest request,
                                                              @Valid @RequestBody MedicationRequest medicationRequest) {
        Long userId = (Long) request.getAttribute("userId");
        Medication medication = medicationService.createMedication(userId, medicationRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("medication", medication);
        result.put("withdrawalEndDate", medication.getWithdrawalEndDate());

        return ApiResponse.success("用药记录创建成功", result);
    }

    @GetMapping
    public ApiResponse<List<Medication>> getMedicationList(HttpServletRequest request,
            @RequestParam(required = false) Long pondId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Boolean inWithdrawalPeriod) {
        Long userId = (Long) request.getAttribute("userId");
        List<Medication> medications = medicationService.getMedicationList(userId, pondId, startDate, endDate, inWithdrawalPeriod);
        return ApiResponse.success(medications);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getMedicationDetail(@PathVariable Long id) {
        Medication medication = medicationService.getMedicationById(id);

        Map<String, Object> result = new HashMap<>();
        result.put("medication", medication);

        // 计算距离休药期结束的天数
        if (medication.getWithdrawalEndDate() != null) {
            long daysUntilEnd = ChronoUnit.DAYS.between(LocalDate.now(), medication.getWithdrawalEndDate());
            result.put("daysUntilWithdrawalEnd", Math.max(0, daysUntilEnd));
            result.put("isInWithdrawalPeriod", daysUntilEnd >= 0);
        }

        return ApiResponse.success(result);
    }

    @PutMapping("/{id}")
    public ApiResponse<Medication> updateMedication(@PathVariable Long id,
                                                     @Valid @RequestBody MedicationRequest medicationRequest) {
        Medication medication = medicationService.updateMedication(id, medicationRequest);
        return ApiResponse.success("用药记录更新成功", medication);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMedication(@PathVariable Long id) {
        medicationService.deleteMedication(id);
        return ApiResponse.success("用药记录删除成功", null);
    }
}
