package com.aquaculture.mapper;

import com.aquaculture.entity.WaterQuality;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WaterQualityMapper {
    
    @Select("SELECT * FROM water_quality WHERE id = #{id}")
    WaterQuality findById(Long id);
    
    @Select("SELECT * FROM water_quality WHERE farming_log_id = #{farmingLogId}")
    WaterQuality findByFarmingLogId(Long farmingLogId);
    
    @Select("SELECT * FROM water_quality WHERE pond_id = #{pondId} ORDER BY test_time DESC LIMIT #{limit}")
    List<WaterQuality> findRecentByPondId(@Param("pondId") Long pondId, @Param("limit") int limit);
    
    @Insert("INSERT INTO water_quality (farming_log_id, pond_id, test_time, water_temp, ph_value, dissolved_oxygen, " +
            "ammonia_nitrogen, nitrite, salinity, transparency, remark, created_at) " +
            "VALUES (#{farmingLogId}, #{pondId}, #{testTime}, #{waterTemp}, #{phValue}, #{dissolvedOxygen}, " +
            "#{ammoniaNitrogen}, #{nitrite}, #{salinity}, #{transparency}, #{remark}, datetime('now'))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WaterQuality waterQuality);
    
    @Update("UPDATE water_quality SET water_temp = #{waterTemp}, ph_value = #{phValue}, dissolved_oxygen = #{dissolvedOxygen}, " +
            "ammonia_nitrogen = #{ammoniaNitrogen}, nitrite = #{nitrite}, salinity = #{salinity}, transparency = #{transparency}, " +
            "remark = #{remark} WHERE id = #{id}")
    int update(WaterQuality waterQuality);
    
    @Delete("DELETE FROM water_quality WHERE farming_log_id = #{farmingLogId}")
    int deleteByFarmingLogId(Long farmingLogId);
}
