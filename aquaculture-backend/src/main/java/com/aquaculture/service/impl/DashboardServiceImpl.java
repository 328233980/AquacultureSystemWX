package com.aquaculture.service.impl;

import com.aquaculture.dto.response.DashboardResponse;
import com.aquaculture.entity.FarmingLog;
import com.aquaculture.entity.Medication;
import com.aquaculture.entity.Pond;
import com.aquaculture.entity.Reminder;
import com.aquaculture.mapper.*;
import com.aquaculture.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private PondMapper pondMapper;

    @Autowired
    private StockingMapper stockingMapper;

    @Autowired
    private FarmingLogMapper farmingLogMapper;

    @Autowired
    private MedicationMapper medicationMapper;

    @Autowired
    private ReminderMapper reminderMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public DashboardResponse getDashboardData(Long userId) {
        DashboardResponse response = new DashboardResponse();
        
        // 统计数据
        DashboardResponse.Statistics statistics = new DashboardResponse.Statistics();
        statistics.setTotalPonds(pondMapper.countByUserId(userId));
        statistics.setActivePonds(pondMapper.countActiveByUserId(userId));
        statistics.setCurrentBatches(stockingMapper.countActiveBatchesByUserId(userId));
        statistics.setTodayFeeding(farmingLogMapper.countTodayFeedingByUserId(userId, LocalDate.now()));
        response.setStatistics(statistics);

        // 待办提醒（未来7天）
        LocalDate endDate = LocalDate.now().plusDays(7);
        List<Reminder> reminders = reminderMapper.findPendingByUserId(userId, endDate);
        List<DashboardResponse.ReminderItem> reminderItems = new ArrayList<>();
        for (Reminder r : reminders) {
            DashboardResponse.ReminderItem item = new DashboardResponse.ReminderItem();
            item.setId(r.getId());
            item.setType(r.getReminderType());
            item.setTitle(r.getTitle());
            item.setDate(r.getRemindDate().format(DATE_FORMATTER));
            reminderItems.add(item);
        }
        response.setReminders(reminderItems);

        // 最近日志
        List<FarmingLog> logs = farmingLogMapper.findRecentByUserId(userId, 5);
        List<DashboardResponse.RecentLog> recentLogs = new ArrayList<>();
        for (FarmingLog log : logs) {
            DashboardResponse.RecentLog rl = new DashboardResponse.RecentLog();
            rl.setId(log.getId());
            rl.setLogDate(log.getLogDate().format(DATE_FORMATTER));
            rl.setWeather(log.getWeather());
            if (log.getFeedingAmount() != null) {
                rl.setFeedingAmount(log.getFeedingAmount() + "kg");
            }
            // 获取池塘名称
            Pond pond = pondMapper.findById(log.getPondId());
            if (pond != null) {
                rl.setPondName(pond.getPondName());
            }
            recentLogs.add(rl);
        }
        response.setRecentLogs(recentLogs);

        // 预警信息
        List<DashboardResponse.Alert> alerts = new ArrayList<>();
        
        // 检查休药期到期提醒
        List<Medication> medications = medicationMapper.findInWithdrawalPeriodByUserId(userId, LocalDate.now());
        for (Medication m : medications) {
            if (m.getWithdrawalEndDate() != null) {
                long daysLeft = LocalDate.now().until(m.getWithdrawalEndDate()).getDays();
                if (daysLeft <= 3) {
                    DashboardResponse.Alert alert = new DashboardResponse.Alert();
                    alert.setType(daysLeft <= 1 ? "danger" : "warning");
                    alert.setMessage("休药期提醒：" + m.getDrugName() + "还有" + daysLeft + "天结束");
                    alert.setPondId(m.getPondId());
                    alerts.add(alert);
                }
            }
        }
        response.setAlerts(alerts);

        return response;
    }
}
