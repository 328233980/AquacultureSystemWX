package com.aquaculture.mapper;

import com.aquaculture.entity.Harvest;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HarvestMapper {
    
    @Select("SELECT * FROM harvest WHERE id = #{id}")
    Harvest findById(Long id);
    
    @Select("SELECT * FROM harvest WHERE pond_id = #{pondId} ORDER BY harvest_date DESC")
    List<Harvest> findByPondId(Long pondId);
    
    List<Harvest> findByCondition(@Param("userId") Long userId,
                                  @Param("pondId") Long pondId,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);
    
    @Select("SELECT COALESCE(SUM(quantity), 0) FROM harvest h " +
            "JOIN pond p ON h.pond_id = p.id " +
            "WHERE p.user_id = #{userId}")
    BigDecimal sumQuantityByUserId(Long userId);
    
    @Select("SELECT COALESCE(SUM(total_revenue), 0) FROM harvest h " +
            "JOIN pond p ON h.pond_id = p.id " +
            "WHERE p.user_id = #{userId}")
    BigDecimal sumRevenueByUserId(Long userId);
    
    @Insert("INSERT INTO harvest (user_id, pond_id, harvest_date, harvest_type, quantity, avg_weight, total_count, mortality, grade_a, grade_b, grade_c, " +
            "price_per_kg, total_revenue, buyer, destination, remark, created_at, updated_at) " +
            "VALUES (#{userId}, #{pondId}, #{harvestDate}, #{harvestType}, #{quantity}, #{avgWeight}, #{totalCount}, #{mortality}, #{gradeA}, #{gradeB}, #{gradeC}, " +
            "#{pricePerKg}, #{totalRevenue}, #{buyer}, #{destination}, #{remark}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Harvest harvest);
    
    @Update("UPDATE harvest SET harvest_date = #{harvestDate}, harvest_type = #{harvestType}, quantity = #{quantity}, " +
            "avg_weight = #{avgWeight}, total_count = #{totalCount}, mortality = #{mortality}, grade_a = #{gradeA}, grade_b = #{gradeB}, grade_c = #{gradeC}, " +
            "price_per_kg = #{pricePerKg}, total_revenue = #{totalRevenue}, buyer = #{buyer}, destination = #{destination}, " +
            "remark = #{remark}, updated_at = NOW() WHERE id = #{id}")
    int update(Harvest harvest);
    
    @Delete("DELETE FROM harvest WHERE id = #{id}")
    int deleteById(Long id);
}
