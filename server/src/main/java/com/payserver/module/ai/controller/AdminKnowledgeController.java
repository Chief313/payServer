package com.payserver.module.ai.controller;

import com.payserver.common.result.Result;
import com.payserver.module.ai.model.entity.KnowledgeDocument;
import com.payserver.module.ai.service.KnowledgeService;
import com.payserver.security.RequireRole;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 管理端 AI 知识库接口
 */
@RestController
@RequestMapping("/api/v1/admin/ai/knowledge")
public class AdminKnowledgeController {

    private final KnowledgeService knowledgeService;

    public AdminKnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    /**
     * 上传知识库文档
     *
     * @param file  文档文件
     * @param title 文档标题
     * @return 文档信息
     */
    @PostMapping("/upload")
    @RequireRole("ADMIN")
    public Result<KnowledgeDocument> upload(@RequestParam("file") MultipartFile file,
                                            @RequestParam("title") String title) {
        KnowledgeDocument document = knowledgeService.upload(file, title);
        return Result.ok(document);
    }

    /**
     * 查询知识库文档列表
     *
     * @return 文档列表
     */
    @GetMapping("/list")
    @RequireRole("ADMIN")
    public Result<List<KnowledgeDocument>> list() {
        return Result.ok(knowledgeService.list());
    }

    /**
     * 删除知识库文档
     *
     * @param id 文档ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @RequireRole("ADMIN")
    public Result<Void> delete(@PathVariable Long id) {
        knowledgeService.delete(id);
        return Result.ok();
    }
}
