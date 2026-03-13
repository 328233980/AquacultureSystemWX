package com.aquaculture.service;

import com.aquaculture.dto.request.MedicationRequest;
import com.aquaculture.entity.Medication;

import java.time.LocalDate;
import java.util.List;

public interface MedicationService {
    Medication createMedication(Long userId, MedicationRequest request);
    List<Medication> getMedicationList(Long userId, Long pondId, LocalDate startDate, LocalDate endDate, Boolean inWithdrawalPeriod);
    Medication getMedicationById(Long id);
    Medication updateMedication(Long id, MedicationRequest request);
    void deleteMedication(Long id);
}
