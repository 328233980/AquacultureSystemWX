package com.aquaculture.mapper;

import com.aquaculture.entity.Supplier;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SupplierMapper {
    
    @Select("SELECT * FROM supplier WHERE id = #{id}")
    Supplier findById(Long id);
    
    @Select("SELECT * FROM supplier WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Supplier> findByUserId(Long userId);
    
    @Insert("INSERT INTO supplier (user_id, name, phone, supply_types, address, remark, created_at, updated_at) " +
            "VALUES (#{userId}, #{name}, #{phone}, #{supplyTypes}, #{address}, #{remark}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Supplier supplier);
    
    @Update("UPDATE supplier SET name = #{name}, phone = #{phone}, supply_types = #{supplyTypes}, " +
            "address = #{address}, remark = #{remark}, updated_at = NOW() WHERE id = #{id}")
    int update(Supplier supplier);
    
    @Delete("DELETE FROM supplier WHERE id = #{id}")
    int deleteById(Long id);
}
