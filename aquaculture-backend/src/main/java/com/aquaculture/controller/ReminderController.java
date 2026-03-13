package com.aquaculture.controller;

import com.aquaculture.dto.response.ApiResponse;
import com.aquaculture.entity.Reminder;
import com.aquaculture.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    @GetMapping
    public ApiResponse<List<Reminder>> getReminderList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Reminder> reminders = reminderService.getReminderList(userId);
        return ApiResponse.success(reminders);
    }

    @PostMapping
    public ApiResponse<Reminder> createReminder(HttpServletRequest request,
                                                 @RequestBody Reminder reminder) {
        Long userId = (Long) request.getAttribute("userId");
        Reminder created = reminderService.createReminder(userId, reminder);
        return ApiResponse.success("提醒创建成功", created);
    }

    @PutMapping("/{id}")
    public ApiResponse<Reminder> updateReminder(@PathVariable Long id,
                                                 @RequestBody Reminder reminder) {
        Reminder updated = reminderService.updateReminder(id, reminder);
        return ApiResponse.success("提醒更新成功", updated);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteReminder(@PathVariable Long id) {
        reminderService.deleteReminder(id);
        return ApiResponse.success("提醒删除成功", null);
    }

    @PutMapping("/{id}/complete")
    public ApiResponse<Void> markAsCompleted(@PathVariable Long id) {
        reminderService.markAsCompleted(id);
        return ApiResponse.success("提醒已标记完成", null);
    }

    /**
     * 手动触发发送提醒通知（用于测试或手动触发）
     */
    @PostMapping("/send-daily")
    public ApiResponse<Map<String, Object>> sendDailyReminders() {
        Map<String, Object> result = reminderService.sendDailyReminders();
        return ApiResponse.success(result);
    }
}
