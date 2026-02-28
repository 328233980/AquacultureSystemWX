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
    
    @Insert("INSERT INTO seedling (user_id, name, species, supplier, default_price, feeding_cycle, remark, created_at, updated_at) " +
            "VALUES (#{userId}, #{name}, #{species}, #{supplier}, #{defaultPrice}, #{feedingCycle}, #{remark}, datetime('now'), datetime('now'))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Seedling seedling);
    
    @Update("UPDATE seedling SET name = #{name}, species = #{species}, supplier = #{supplier}, " +
            "default_price = #{defaultPrice}, feeding_cycle = #{feedingCycle}, remark = #{remark}, updated_at = datetime('now') WHERE id = #{id}")
    int update(Seedling seedling);
    
    @Delete("DELETE FROM seedling WHERE id = #{id}")
    int deleteById(Long id);
}
