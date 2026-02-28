package com.aquaculture.service.impl;

import com.aquaculture.dto.request.SupplierRequest;
import com.aquaculture.entity.Supplier;
import com.aquaculture.exception.BusinessException;
import com.aquaculture.mapper.SupplierMapper;
import com.aquaculture.service.SupplierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class SupplierServiceImpl implements SupplierService {
    @Autowired
    private SupplierMapper supplierMapper;

    @Override
    public Supplier create(Long userId, SupplierRequest request) {
        Supplier supplier = new Supplier();
        supplier.setUserId(userId);
        supplier.setName(request.getName());
        supplier.setPhone(request.getPhone());
        supplier.setSupplyTypes(request.getSupplyTypes());
        supplier.setAddress(request.getAddress());
        supplier.setRemark(request.getRemark());
        supplierMapper.insert(supplier);
        log.info("创建供应商配置: id={}, name={}", supplier.getId(), supplier.getName());
        return supplier;
    }

    @Override
    public List<Supplier> getList(Long userId) {
        return supplierMapper.findByUserId(userId);
    }

    @Override
    public Supplier getById(Long id) {
        Supplier supplier = supplierMapper.findById(id);
        if (supplier == null) {
            throw new BusinessException(404, "供应商不存在");
        }
        return supplier;
    }

    @Override
    public Supplier update(Long id, SupplierRequest request) {
        Supplier supplier = getById(id);
        supplier.setName(request.getName());
        supplier.setPhone(request.getPhone());
        supplier.setSupplyTypes(request.getSupplyTypes());
        supplier.setAddress(request.getAddress());
        supplier.setRemark(request.getRemark());
        supplierMapper.update(supplier);
        log.info("更新供应商配置: id={}", id);
        return supplier;
    }

    @Override
    public void delete(Long id) {
        getById(id);
        supplierMapper.deleteById(id);
        log.info("删除供应商配置: id={}", id);
    }
}
