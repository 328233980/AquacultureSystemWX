package com.aquaculture.service.impl;

import com.aquaculture.entity.Pond;
import com.aquaculture.entity.Reminder;
import com.aquaculture.entity.User;
import com.aquaculture.exception.BusinessException;
import com.aquaculture.mapper.PondMapper;
import com.aquaculture.mapper.ReminderMapper;
import com.aquaculture.mapper.UserMapper;
import com.aquaculture.service.ReminderService;
import com.aquaculture.util.WxApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class ReminderServiceImpl implements ReminderService {

    @Autowired
    private ReminderMapper reminderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PondMapper pondMapper;

    @Autowired
    private WxApiUtil wxApiUtil;

    // 订阅消息模板ID - 需要在微信公众平台申请后配置
    @Value("${wx.miniapp.template.reminder:}")
    private String reminderTemplateId;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");

    @Override
    public List<Reminder> getReminderList(Long userId) {
        return reminderMapper.findByUserId(userId);
    }

    @Override
    public Reminder createReminder(Long userId, Reminder reminder) {
        reminder.setUserId(userId);
        reminder.setStatus("pending");
        reminderMapper.insert(reminder);
        log.info("创建提醒: userId={}, title={}, remindDate={}", userId, reminder.getTitle(), reminder.getRemindDate());
        return reminder;
    }

    @Override
    public Reminder updateReminder(Long id, Reminder reminder) {
        Reminder existing = reminderMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(404, "提醒不存在");
        }
        reminder.setId(id);
        reminderMapper.updateStatus(id, reminder.getStatus());
        return reminder;
    }

    @Override
    public void deleteReminder(Long id) {
        Reminder existing = reminderMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(404, "提醒不存在");
        }
        reminderMapper.deleteById(id);
        log.info("删除提醒: id={}", id);
    }

    @Override
    public void markAsCompleted(Long id) {
        Reminder existing = reminderMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(404, "提醒不存在");
        }
        reminderMapper.updateStatus(id, "completed");
        log.info("标记提醒完成: id={}", id);
    }

    @Override
    public boolean sendReminderNotification(Long reminderId) {
        Reminder reminder = reminderMapper.findById(reminderId);
        if (reminder == null) {
            log.error("提醒不存在: id={}", reminderId);
            return false;
        }

        User user = userMapper.findById(reminder.getUserId());
        if (user == null || user.getOpenid() == null) {
            log.error("用户不存在或openid为空: userId={}", reminder.getUserId());
            return false;
        }

        if (reminderTemplateId == null || reminderTemplateId.isEmpty()) {
            log.error("订阅消息模板ID未配置");
            return false;
        }

        // 构建模板数据
        // 模板字段：消息类型(thing1)、消息时间(time5)、隐患问题(thing8)、温馨提示(thing7)
        Map<String, String> data = new HashMap<>();
        
        // 1. 消息类型 - 根据提醒类型显示
        String messageType = getReminderType(reminder.getReminderType());
        data.put("thing1", truncate(messageType, 20));
        
        // 2. 消息时间 - 当前发送时间
        data.put("time5", LocalDateTime.now().format(TIME_FORMATTER));
        
        // 3. 隐患问题 - 提醒标题和内容
        String problem = reminder.getTitle();
        if (reminder.getContent() != null && !reminder.getContent().isEmpty()) {
            problem = reminder.getTitle() + "：" + reminder.getContent();
        }
        data.put("thing8", truncate(problem, 20));
        
        // 4. 温馨提示
        String tip = getWarmTip(reminder);
        data.put("thing7", truncate(tip, 20));

        // 跳转页面
        String page = "pages/dashboard/dashboard";

        boolean success = wxApiUtil.sendSubscribeMessage(user.getOpenid(), reminderTemplateId, data, page);
        
        if (success) {
            log.info("提醒通知发送成功: reminderId={}, userId={}", reminderId, reminder.getUserId());
        }
        
        return success;
    }

    /**
     * 获取提醒类型名称
     */
    private String getReminderType(String reminderType) {
        if (reminderType == null) {
            return "其他提醒";
        }
        switch (reminderType) {
            case "medication":
                return "喂药提醒";
            case "feeding":
                return "喂养提醒";
            case "harvest":
                return "捕捞提醒";
            case "water":
                return "水质提醒";
            default:
                return "其他提醒";
        }
    }

    /**
     * 获取温馨提示内容
     */
    private String getWarmTip(Reminder reminder) {
        String type = reminder.getReminderType();
        
        if ("medication".equals(type)) {
            return "请注意休药期，确保水产品质量安全";
        } else if ("feeding".equals(type)) {
            return "定时定量投喂，观察摄食情况";
        } else if ("harvest".equals(type)) {
            return "捕捞前请确认已过休药期";
        } else if ("water".equals(type)) {
            return "定期检测水质，保持良好养殖环境";
        }
        
        // 默认提示
        if (reminder.getPondId() != null) {
            Pond pond = pondMapper.findById(reminder.getPondId());
            if (pond != null) {
                return "池塘：" + pond.getPondName();
            }
        }
        
        return "请及时处理，避免影响养殖效益";
    }

    @Override
    // 每天早上8点执行
    @Scheduled(cron = "0 0 8 * * ?")
    public Map<String, Object> sendDailyReminders() {
        log.info("开始发送每日提醒通知...");
        
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        
        // 查询今天需要发送的提醒
        LocalDate today = LocalDate.now();
        List<Reminder> reminders = reminderMapper.findRemindersForDate(today);
        
        for (Reminder reminder : reminders) {
            boolean success = sendReminderNotification(reminder.getId());
            if (success) {
                successCount++;
            } else {
                failCount++;
            }
        }
        
        result.put("success", successCount);
        result.put("fail", failCount);
        result.put("total", successCount + failCount);
        
        log.info("每日提醒发送完成: 成功={}, 失败={}", successCount, failCount);
        return result;
    }

    /**
     * 截断字符串到指定长度
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 1) + "…";
    }
}
