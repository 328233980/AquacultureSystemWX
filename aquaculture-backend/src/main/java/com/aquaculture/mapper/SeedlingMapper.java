package com.aquaculture.mapper;

import com.aquaculture.entity.Seedling;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SeedlingMapper {
    
    @Select("SELECT * FROM seedling WHERE id = #{id}")
    Seedling findById(Long id);
    
    @Select("SELECT * FROM seedling WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Seedling> findByUserId(Long userId);
    
    @Insert("INSERT INTO seedling (user_id, name, category, species, supplier, default_price, feeding_cycle, avg_weight, " +
            "temp_min, temp_max, ph_min, ph_max, do_min, do_max, cycle_days, density, remark, created_at, updated_at) " +
            "VALUES (#{userId}, #{name}, #{category}, #{species}, #{supplier}, #{defaultPrice}, #{feedingCycle}, #{avgWeight}, " +
            "#{tempMin}, #{tempMax}, #{phMin}, #{phMax}, #{doMin}, #{doMax}, #{cycleDays}, #{density}, #{remark}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Seedling seedling);
    
    @Update("UPDATE seedling SET name = #{name}, category = #{category}, species = #{species}, supplier = #{supplier}, " +
            "default_price = #{defaultPrice}, feeding_cycle = #{feedingCycle}, avg_weight = #{avgWeight}, " +
            "temp_min = #{tempMin}, temp_max = #{tempMax}, ph_min = #{phMin}, ph_max = #{phMax}, " +
            "do_min = #{doMin}, do_max = #{doMax}, cycle_days = #{cycleDays}, density = #{density}, remark = #{remark}, updated_at = NOW() WHERE id = #{id}")
    int update(Seedling seedling);
    
    @Delete("DELETE FROM seedling WHERE id = #{id}")
    int deleteById(Long id);
}
