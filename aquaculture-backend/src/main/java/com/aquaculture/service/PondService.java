package com.aquaculture.service;

import com.aquaculture.dto.request.PondRequest;
import com.aquaculture.entity.Pond;

import java.util.List;

public interface PondService {
    Pond createPond(Long userId, PondRequest request);
    List<Pond> getPondList(Long userId, String status, String pondType);
    Pond getPondById(Long id);
    Pond updatePond(Long id, PondRequest request);
    void deletePond(Long id);
}
