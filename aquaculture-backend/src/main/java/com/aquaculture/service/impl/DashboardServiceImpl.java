package com.aquaculture.service.impl;

import com.aquaculture.dto.response.DashboardResponse;
import com.aquaculture.entity.*;
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

    @Autowired
    private WaterQualityMapper waterQualityMapper;

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
            // 获取池塘名称
            if (r.getPondId() != null) {
                Pond pond = pondMapper.findById(r.getPondId());
                if (pond != null) {
                    item.setPondName(pond.getPondName());
                }
            }
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

        // 检查休药期预警 - 显示"距离可捕捞/可再次投药还有X天"
        List<Medication> medications = medicationMapper.findInWithdrawalPeriodByUserId(userId, LocalDate.now());
        for (Medication m : medications) {
            if (m.getWithdrawalEndDate() != null) {
                long daysLeft = LocalDate.now().until(m.getWithdrawalEndDate()).getDays();
                
                DashboardResponse.Alert alert = new DashboardResponse.Alert();
                
                // 获取池塘名称
                String pondName = "";
                if (m.getPondId() != null) {
                    Pond pond = pondMapper.findById(m.getPondId());
                    if (pond != null) {
                        pondName = pond.getPondName();
                    }
                }
                
                if (daysLeft <= 0) {
                    // 休药期已结束
                    alert.setType("success");
                    alert.setMessage("【" + pondName + "】" + m.getDrugName() + " 休药期已结束，可进行捕捞");
                } else if (daysLeft <= 3) {
                    // 即将结束（3天内）
                    alert.setType("danger");
                    alert.setMessage("【" + pondName + "】" + m.getDrugName() + " 距离休药期结束还有 " + daysLeft + " 天");
                } else {
                    // 正常显示
                    alert.setType("warning");
                    alert.setMessage("【" + pondName + "】" + m.getDrugName() + " 距离休药期结束还有 " + daysLeft + " 天");
                }
                
                alert.setPondId(m.getPondId());
                alerts.add(alert);
            }
        }
        response.setAlerts(alerts);

        // 最近水质
        WaterQuality latest = waterQualityMapper.findLatestByUserId(userId);
        if (latest != null) {
            DashboardResponse.LatestWaterQuality lwq = new DashboardResponse.LatestWaterQuality();
            lwq.setPondId(latest.getPondId());
            Pond pond = pondMapper.findById(latest.getPondId());
            if (pond != null) {
                lwq.setPondName(pond.getPondName());
            }
            if (latest.getTestTime() != null) {
                lwq.setTestTime(latest.getTestTime().toString());
            }
            if (latest.getWaterTemp() != null) {
                lwq.setWaterTemp(latest.getWaterTemp().toString());
            }
            if (latest.getPhValue() != null) {
                lwq.setPhValue(latest.getPhValue().toString());
            }
            if (latest.getDissolvedOxygen() != null) {
                lwq.setDissolvedOxygen(latest.getDissolvedOxygen().toString());
            }
            response.setLatestWaterQuality(lwq);
        }

        return response;
    }
}
