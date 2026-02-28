package com.aquaculture.mapper;

import com.aquaculture.entity.Equipment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EquipmentMapper {
    
    @Select("SELECT * FROM equipment WHERE id = #{id}")
    Equipment findById(Long id);
    
    @Select("SELECT * FROM equipment WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Equipment> findByUserId(Long userId);
    
    @Select("SELECT * FROM equipment WHERE user_id = #{userId} AND pond_id = #{pondId} ORDER BY created_at DESC")
    List<Equipment> findByUserIdAndPondId(@Param("userId") Long userId, @Param("pondId") Long pondId);
    
    @Insert("INSERT INTO equipment (user_id, pond_id, pond_name, name, original_value, monthly_depreciation, purchase_date, remark, created_at, updated_at) " +
            "VALUES (#{userId}, #{pondId}, #{pondName}, #{name}, #{originalValue}, #{monthlyDepreciation}, #{purchaseDate}, #{remark}, datetime('now'), datetime('now'))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Equipment equipment);
    
    @Update("UPDATE equipment SET pond_id = #{pondId}, pond_name = #{pondName}, name = #{name}, original_value = #{originalValue}, " +
            "monthly_depreciation = #{monthlyDepreciation}, purchase_date = #{purchaseDate}, remark = #{remark}, updated_at = datetime('now') WHERE id = #{id}")
    int update(Equipment equipment);
    
    @Delete("DELETE FROM equipment WHERE id = #{id}")
    int deleteById(Long id);
}
