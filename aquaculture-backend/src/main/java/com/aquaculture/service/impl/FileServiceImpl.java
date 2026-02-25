package com.aquaculture.service.impl;

import com.aquaculture.entity.Attachment;
import com.aquaculture.exception.BusinessException;
import com.aquaculture.mapper.AttachmentMapper;
import com.aquaculture.service.FileService;
import com.aquaculture.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Value("${file.upload-path}")
    private String uploadPath;

    @Value("${file.max-size}")
    private long maxFileSize;

    @Override
    public Attachment uploadFile(MultipartFile file, String relatedType, Long relatedId, Long userId) {
        // 验证文件
        if (file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }
        if (file.getSize() > maxFileSize) {
            throw new BusinessException(400, "文件大小超过限制（最大5MB）");
        }
        if (!FileUtil.isValidImageType(file)) {
            throw new BusinessException(400, "不支持的文件类型，仅支持jpg/png/gif/webp");
        }

        String originalFilename = file.getOriginalFilename();
        String newFilename = FileUtil.generateUniqueFileName(originalFilename);

        try {
            FileUtil.saveFile(file, uploadPath, newFilename);
        } catch (IOException e) {
            log.error("文件保存失败", e);
            throw new BusinessException(500, "文件保存失败");
        }

        // 保存附件记录
        Attachment attachment = new Attachment();
        attachment.setRelatedType(relatedType);
        attachment.setRelatedId(relatedId);
        attachment.setFileName(originalFilename);
        attachment.setFilePath("/uploads/" + newFilename);
        attachment.setFileType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setUploadBy(userId);

        attachmentMapper.insert(attachment);
        log.info("文件上传成功: id={}, path={}", attachment.getId(), attachment.getFilePath());

        return attachment;
    }

    @Override
    public List<Attachment> getAttachments(String relatedType, Long relatedId) {
        return attachmentMapper.findByRelated(relatedType, relatedId);
    }

    @Override
    public void deleteFile(Long id) {
        Attachment attachment = attachmentMapper.findById(id);
        if (attachment == null) {
            throw new BusinessException(404, "附件不存在");
        }

        // 删除物理文件
        String filePath = uploadPath + attachment.getFilePath().replace("/uploads/", "");
        FileUtil.deleteFile(filePath);

        // 删除数据库记录
        attachmentMapper.deleteById(id);
        log.info("文件删除成功: id={}", id);
    }
}
