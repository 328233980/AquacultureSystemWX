package com.aquaculture.mapper;

import com.aquaculture.entity.StockingRecord;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StockingMapper {
    
    @Select("SELECT * FROM stocking_record WHERE id = #{id}")
    StockingRecord findById(Long id);
    
    @Select("SELECT * FROM stocking_record WHERE pond_id = #{pondId} ORDER BY stocking_date DESC")
    List<StockingRecord> findByPondId(Long pondId);
    
    @Select("SELECT * FROM stocking_record WHERE pond_id = #{pondId} ORDER BY stocking_date DESC LIMIT 1")
    StockingRecord findLatestByPondId(Long pondId);
    
    List<StockingRecord> findByCondition(@Param("pondId") Long pondId, 
                                         @Param("startDate") LocalDate startDate, 
                                         @Param("endDate") LocalDate endDate);
    
    @Select("SELECT COUNT(DISTINCT pond_id) FROM stocking_record sr " +
            "JOIN pond p ON sr.pond_id = p.id " +
            "WHERE p.user_id = #{userId} AND p.status = 'active'")
    int countActiveBatchesByUserId(Long userId);
    
    @Insert("INSERT INTO stocking_record (pond_id, stocking_date, species, quantity, unit, avg_size, supplier, cost, survival_rate, remark, created_at, updated_at) " +
            "VALUES (#{pondId}, #{stockingDate}, #{species}, #{quantity}, #{unit}, #{avgSize}, #{supplier}, #{cost}, #{survivalRate}, #{remark}, datetime('now'), datetime('now'))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StockingRecord record);
    
    @Update("UPDATE stocking_record SET stocking_date = #{stockingDate}, species = #{species}, quantity = #{quantity}, " +
            "unit = #{unit}, avg_size = #{avgSize}, supplier = #{supplier}, cost = #{cost}, survival_rate = #{survivalRate}, " +
            "remark = #{remark}, updated_at = datetime('now') WHERE id = #{id}")
    int update(StockingRecord record);
    
    @Delete("DELETE FROM stocking_record WHERE id = #{id}")
    int deleteById(Long id);
}
