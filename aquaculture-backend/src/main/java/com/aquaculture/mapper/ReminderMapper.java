package com.aquaculture.mapper;

import com.aquaculture.entity.Reminder;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ReminderMapper {
    
    @Select("SELECT * FROM reminder WHERE id = #{id}")
    Reminder findById(Long id);
    
    @Select("SELECT r.*, p.pond_name FROM reminder r " +
            "LEFT JOIN pond p ON r.pond_id = p.id " +
            "WHERE r.user_id = #{userId} AND r.status = 'pending' AND r.remind_date <= #{endDate} " +
            "ORDER BY r.remind_date ASC")
    List<Reminder> findPendingByUserId(@Param("userId") Long userId, @Param("endDate") LocalDate endDate);
    
    /**
     * 查询指定日期需要发送的所有提醒（状态为pending）
     */
    @Select("SELECT r.*, p.pond_name FROM reminder r " +
            "LEFT JOIN pond p ON r.pond_id = p.id " +
            "WHERE r.status = 'pending' AND r.remind_date = #{date} " +
            "ORDER BY r.remind_date ASC")
    List<Reminder> findRemindersForDate(@Param("date") LocalDate date);
    
    @Select("SELECT r.*, p.pond_name FROM reminder r " +
            "LEFT JOIN pond p ON r.pond_id = p.id " +
            "WHERE r.user_id = #{userId} ORDER BY r.remind_date DESC")
    List<Reminder> findByUserId(Long userId);
    
    @Insert("INSERT INTO reminder (user_id, pond_id, reminder_type, title, content, remind_date, status, created_at, updated_at) " +
            "VALUES (#{userId}, #{pondId}, #{reminderType}, #{title}, #{content}, #{remindDate}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Reminder reminder);
    
    @Update("UPDATE reminder SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    @Delete("DELETE FROM reminder WHERE id = #{id}")
    int deleteById(Long id);
}
