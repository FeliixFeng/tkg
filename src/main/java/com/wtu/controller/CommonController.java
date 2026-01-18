package com.wtu.controller;

import com.wtu.constant.MessageConstant;
import com.wtu.exception.BusinessException;
import com.wtu.result.Result;
import com.wtu.utils.AliOssUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * <p>
 *  通用接口
 * </p>
 *
 * @since 2024-11-21
 */
@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "通用接口")
@RequiredArgsConstructor
public class CommonController {

    private final AliOssUtil aliOssUtil;
    
    // 允许上传的文件类型
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp",  // 图片
            ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".txt"   // 文档
    );
    
    // 最大文件大小 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    // 文件上传
    @PostMapping("/upload")
    @Operation(description = "文件上传")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传: {}", file != null ? file.getOriginalFilename() : "null");
        
        // 校验文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException(MessageConstant.FILE_EMPTY);
        }
        
        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new BusinessException(MessageConstant.FILE_EMPTY);
        }
        
        // 校验文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过10MB");
        }
        
        // 获取文件扩展名
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new BusinessException(MessageConstant.FILE_TYPE_ERROR);
        }
        
        String extension = originalFilename.substring(lastDotIndex).toLowerCase();
        
        // 校验文件类型
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(MessageConstant.FILE_TYPE_ERROR);
        }
        
        try {
            // 构建新的文件名称
            String objectName = UUID.randomUUID().toString() + extension;
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException(MessageConstant.UPLOAD_FAILED + ": " + e.getMessage());
        }
    }
}
