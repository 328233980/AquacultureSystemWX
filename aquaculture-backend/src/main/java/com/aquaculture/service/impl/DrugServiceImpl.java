package com.aquaculture.service.impl;

import com.aquaculture.dto.request.DrugRequest;
import com.aquaculture.entity.Drug;
import com.aquaculture.exception.BusinessException;
import com.aquaculture.mapper.DrugMapper;
import com.aquaculture.service.DrugService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class DrugServiceImpl implements DrugService {
    @Autowired
    private DrugMapper drugMapper;

    @Override
    public Drug create(Long userId, DrugRequest request) {
        Drug drug = new Drug();
        drug.setUserId(userId);
        drug.setName(request.getName());
        drug.setDrugType(request.getDrugType());
        drug.setUnit(request.getUnit());
        drug.setDefaultPrice(request.getDefaultPrice());
        drug.setRemark(request.getRemark());
        drugMapper.insert(drug);
        log.info("创建药品配置: id={}, name={}", drug.getId(), drug.getName());
        return drug;
    }

    @Override
    public List<Drug> getList(Long userId) {
        return drugMapper.findByUserId(userId);
    }

    @Override
    public Drug getById(Long id) {
        Drug drug = drugMapper.findById(id);
        if (drug == null) {
            throw new BusinessException(404, "药品配置不存在");
        }
        return drug;
    }

    @Override
    public Drug update(Long id, DrugRequest request) {
        Drug drug = getById(id);
        drug.setName(request.getName());
        drug.setDrugType(request.getDrugType());
        drug.setUnit(request.getUnit());
        drug.setDefaultPrice(request.getDefaultPrice());
        drug.setRemark(request.getRemark());
        drugMapper.update(drug);
        log.info("更新药品配置: id={}", id);
        return drug;
    }

    @Override
    public void delete(Long id) {
        getById(id);
        drugMapper.deleteById(id);
        log.info("删除药品配置: id={}", id);
    }
}
