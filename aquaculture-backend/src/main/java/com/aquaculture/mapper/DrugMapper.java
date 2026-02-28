package com.aquaculture.mapper;

import com.aquaculture.entity.Drug;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DrugMapper {
    
    @Select("SELECT * FROM drug WHERE id = #{id}")
    Drug findById(Long id);
    
    @Select("SELECT * FROM drug WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Drug> findByUserId(Long userId);
    
    @Insert("INSERT INTO drug (user_id, name, drug_type, unit, default_price, remark, created_at, updated_at) " +
            "VALUES (#{userId}, #{name}, #{drugType}, #{unit}, #{defaultPrice}, #{remark}, datetime('now'), datetime('now'))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Drug drug);
    
    @Update("UPDATE drug SET name = #{name}, drug_type = #{drugType}, unit = #{unit}, " +
            "default_price = #{defaultPrice}, remark = #{remark}, updated_at = datetime('now') WHERE id = #{id}")
    int update(Drug drug);
    
    @Delete("DELETE FROM drug WHERE id = #{id}")
    int deleteById(Long id);
}
