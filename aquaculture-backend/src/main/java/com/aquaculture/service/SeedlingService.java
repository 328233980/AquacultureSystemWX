package com.aquaculture.service;

import com.aquaculture.dto.request.SeedlingRequest;
import com.aquaculture.entity.Seedling;
import java.util.List;

public interface SeedlingService {
    Seedling create(Long userId, SeedlingRequest request);
    List<Seedling> getList(Long userId);
    Seedling getById(Long id);
    Seedling update(Long id, SeedlingRequest request);
    void delete(Long id);
}
