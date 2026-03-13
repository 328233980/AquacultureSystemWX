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

    // 查询指定用户所有池塘最新的一条水质记录
    @Select("SELECT w.* FROM water_quality w " +
            "JOIN pond p ON w.pond_id = p.id " +
            "WHERE p.user_id = #{userId} " +
            "ORDER BY w.test_time DESC LIMIT 1")
    WaterQuality findLatestByUserId(Long userId);
    
    @Insert("INSERT INTO water_quality (user_id, farming_log_id, pond_id, test_time, water_temp, ph_value, dissolved_oxygen, " +
            "ammonia_nitrogen, nitrite, salinity, transparency, remark, created_at) " +
            "VALUES (#{userId}, #{farmingLogId}, #{pondId}, #{testTime}, #{waterTemp}, #{phValue}, #{dissolvedOxygen}, " +
            "#{ammoniaNitrogen}, #{nitrite}, #{salinity}, #{transparency}, #{remark}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WaterQuality waterQuality);
    
    @Update("UPDATE water_quality SET water_temp = #{waterTemp}, ph_value = #{phValue}, dissolved_oxygen = #{dissolvedOxygen}, " +
            "ammonia_nitrogen = #{ammoniaNitrogen}, nitrite = #{nitrite}, salinity = #{salinity}, transparency = #{transparency}, " +
            "remark = #{remark} WHERE id = #{id}")
    int update(WaterQuality waterQuality);
    
    @Delete("DELETE FROM water_quality WHERE farming_log_id = #{farmingLogId}")
    int deleteByFarmingLogId(Long farmingLogId);
}
