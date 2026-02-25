package com.aquaculture.controller;

import com.aquaculture.dto.response.ApiResponse;
import com.aquaculture.entity.Attachment;
import com.aquaculture.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ApiResponse<Attachment> uploadFile(HttpServletRequest request,
                                               @RequestParam("file") MultipartFile file,
                                               @RequestParam("relatedType") String relatedType,
                                               @RequestParam("relatedId") Long relatedId) {
        Long userId = (Long) request.getAttribute("userId");
        Attachment attachment = fileService.uploadFile(file, relatedType, relatedId, userId);
        return ApiResponse.success("文件上传成功", attachment);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFile(@PathVariable Long id) {
        fileService.deleteFile(id);
        return ApiResponse.success("文件删除成功", null);
    }
}
