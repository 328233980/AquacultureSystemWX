package com.aquaculture.service.impl;

import com.aquaculture.dto.request.PondRequest;
import com.aquaculture.entity.Pond;
import com.aquaculture.exception.BusinessException;
import com.aquaculture.mapper.PondMapper;
import com.aquaculture.mapper.StockingMapper;
import com.aquaculture.service.PondService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PondServiceImpl implements PondService {

    @Autowired
    private PondMapper pondMapper;

    @Autowired
    private StockingMapper stockingMapper;

    @Override
    public Pond createPond(Long userId, PondRequest request) {
        Pond pond = new Pond();
        pond.setUserId(userId);
        pond.setPondName(request.getPondName());
        pond.setPondType(request.getPondType());
        pond.setArea(request.getArea());
        pond.setDepth(request.getDepth());
        pond.setLocation(request.getLocation());
        pond.setRemark(request.getRemark());
        pond.setStatus("active");
        
        pondMapper.insert(pond);
        log.info("创建池塘: id={}, name={}", pond.getId(), pond.getPondName());
        
        return pond;
    }

    @Override
    public List<Pond> getPondList(Long userId, String status, String pondType) {
        if (status != null && !status.isEmpty()) {
            return pondMapper.findByUserIdAndStatus(userId, status);
        }
        if (pondType != null && !pondType.isEmpty()) {
            return pondMapper.findByUserIdAndType(userId, pondType);
        }
        return pondMapper.findByUserId(userId);
    }

    @Override
    public Pond getPondById(Long id) {
        Pond pond = pondMapper.findById(id);
        if (pond == null) {
            throw new BusinessException(404, "池塘不存在");
        }
        return pond;
    }

    @Override
    public Pond updatePond(Long id, PondRequest request) {
        Pond pond = getPondById(id);
        
        pond.setPondName(request.getPondName());
        pond.setPondType(request.getPondType());
        pond.setArea(request.getArea());
        pond.setDepth(request.getDepth());
        pond.setLocation(request.getLocation());
        pond.setRemark(request.getRemark());
        
        pondMapper.update(pond);
        log.info("更新池塘: id={}", id);
        
        return pond;
    }

    @Override
    public void deletePond(Long id) {
        Pond pond = getPondById(id);
        
        // 检查是否有活跃的投放记录
        var latestStocking = stockingMapper.findLatestByPondId(id);
        if (latestStocking != null && "active".equals(pond.getStatus())) {
            throw new BusinessException(400, "该池塘有活跃的养殖批次，无法删除");
        }
        
        pondMapper.deleteById(id);
        log.info("删除池塘: id={}", id);
    }
}
