package com.aquaculture.service.impl;

import com.aquaculture.dto.request.StockingRequest;
import com.aquaculture.entity.StockingRecord;
import com.aquaculture.entity.Pond;
import com.aquaculture.entity.Seedling;
import com.aquaculture.exception.BusinessException;
import com.aquaculture.mapper.PondMapper;
import com.aquaculture.mapper.StockingMapper;
import com.aquaculture.mapper.SeedlingMapper;
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

    @Autowired
    private SeedlingMapper seedlingMapper;

    @Override
    public StockingRecord createStocking(Long userId, StockingRequest request) {
        // 验证池塘存在且属于当前用户
        Pond pond = pondMapper.findById(request.getPondId());
        if (pond == null) {
            throw new BusinessException(404, "池塘不存在");
        }
        if (!userId.equals(pond.getUserId())) {
            throw new BusinessException(403, "无权操作此池塘");
        }

        StockingRecord record = new StockingRecord();
        record.setUserId(userId);
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
        
        // 如果提供了种苗ID，自动关联养殖周期和密度到池塘（仅当池塘未设置时）
        if (request.getSeedlingId() != null) {
            Seedling seedling = seedlingMapper.findById(request.getSeedlingId());
            if (seedling != null) {
                // 只有池塘未设置时才自动填充
                boolean needUpdate = false;
                if (pond.getCycleDays() == null && seedling.getCycleDays() != null) {
                    pond.setCycleDays(seedling.getCycleDays());
                    needUpdate = true;
                }
                if (pond.getDensity() == null && seedling.getDensity() != null) {
                    pond.setDensity(seedling.getDensity());
                    needUpdate = true;
                }
                if (needUpdate) {
                    pondMapper.update(pond);
                    log.info("自动关联池塘养殖周期和密度: pondId={}, cycleDays={}, density={}", 
                            pond.getId(), pond.getCycleDays(), pond.getDensity());
                }
            }
        }
        
        // 更新池塘状态为活跃
        pondMapper.updateStatus(request.getPondId(), "active");
        
        log.info("创建投放记录: id={}, pondId={}, userId={}", record.getId(), record.getPondId(), userId);
        
        return record;
    }

    @Override
    public List<StockingRecord> getStockingList(Long userId, Long pondId, LocalDate startDate, LocalDate endDate) {
        return stockingMapper.findByCondition(userId, pondId, startDate, endDate);
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
