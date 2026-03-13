package com.aquaculture.service;

import com.aquaculture.entity.Reminder;

import java.util.List;
import java.util.Map;

public interface ReminderService {
    List<Reminder> getReminderList(Long userId);
    Reminder createReminder(Long userId, Reminder reminder);
    Reminder updateReminder(Long id, Reminder reminder);
    void deleteReminder(Long id);
    void markAsCompleted(Long id);
    
    /**
     * 发送当日提醒通知
     * @return 发送结果统计
     */
    Map<String, Object> sendDailyReminders();
    
    /**
     * 发送单个提醒通知
     * @param reminderId 提醒ID
     * @return 是否发送成功
     */
    boolean sendReminderNotification(Long reminderId);
}
