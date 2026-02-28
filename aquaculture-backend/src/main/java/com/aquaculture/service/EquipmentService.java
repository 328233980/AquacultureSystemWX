package com.aquaculture.service;

import com.aquaculture.dto.request.EquipmentRequest;
import com.aquaculture.entity.Equipment;

import java.util.List;

public interface EquipmentService {
    Equipment createEquipment(Long userId, EquipmentRequest request);
    List<Equipment> getEquipmentList(Long userId, Long pondId);
    Equipment getEquipmentById(Long id);
    Equipment updateEquipment(Long id, EquipmentRequest request);
    void deleteEquipment(Long id);
}
