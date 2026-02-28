package com.aquaculture.controller;

import com.aquaculture.dto.request.EquipmentRequest;
import com.aquaculture.dto.response.ApiResponse;
import com.aquaculture.entity.Equipment;
import com.aquaculture.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/equipments")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @PostMapping
    public ApiResponse<Equipment> createEquipment(HttpServletRequest request, @Valid @RequestBody EquipmentRequest equipmentRequest) {
        Long userId = (Long) request.getAttribute("userId");
        Equipment equipment = equipmentService.createEquipment(userId, equipmentRequest);
        return ApiResponse.success("设备创建成功", equipment);
    }

    @GetMapping
    public ApiResponse<List<Equipment>> getEquipmentList(HttpServletRequest request,
                                                         @RequestParam(required = false) Long pondId) {
        Long userId = (Long) request.getAttribute("userId");
        List<Equipment> equipments = equipmentService.getEquipmentList(userId, pondId);
        return ApiResponse.success(equipments);
    }

    @GetMapping("/{id}")
    public ApiResponse<Equipment> getEquipmentDetail(@PathVariable Long id) {
        Equipment equipment = equipmentService.getEquipmentById(id);
        return ApiResponse.success(equipment);
    }

    @PutMapping("/{id}")
    public ApiResponse<Equipment> updateEquipment(@PathVariable Long id, @Valid @RequestBody EquipmentRequest equipmentRequest) {
        Equipment equipment = equipmentService.updateEquipment(id, equipmentRequest);
        return ApiResponse.success("设备更新成功", equipment);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return ApiResponse.success("设备删除成功", null);
    }
}
