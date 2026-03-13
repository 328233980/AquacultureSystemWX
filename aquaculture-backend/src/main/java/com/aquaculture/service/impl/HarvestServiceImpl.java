package com.aquaculture.service.impl;

import com.aquaculture.dto.request.HarvestRequest;
import com.aquaculture.entity.Harvest;
import com.aquaculture.entity.Pond;
import com.aquaculture.exception.BusinessException;
import com.aquaculture.mapper.HarvestMapper;
import com.aquaculture.mapper.PondMapper;
import com.aquaculture.service.HarvestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HarvestServiceImpl implements HarvestService {

    @Autowired
    private HarvestMapper harvestMapper;

    @Autowired
    private PondMapper pondMapper;

    @Override
    @Transactional
    public Harvest createHarvest(Long userId, HarvestRequest request) {
        Pond pond = pondMapper.findById(request.getPondId());
        if (pond == null) {
            throw new BusinessException(404, "池塘不存在");
        }
        if (!userId.equals(pond.getUserId())) {
            throw new BusinessException(403, "无权操作此池塘");
        }

        Harvest harvest = new Harvest();
        harvest.setUserId(userId);
        harvest.setPondId(request.getPondId());
        harvest.setHarvestDate(request.getHarvestDate());
        harvest.setHarvestType(request.getHarvestType());
        harvest.setQuantity(request.getQuantity());
        harvest.setAvgWeight(request.getAvgWeight());
        harvest.setTotalCount(request.getTotalCount());
        harvest.setGradeA(request.getGradeA());
        harvest.setGradeB(request.getGradeB());
        harvest.setGradeC(request.getGradeC());
        harvest.setPricePerKg(request.getPricePerKg());
        harvest.setTotalRevenue(request.getTotalRevenue());
        harvest.setBuyer(request.getBuyer());
        harvest.setDestination(request.getDestination());
        harvest.setRemark(request.getRemark());

        // 自动计算总收入
        if (harvest.getTotalRevenue() == null && harvest.getPricePerKg() != null && harvest.getQuantity() != null) {
            harvest.setTotalRevenue(harvest.getPricePerKg().multiply(harvest.getQuantity()));
        }

        harvestMapper.insert(harvest);

        // 如果是全部捕捞，更新池塘状态为已清塘
        if ("full".equals(request.getHarvestType())) {
            pondMapper.updateStatus(request.getPondId(), "harvested");
        }

        log.info("创建捕捞记录: id={}, quantity={}, userId={}", harvest.getId(), harvest.getQuantity(), userId);

        return harvest;
    }

    @Override
    public List<Harvest> getHarvestList(Long userId, Long pondId, LocalDate startDate, LocalDate endDate) {
        return harvestMapper.findByCondition(userId, pondId, startDate, endDate);
    }

    @Override
    public Harvest getHarvestById(Long id) {
        Harvest harvest = harvestMapper.findById(id);
        if (harvest == null) {
            throw new BusinessException(404, "捕捞记录不存在");
        }
        return harvest;
    }

    @Override
    public Harvest updateHarvest(Long id, HarvestRequest request) {
        Harvest harvest = getHarvestById(id);

        harvest.setHarvestDate(request.getHarvestDate());
        harvest.setHarvestType(request.getHarvestType());
        harvest.setQuantity(request.getQuantity());
        harvest.setAvgWeight(request.getAvgWeight());
        harvest.setTotalCount(request.getTotalCount());
        harvest.setGradeA(request.getGradeA());
        harvest.setGradeB(request.getGradeB());
        harvest.setGradeC(request.getGradeC());
        harvest.setPricePerKg(request.getPricePerKg());
        harvest.setTotalRevenue(request.getTotalRevenue());
        harvest.setBuyer(request.getBuyer());
        harvest.setDestination(request.getDestination());
        harvest.setRemark(request.getRemark());

        harvestMapper.update(harvest);
        log.info("更新捕捞记录: id={}", id);

        return harvest;
    }

    @Override
    public void deleteHarvest(Long id) {
        getHarvestById(id);
        harvestMapper.deleteById(id);
        log.info("删除捕捞记录: id={}", id);
    }

    @Override
    public Map<String, Object> getStatistics(Long userId, Integer year, Long pondId) {
        Map<String, Object> stats = new HashMap<>();
        
        BigDecimal totalQuantity = harvestMapper.sumQuantityByUserId(userId);
        BigDecimal totalRevenue = harvestMapper.sumRevenueByUserId(userId);
        
        stats.put("totalQuantity", totalQuantity);
        stats.put("totalRevenue", totalRevenue);
        
        if (totalQuantity != null && totalQuantity.compareTo(BigDecimal.ZERO) > 0 
                && totalRevenue != null) {
            stats.put("averagePrice", totalRevenue.divide(totalQuantity, 2, BigDecimal.ROUND_HALF_UP));
        } else {
            stats.put("averagePrice", BigDecimal.ZERO);
        }
        
        return stats;
    }
}
