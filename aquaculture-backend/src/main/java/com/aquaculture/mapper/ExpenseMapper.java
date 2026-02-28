package com.aquaculture.mapper;

import com.aquaculture.entity.Expense;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ExpenseMapper {
    
    @Select("SELECT * FROM expense WHERE id = #{id}")
    Expense findById(Long id);
    
    @Select("SELECT * FROM expense WHERE user_id = #{userId} ORDER BY expense_date DESC, created_at DESC")
    List<Expense> findByUserId(Long userId);
    
    @Select("SELECT * FROM expense WHERE user_id = #{userId} AND expense_date LIKE #{monthPrefix} ORDER BY expense_date DESC")
    List<Expense> findByUserIdAndMonth(@Param("userId") Long userId, @Param("monthPrefix") String monthPrefix);
    
    @Insert("INSERT INTO expense (user_id, category, category_label, amount, expense_date, description, created_at, updated_at) " +
            "VALUES (#{userId}, #{category}, #{categoryLabel}, #{amount}, #{expenseDate}, #{description}, datetime('now'), datetime('now'))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Expense expense);
    
    @Update("UPDATE expense SET category = #{category}, category_label = #{categoryLabel}, amount = #{amount}, " +
            "expense_date = #{expenseDate}, description = #{description}, updated_at = datetime('now') WHERE id = #{id}")
    int update(Expense expense);
    
    @Delete("DELETE FROM expense WHERE id = #{id}")
    int deleteById(Long id);
}
