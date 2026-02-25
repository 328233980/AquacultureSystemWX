package com.aquaculture.mapper;

import com.aquaculture.entity.FarmingLog;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface FarmingLogMapper {
    
    @Select("SELECT * FROM farming_log WHERE id = #{id}")
    FarmingLog findById(Long id);
    
    @Select("SELECT * FROM farming_log WHERE pond_id = #{pondId} ORDER BY log_date DESC")
    List<FarmingLog> findByPondId(Long pondId);
    
    @Select("SELECT * FROM farming_log WHERE pond_id = #{pondId} ORDER BY log_date DESC LIMIT #{limit}")
    List<FarmingLog> findRecentByPondId(@Param("pondId") Long pondId, @Param("limit") int limit);
    
    List<FarmingLog> findByCondition(@Param("pondId") Long pondId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);
    
    int countByCondition(@Param("pondId") Long pondId,
                         @Param("startDate") LocalDate startDate,
                         @Param("endDate") LocalDate endDate);
    
    @Select("SELECT COUNT(*) FROM farming_log fl " +
            "JOIN pond p ON fl.pond_id = p.id " +
            "WHERE p.user_id = #{userId} AND fl.log_date = #{logDate}")
    int countTodayFeedingByUserId(@Param("userId") Long userId, @Param("logDate") LocalDate logDate);
    
    @Select("SELECT fl.*, p.pond_name FROM farming_log fl " +
            "JOIN pond p ON fl.pond_id = p.id " +
            "WHERE p.user_id = #{userId} ORDER BY fl.log_date DESC, fl.created_at DESC LIMIT #{limit}")
    List<FarmingLog> findRecentByUserId(@Param("userId") Long userId, @Param("limit") int limit);
    
    @Insert("INSERT INTO farming_log (pond_id, log_date, weather, temperature, feeding_amount, feeding_type, mortality, " +
            "abnormal_behavior, remark, created_by, created_at, updated_at) " +
            "VALUES (#{pondId}, #{logDate}, #{weather}, #{temperature}, #{feedingAmount}, #{feedingType}, #{mortality}, " +
            "#{abnormalBehavior}, #{remark}, #{createdBy}, datetime('now'), datetime('now'))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FarmingLog log);
    
    @Update("UPDATE farming_log SET log_date = #{logDate}, weather = #{weather}, temperature = #{temperature}, " +
            "feeding_amount = #{feedingAmount}, feeding_type = #{feedingType}, mortality = #{mortality}, " +
            "abnormal_behavior = #{abnormalBehavior}, remark = #{remark}, updated_at = datetime('now') WHERE id = #{id}")
    int update(FarmingLog log);
    
    @Delete("DELETE FROM farming_log WHERE id = #{id}")
    int deleteById(Long id);
}
