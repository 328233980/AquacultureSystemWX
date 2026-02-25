package com.aquaculture.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FileUtil {
    
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    /**
     * 验证文件类型
     */
    public static boolean isValidImageType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase());
    }

    /**
     * 获取文件扩展名
     */
    public static String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }

    /**
     * 验证文件扩展名
     */
    public static boolean isValidExtension(String filename) {
        String ext = getExtension(filename);
        return ALLOWED_EXTENSIONS.contains(ext);
    }

    /**
     * 生成唯一文件名
     */
    public static String generateUniqueFileName(String originalFilename) {
        String ext = getExtension(originalFilename);
        return UUID.randomUUID().toString().replace("-", "") + "_" + System.currentTimeMillis() + ext;
    }

    /**
     * 保存文件
     */
    public static String saveFile(MultipartFile file, String uploadPath, String filename) throws IOException {
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        File destFile = new File(dir, filename);
        file.transferTo(destFile);
        
        return filename;
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}
