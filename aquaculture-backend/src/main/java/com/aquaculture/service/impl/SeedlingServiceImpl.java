package com.aquaculture.service.impl;

import com.aquaculture.dto.request.SeedlingRequest;
import com.aquaculture.entity.Seedling;
import com.aquaculture.exception.BusinessException;
import com.aquaculture.mapper.SeedlingMapper;
import com.aquaculture.service.SeedlingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class SeedlingServiceImpl implements SeedlingService {
    @Autowired
    private SeedlingMapper seedlingMapper;

    @Override
    public Seedling create(Long userId, SeedlingRequest request) {
        Seedling seedling = new Seedling();
        seedling.setUserId(userId);
        seedling.setName(request.getName());
        seedling.setCategory(request.getCategory());
        seedling.setSpecies(request.getSpecies());
        seedling.setSupplier(request.getSupplier());
        seedling.setDefaultPrice(request.getDefaultPrice());
        seedling.setFeedingCycle(request.getFeedingCycle());
        seedling.setAvgWeight(request.getAvgWeight());
        seedling.setTempMin(request.getTempMin());
        seedling.setTempMax(request.getTempMax());
        seedling.setPhMin(request.getPhMin());
        seedling.setPhMax(request.getPhMax());
        seedling.setDoMin(request.getDoMin());
        seedling.setDoMax(request.getDoMax());
        seedling.setRemark(request.getRemark());
        seedlingMapper.insert(seedling);
        log.info("创建种苗配置: id={}, name={}", seedling.getId(), seedling.getName());
        return seedling;
    }

    @Override
    public List<Seedling> getList(Long userId) {
        return seedlingMapper.findByUserId(userId);
    }

    @Override
    public Seedling getById(Long id) {
        Seedling seedling = seedlingMapper.findById(id);
        if (seedling == null) {
            throw new BusinessException(404, "种苗配置不存在");
        }
        return seedling;
    }

    @Override
    public Seedling update(Long id, SeedlingRequest request) {
        Seedling seedling = getById(id);
        seedling.setName(request.getName());
        seedling.setCategory(request.getCategory());
        seedling.setSpecies(request.getSpecies());
        seedling.setSupplier(request.getSupplier());
        seedling.setDefaultPrice(request.getDefaultPrice());
        seedling.setFeedingCycle(request.getFeedingCycle());
        seedling.setAvgWeight(request.getAvgWeight());
        seedling.setTempMin(request.getTempMin());
        seedling.setTempMax(request.getTempMax());
        seedling.setPhMin(request.getPhMin());
        seedling.setPhMax(request.getPhMax());
        seedling.setDoMin(request.getDoMin());
        seedling.setDoMax(request.getDoMax());
        seedling.setRemark(request.getRemark());
        seedlingMapper.update(seedling);
        log.info("更新种苗配置: id={}", id);
        return seedling;
    }

    @Override
    public void delete(Long id) {
        getById(id);
        seedlingMapper.deleteById(id);
        log.info("删除种苗配置: id={}", id);
    }
}
