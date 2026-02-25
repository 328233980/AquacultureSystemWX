package com.aquaculture.service.impl;

import com.aquaculture.dto.request.StockingRequest;
import com.aquaculture.entity.StockingRecord;
import com.aquaculture.exception.BusinessException;
import com.aquaculture.mapper.PondMapper;
import com.aquaculture.mapper.StockingMapper;
import com.aquaculture.service.StockingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class StockingServiceImpl implements StockingService {

    @Autowired
    private StockingMapper stockingMapper;

    @Autowired
    private PondMapper pondMapper;

    @Override
    public StockingRecord createStocking(StockingRequest request) {
        // 验证池塘存在
        if (pondMapper.findById(request.getPondId()) == null) {
            throw new BusinessException(404, "池塘不存在");
        }

        StockingRecord record = new StockingRecord();
        record.setPondId(request.getPondId());
        record.setStockingDate(request.getStockingDate());
        record.setSpecies(request.getSpecies());
        record.setQuantity(request.getQuantity());
        record.setUnit(request.getUnit());
        record.setAvgSize(request.getAvgSize());
        record.setSupplier(request.getSupplier());
        record.setCost(request.getCost());
        record.setSurvivalRate(request.getSurvivalRate());
        record.setRemark(request.getRemark());

        stockingMapper.insert(record);
        
        // 更新池塘状态为活跃
        pondMapper.updateStatus(request.getPondId(), "active");
        
        log.info("创建投放记录: id={}, pondId={}", record.getId(), record.getPondId());
        
        return record;
    }

    @Override
    public List<StockingRecord> getStockingList(Long pondId, LocalDate startDate, LocalDate endDate) {
        return stockingMapper.findByCondition(pondId, startDate, endDate);
    }

    @Override
    public StockingRecord getStockingById(Long id) {
        StockingRecord record = stockingMapper.findById(id);
        if (record == null) {
            throw new BusinessException(404, "投放记录不存在");
        }
        return record;
    }

    @Override
    public StockingRecord updateStocking(Long id, StockingRequest request) {
        StockingRecord record = getStockingById(id);
        
        record.setStockingDate(request.getStockingDate());
        record.setSpecies(request.getSpecies());
        record.setQuantity(request.getQuantity());
        record.setUnit(request.getUnit());
        record.setAvgSize(request.getAvgSize());
        record.setSupplier(request.getSupplier());
        record.setCost(request.getCost());
        record.setSurvivalRate(request.getSurvivalRate());
        record.setRemark(request.getRemark());

        stockingMapper.update(record);
        log.info("更新投放记录: id={}", id);
        
        return record;
    }

    @Override
    public void deleteStocking(Long id) {
        getStockingById(id);
        stockingMapper.deleteById(id);
        log.info("删除投放记录: id={}", id);
    }
}
