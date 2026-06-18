package com.payserver.module.pic.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 图片存储服务接口
 */
public interface PicStorageService {

    /**
     * 保存上传的图片文件
     *
     * @param file 上传文件
     * @return 图片访问 URL，如 /pic/files/xxx.jpg
     */
    String save(MultipartFile file);
}
