package com.aquaculture.mapper;

import com.aquaculture.entity.Pond;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PondMapper {
    
    @Select("SELECT * FROM pond WHERE id = #{id}")
    Pond findById(Long id);
    
    @Select("SELECT * FROM pond WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Pond> findByUserId(Long userId);
    
    @Select("SELECT * FROM pond WHERE user_id = #{userId} AND status = #{status} ORDER BY created_at DESC")
    List<Pond> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
    
    @Select("SELECT * FROM pond WHERE user_id = #{userId} AND pond_type = #{pondType} ORDER BY created_at DESC")
    List<Pond> findByUserIdAndType(@Param("userId") Long userId, @Param("pondType") String pondType);
    
    @Select("SELECT COUNT(*) FROM pond WHERE user_id = #{userId}")
    int countByUserId(Long userId);
    
    @Select("SELECT COUNT(*) FROM pond WHERE user_id = #{userId} AND status = 'active'")
    int countActiveByUserId(Long userId);
    
    @Insert("INSERT INTO pond (user_id, pond_name, pond_type, area, depth, location, cycle_days, density, status, remark, created_at, updated_at) " +
            "VALUES (#{userId}, #{pondName}, #{pondType}, #{area}, #{depth}, #{location}, #{cycleDays}, #{density}, #{status}, #{remark}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Pond pond);
    
    @Update("UPDATE pond SET pond_name = #{pondName}, pond_type = #{pondType}, area = #{area}, depth = #{depth}, " +
            "location = #{location}, cycle_days = #{cycleDays}, density = #{density}, status = #{status}, remark = #{remark}, updated_at = NOW() WHERE id = #{id}")
    int update(Pond pond);
    
    @Update("UPDATE pond SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    @Delete("DELETE FROM pond WHERE id = #{id}")
    int deleteById(Long id);
}
