package com.aquaculture.service.impl;

import com.aquaculture.dto.request.EquipmentRequest;
import com.aquaculture.entity.Equipment;
import com.aquaculture.exception.BusinessException;
import com.aquaculture.mapper.EquipmentMapper;
import com.aquaculture.service.EquipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Override
    public Equipment createEquipment(Long userId, EquipmentRequest request) {
        Equipment equipment = new Equipment();
        equipment.setUserId(userId);
        equipment.setPondId(request.getPondId());
        equipment.setPondName(request.getPondName());
        equipment.setName(request.getName());
        equipment.setOriginalValue(request.getOriginalValue());
        equipment.setMonthlyDepreciation(request.getMonthlyDepreciation());
        equipment.setPurchaseDate(request.getPurchaseDate());
        equipment.setRemark(request.getRemark());

        equipmentMapper.insert(equipment);
        log.info("创建设备: id={}, name={}", equipment.getId(), equipment.getName());

        return equipment;
    }

    @Override
    public List<Equipment> getEquipmentList(Long userId, Long pondId) {
        if (pondId != null) {
            return equipmentMapper.findByUserIdAndPondId(userId, pondId);
        }
        return equipmentMapper.findByUserId(userId);
    }

    @Override
    public Equipment getEquipmentById(Long id) {
        Equipment equipment = equipmentMapper.findById(id);
        if (equipment == null) {
            throw new BusinessException(404, "设备不存在");
        }
        return equipment;
    }

    @Override
    public Equipment updateEquipment(Long id, EquipmentRequest request) {
        Equipment equipment = getEquipmentById(id);

        equipment.setPondId(request.getPondId());
        equipment.setPondName(request.getPondName());
        equipment.setName(request.getName());
        equipment.setOriginalValue(request.getOriginalValue());
        equipment.setMonthlyDepreciation(request.getMonthlyDepreciation());
        equipment.setPurchaseDate(request.getPurchaseDate());
        equipment.setRemark(request.getRemark());

        equipmentMapper.update(equipment);
        log.info("更新设备: id={}", id);

        return equipment;
    }

    @Override
    public void deleteEquipment(Long id) {
        getEquipmentById(id);
        equipmentMapper.deleteById(id);
        log.info("删除设备: id={}", id);
    }
}
