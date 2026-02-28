package com.aquaculture.mapper;

import com.aquaculture.entity.Customer;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CustomerMapper {
    
    @Select("SELECT * FROM customer WHERE id = #{id}")
    Customer findById(Long id);
    
    @Select("SELECT * FROM customer WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Customer> findByUserId(Long userId);
    
    @Insert("INSERT INTO customer (user_id, name, phone, address, remark, created_at, updated_at) " +
            "VALUES (#{userId}, #{name}, #{phone}, #{address}, #{remark}, datetime('now'), datetime('now'))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Customer customer);
    
    @Update("UPDATE customer SET name = #{name}, phone = #{phone}, address = #{address}, " +
            "remark = #{remark}, updated_at = datetime('now') WHERE id = #{id}")
    int update(Customer customer);
    
    @Delete("DELETE FROM customer WHERE id = #{id}")
    int deleteById(Long id);
}
