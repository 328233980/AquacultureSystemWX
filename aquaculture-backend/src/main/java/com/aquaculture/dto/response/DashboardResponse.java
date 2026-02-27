package com.aquaculture.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class DashboardResponse {
    private Statistics statistics;
    private List<ReminderItem> reminders;
    private List<RecentLog> recentLogs;
    private List<Alert> alerts;
    
    @Data
    public static class Statistics {
        private int totalPonds;
        private int activePonds;
        private int currentBatches;
        private int todayFeeding;
    }
    
    @Data
    public static class ReminderItem {
        private Long id;
        private String type;
        private String title;
        private String date;
        private String pondName;
    }
    
    @Data
    public static class RecentLog {
        private Long id;
        private String pondName;
        private String logDate;
        private String feedingAmount;
        private String weather;
    }
    
    @Data
    public static class Alert {
        private String type;
        private String message;
        private Long pondId;
    }

    @Data
    public static class LatestWaterQuality {
        private Long pondId;
        private String pondName;
        private String testTime;
        private String waterTemp;
        private String phValue;
        private String dissolvedOxygen;
    }

    // 最近水质，可为null
    private LatestWaterQuality latestWaterQuality;
}
