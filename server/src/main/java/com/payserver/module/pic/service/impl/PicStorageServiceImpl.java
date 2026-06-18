package com.payserver.module.pic.service.impl;

import com.payserver.common.exception.BizException;
import com.payserver.module.pic.service.PicStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 本地图片存储服务实现
 * 文件保存至 picserver 目录，返回 /pic/files/{filename} 访问路径
 */
@Service
public class PicStorageServiceImpl implements PicStorageService {

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp");

    private final Path uploadDir;

    /**
     * @param uploadDir 图片存储目录（picserver 文件夹路径）
     */
    public PicStorageServiceImpl(@Value("${pic.upload-dir:../picserver}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        }
        if (extension.isEmpty() || !ALLOWED_EXT.contains(extension)) {
            throw new BizException("仅支持 jpg/jpeg/png/gif/webp/bmp 图片格式");
        }

        String filename = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        try {
            Files.createDirectories(uploadDir);
            Path target = uploadDir.resolve(filename);
            file.transferTo(target);
            return "/pic/files/" + filename;
        } catch (IOException e) {
            throw new BizException("图片保存失败：" + e.getMessage());
        }
    }
}
