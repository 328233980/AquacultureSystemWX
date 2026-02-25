package com.aquaculture.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Reminder {
    private Long id;
    private Long userId;
    private Long pondId;
    private String reminderType;
    private String title;
    private String content;
    private LocalDate remindDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
