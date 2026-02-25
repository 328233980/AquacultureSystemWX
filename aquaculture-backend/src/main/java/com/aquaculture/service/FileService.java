package com.aquaculture.service;

import com.aquaculture.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    Attachment uploadFile(MultipartFile file, String relatedType, Long relatedId, Long userId);
    List<Attachment> getAttachments(String relatedType, Long relatedId);
    void deleteFile(Long id);
}
