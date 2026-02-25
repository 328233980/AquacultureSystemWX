package com.aquaculture.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Attachment {
    private Long id;
    private String relatedType;
    private Long relatedId;
    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private Long uploadBy;
    private LocalDateTime createdAt;
}
