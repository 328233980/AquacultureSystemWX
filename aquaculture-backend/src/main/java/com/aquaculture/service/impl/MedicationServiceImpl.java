package com.aquaculture.service.impl;

import com.aquaculture.dto.request.MedicationRequest;
import com.aquaculture.entity.Medication;
import com.aquaculture.entity.Pond;
import com.aquaculture.entity.Reminder;
import com.aquaculture.exception.BusinessException;
import com.aquaculture.mapper.MedicationMapper;
import com.aquaculture.mapper.PondMapper;
import com.aquaculture.mapper.ReminderMapper;
import com.aquaculture.service.MedicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class MedicationServiceImpl implements MedicationService {

    @Autowired
    private MedicationMapper medicationMapper;

    @Autowired
    private PondMapper pondMapper;

    @Autowired
    private ReminderMapper reminderMapper;

    @Override
    @Transactional
    public Medication createMedication(Long userId, MedicationRequest request) {
        Pond pond = pondMapper.findById(request.getPondId());
        if (pond == null) {
            throw new BusinessException(404, "池塘不存在");
        }
        if (!userId.equals(pond.getUserId())) {
            throw new BusinessException(403, "无权操作此池塘");
        }

        Medication medication = new Medication();
        medication.setUserId(userId);
        medication.setPondId(request.getPondId());
        medication.setMedicationDate(request.getMedicationDate());
        medication.setDrugName(request.getDrugName());
        medication.setDrugType(request.getDrugType());
        medication.setDosage(request.getDosage());
        medication.setDosageUnit(request.getDosageUnit());
        medication.setCost(request.getCost());
        medication.setPurpose(request.getPurpose());
        medication.setTargetDisease(request.getTargetDisease());
        medication.setWithdrawalPeriod(request.getWithdrawalPeriod());
        medication.setOperator(request.getOperator());
        medication.setRemark(request.getRemark());

        // 计算休药期结束日期
        if (request.getWithdrawalPeriod() != null && request.getWithdrawalPeriod() > 0) {
            LocalDate endDate = request.getMedicationDate().plusDays(request.getWithdrawalPeriod());
            medication.setWithdrawalEndDate(endDate);

            // 创建休药期结束提醒
            Reminder reminder = new Reminder();
            reminder.setUserId(userId);
            reminder.setPondId(request.getPondId());
            reminder.setReminderType("medication");
            reminder.setTitle(pond.getPondName() + " - " + request.getDrugName() + " 休药期结束");
            reminder.setContent("药品：" + request.getDrugName() + "，休药期结束，可以进行捕捞");
            reminder.setRemindDate(endDate);
            reminder.setStatus("pending");
            reminderMapper.insert(reminder);
        }

        medicationMapper.insert(medication);
        log.info("创建用药记录: id={}, drugName={}, userId={}", medication.getId(), medication.getDrugName(), userId);

        return medication;
    }

    @Override
    public List<Medication> getMedicationList(Long userId, Long pondId, LocalDate startDate, LocalDate endDate, Boolean inWithdrawalPeriod) {
        return medicationMapper.findByCondition(userId, pondId, startDate, endDate, inWithdrawalPeriod, LocalDate.now());
    }

    @Override
    public Medication getMedicationById(Long id) {
        Medication medication = medicationMapper.findById(id);
        if (medication == null) {
            throw new BusinessException(404, "用药记录不存在");
        }
        return medication;
    }

    @Override
    public Medication updateMedication(Long id, MedicationRequest request) {
        Medication medication = getMedicationById(id);

        medication.setMedicationDate(request.getMedicationDate());
        medication.setDrugName(request.getDrugName());
        medication.setDrugType(request.getDrugType());
        medication.setDosage(request.getDosage());
        medication.setDosageUnit(request.getDosageUnit());
        medication.setCost(request.getCost());
        medication.setPurpose(request.getPurpose());
        medication.setTargetDisease(request.getTargetDisease());
        medication.setWithdrawalPeriod(request.getWithdrawalPeriod());
        medication.setOperator(request.getOperator());
        medication.setRemark(request.getRemark());

        // 重新计算休药期结束日期
        if (request.getWithdrawalPeriod() != null && request.getWithdrawalPeriod() > 0) {
            medication.setWithdrawalEndDate(request.getMedicationDate().plusDays(request.getWithdrawalPeriod()));
        } else {
            medication.setWithdrawalEndDate(null);
        }

        medicationMapper.update(medication);
        log.info("更新用药记录: id={}", id);

        return medication;
    }

    @Override
    public void deleteMedication(Long id) {
        getMedicationById(id);
        medicationMapper.deleteById(id);
        log.info("删除用药记录: id={}", id);
    }
}
