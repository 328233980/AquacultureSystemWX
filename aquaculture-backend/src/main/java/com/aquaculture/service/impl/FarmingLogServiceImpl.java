package com.aquaculture.service.impl;

import com.aquaculture.dto.request.FarmingLogRequest;
import com.aquaculture.entity.FarmingLog;
import com.aquaculture.entity.WaterQuality;
import com.aquaculture.exception.BusinessException;
import com.aquaculture.mapper.FarmingLogMapper;
import com.aquaculture.mapper.PondMapper;
import com.aquaculture.mapper.WaterQualityMapper;
import com.aquaculture.service.FarmingLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FarmingLogServiceImpl implements FarmingLogService {

    @Autowired
    private FarmingLogMapper farmingLogMapper;

    @Autowired
    private WaterQualityMapper waterQualityMapper;

    @Autowired
    private PondMapper pondMapper;

    @Override
    @Transactional
    public FarmingLog createFarmingLog(Long userId, FarmingLogRequest request) {
        // 验证池塘存在
        if (pondMapper.findById(request.getPondId()) == null) {
            throw new BusinessException(404, "池塘不存在");
        }

        FarmingLog log = new FarmingLog();
        log.setPondId(request.getPondId());
        log.setLogDate(request.getLogDate());
        log.setWeather(request.getWeather());
        log.setTemperature(request.getTemperature());
        log.setFeedingAmount(request.getFeedingAmount());
        log.setFeedingType(request.getFeedingType());
        log.setMortality(request.getMortality());
        log.setAbnormalBehavior(request.getAbnormalBehavior());
        log.setRemark(request.getRemark());
        log.setCreatedBy(userId);

        farmingLogMapper.insert(log);

        // 保存水质数据
        if (request.getWaterQuality() != null) {
            FarmingLogRequest.WaterQualityData wqData = request.getWaterQuality();
            WaterQuality wq = new WaterQuality();
            wq.setFarmingLogId(log.getId());
            wq.setPondId(request.getPondId());
            wq.setTestTime(LocalDateTime.now());
            wq.setWaterTemp(wqData.getWaterTemp());
            wq.setPhValue(wqData.getPhValue());
            wq.setDissolvedOxygen(wqData.getDissolvedOxygen());
            wq.setAmmoniaNitrogen(wqData.getAmmoniaNitrogen());
            wq.setNitrite(wqData.getNitrite());
            wq.setSalinity(wqData.getSalinity());
            wq.setTransparency(wqData.getTransparency());
            wq.setRemark(wqData.getRemark());
            waterQualityMapper.insert(wq);
        }

        return log;
    }

    @Override
    public Map<String, Object> getFarmingLogList(Long pondId, LocalDate startDate, LocalDate endDate, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<FarmingLog> logs = farmingLogMapper.findByCondition(pondId, startDate, endDate, offset, pageSize);
        int total = farmingLogMapper.countByCondition(pondId, startDate, endDate);

        Map<String, Object> result = new HashMap<>();
        result.put("logs", logs);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    @Override
    public FarmingLog getFarmingLogById(Long id) {
        FarmingLog log = farmingLogMapper.findById(id);
        if (log == null) {
            throw new BusinessException(404, "养殖日志不存在");
        }
        return log;
    }

    @Override
    public WaterQuality getWaterQualityByLogId(Long logId) {
        return waterQualityMapper.findByFarmingLogId(logId);
    }

    @Override
    @Transactional
    public FarmingLog updateFarmingLog(Long id, FarmingLogRequest request) {
        FarmingLog log = getFarmingLogById(id);

        log.setLogDate(request.getLogDate());
        log.setWeather(request.getWeather());
        log.setTemperature(request.getTemperature());
        log.setFeedingAmount(request.getFeedingAmount());
        log.setFeedingType(request.getFeedingType());
        log.setMortality(request.getMortality());
        log.setAbnormalBehavior(request.getAbnormalBehavior());
        log.setRemark(request.getRemark());

        farmingLogMapper.update(log);

        // 更新水质数据
        if (request.getWaterQuality() != null) {
            FarmingLogRequest.WaterQualityData wqData = request.getWaterQuality();
            WaterQuality wq = waterQualityMapper.findByFarmingLogId(id);
            if (wq == null) {
                wq = new WaterQuality();
                wq.setFarmingLogId(id);
                wq.setPondId(log.getPondId());
                wq.setTestTime(LocalDateTime.now());
            }
            wq.setWaterTemp(wqData.getWaterTemp());
            wq.setPhValue(wqData.getPhValue());
            wq.setDissolvedOxygen(wqData.getDissolvedOxygen());
            wq.setAmmoniaNitrogen(wqData.getAmmoniaNitrogen());
            wq.setNitrite(wqData.getNitrite());
            wq.setSalinity(wqData.getSalinity());
            wq.setTransparency(wqData.getTransparency());
            wq.setRemark(wqData.getRemark());

            if (wq.getId() == null) {
                waterQualityMapper.insert(wq);
            } else {
                waterQualityMapper.update(wq);
            }
        }

        return log;
    }

    @Override
    @Transactional
    public void deleteFarmingLog(Long id) {
        getFarmingLogById(id);
        waterQualityMapper.deleteByFarmingLogId(id);
        farmingLogMapper.deleteById(id);
    }

    @Override
    public List<WaterQuality> getWaterQualityTrend(Long pondId, int days) {
        return waterQualityMapper.findRecentByPondId(pondId, days);
    }
}
