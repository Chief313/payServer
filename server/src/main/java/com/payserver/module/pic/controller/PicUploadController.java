package com.payserver.module.pic.controller;

import com.payserver.common.result.Result;
import com.payserver.module.pic.service.PicStorageService;
import com.payserver.security.RequireRole;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片上传接口
 * 图片存储在 picserver 目录，由 server 统一提供上传与访问
 */
@RestController
@RequestMapping("/api/pic")
public class PicUploadController {

    private final PicStorageService picStorageService;

    public PicUploadController(PicStorageService picStorageService) {
        this.picStorageService = picStorageService;
    }

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @return 图片访问 URL
     */
    @PostMapping("/upload")
    @RequireRole({"ADMIN", "USER"})
    public Result<UploadResult> upload(@RequestParam("file") MultipartFile file) {
        String fileUrl = picStorageService.save(file);
        UploadResult result = new UploadResult();
        result.setFileUrl(fileUrl);
        return Result.ok(result);
    }

    /**
     * 上传响应数据
     */
    @Data
    public static class UploadResult {
        /** 图片访问 URL */
        private String fileUrl;
    }
}
