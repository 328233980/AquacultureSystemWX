package com.aquaculture.mapper;

import com.aquaculture.entity.Medication;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface MedicationMapper {
    
    @Select("SELECT * FROM medication WHERE id = #{id}")
    Medication findById(Long id);
    
    @Select("SELECT * FROM medication WHERE pond_id = #{pondId} ORDER BY medication_date DESC")
    List<Medication> findByPondId(Long pondId);
    
    List<Medication> findByCondition(@Param("pondId") Long pondId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate,
                                     @Param("inWithdrawalPeriod") Boolean inWithdrawalPeriod,
                                     @Param("today") LocalDate today);
    
    @Select("SELECT m.*, p.pond_name FROM medication m " +
            "JOIN pond p ON m.pond_id = p.id " +
            "WHERE p.user_id = #{userId} AND m.withdrawal_end_date >= #{today} " +
            "ORDER BY m.withdrawal_end_date ASC")
    List<Medication> findInWithdrawalPeriodByUserId(@Param("userId") Long userId, @Param("today") LocalDate today);
    
    @Insert("INSERT INTO medication (pond_id, medication_date, drug_name, drug_type, dosage, dosage_unit, cost, purpose, " +
            "target_disease, withdrawal_period, withdrawal_end_date, operator, remark, created_at, updated_at) " +
            "VALUES (#{pondId}, #{medicationDate}, #{drugName}, #{drugType}, #{dosage}, #{dosageUnit}, #{cost}, #{purpose}, " +
            "#{targetDisease}, #{withdrawalPeriod}, #{withdrawalEndDate}, #{operator}, #{remark}, datetime('now'), datetime('now'))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Medication medication);
    
    @Update("UPDATE medication SET medication_date = #{medicationDate}, drug_name = #{drugName}, drug_type = #{drugType}, " +
            "dosage = #{dosage}, dosage_unit = #{dosageUnit}, cost = #{cost}, purpose = #{purpose}, target_disease = #{targetDisease}, " +
            "withdrawal_period = #{withdrawalPeriod}, withdrawal_end_date = #{withdrawalEndDate}, operator = #{operator}, " +
            "remark = #{remark}, updated_at = datetime('now') WHERE id = #{id}")
    int update(Medication medication);
    
    @Delete("DELETE FROM medication WHERE id = #{id}")
    int deleteById(Long id);
}
