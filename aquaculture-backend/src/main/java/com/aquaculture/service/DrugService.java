package com.aquaculture.service;

import com.aquaculture.dto.request.DrugRequest;
import com.aquaculture.entity.Drug;
import java.util.List;

public interface DrugService {
    Drug create(Long userId, DrugRequest request);
    List<Drug> getList(Long userId);
    Drug getById(Long id);
    Drug update(Long id, DrugRequest request);
    void delete(Long id);
}
