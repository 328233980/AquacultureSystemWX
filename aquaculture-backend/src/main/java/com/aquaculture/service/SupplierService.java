package com.aquaculture.service;

import com.aquaculture.dto.request.SupplierRequest;
import com.aquaculture.entity.Supplier;
import java.util.List;

public interface SupplierService {
    Supplier create(Long userId, SupplierRequest request);
    List<Supplier> getList(Long userId);
    Supplier getById(Long id);
    Supplier update(Long id, SupplierRequest request);
    void delete(Long id);
}
