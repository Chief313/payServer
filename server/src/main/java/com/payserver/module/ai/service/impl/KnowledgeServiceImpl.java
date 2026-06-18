package com.payserver.module.ai.service.impl;

import org.springframework.ai.vectorstore.VectorStore;
import com.payserver.common.exception.BizException;
import com.payserver.module.ai.mapper.KnowledgeDocumentMapper;
import com.payserver.module.ai.model.entity.KnowledgeDocument;
import com.payserver.module.ai.service.KnowledgeService;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * AI 知识库服务实现
 * 支持 txt/doc/docx/pdf/md/markdown 解析，分块后写入向量库
 */
@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    private static final Set<String> SUPPORTED_TYPES = Set.of("txt", "doc", "docx", "pdf", "md", "markdown");

    private final KnowledgeDocumentMapper knowledgeDocumentMapper;
    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;
    private final Path uploadDir;

    public KnowledgeServiceImpl(KnowledgeDocumentMapper knowledgeDocumentMapper,
                                VectorStore vectorStore,
                                EmbeddingModel embeddingModel,
                                @Value("${knowledge.upload-dir:./data/knowledge}") String uploadDir) {
        this.knowledgeDocumentMapper = knowledgeDocumentMapper;
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel;
        this.uploadDir = Paths.get(uploadDir);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeDocument upload(MultipartFile file, String title) {
        if (file == null || file.isEmpty()) {
            throw new BizException("上传文件不能为空");
        }
        if (title == null || title.isBlank()) {
            throw new BizException("文档标题不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BizException("无法识别文件类型");
        }

        String fileType = extractFileType(originalFilename);
        if (!SUPPORTED_TYPES.contains(fileType)) {
            throw new BizException("不支持的文件类型：" + fileType);
        }

        KnowledgeDocument document = new KnowledgeDocument();
        document.setTitle(title.trim());
        document.setFileName(originalFilename);
        document.setFileType(fileType);
        document.setFilePath("");
        document.setFileSize(file.getSize());
        document.setChunkCount(0);
        document.setVectorStatus("PENDING");
        knowledgeDocumentMapper.insert(document);

        Path savedPath = saveFile(file, document.getId(), originalFilename);
        document.setFilePath(savedPath.toString());
        knowledgeDocumentMapper.updateFilePath(document);

        try {
            List<Document> documents = parseDocuments(savedPath, fileType, document);
            List<Document> chunks = splitDocuments(documents);
            attachDocMetadata(chunks, document);
            vectorStore.add(chunks);

            document.setChunkCount(chunks.size());
            document.setVectorStatus("DONE");
            knowledgeDocumentMapper.updateVectorStatus(document);
            return knowledgeDocumentMapper.findById(document.getId());
        } catch (Exception e) {
            document.setVectorStatus("FAILED");
            knowledgeDocumentMapper.updateVectorStatus(document);
            throw new BizException("文档解析或向量化失败：" + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<KnowledgeDocument> list() {
        return knowledgeDocumentMapper.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        KnowledgeDocument document = knowledgeDocumentMapper.findById(id);
        if (document == null) {
            throw new BizException("文档不存在");
        }

        vectorStore.delete(new FilterExpressionBuilder().eq("docId", id.toString()).build());

        try {
            Files.deleteIfExists(Paths.get(document.getFilePath()));
        } catch (IOException ignored) {
            // 忽略本地文件删除失败
        }

        knowledgeDocumentMapper.deleteById(id);
    }

    /**
     * 保存上传文件到本地目录
     */
    private Path saveFile(MultipartFile file, Long docId, String originalFilename) {
        try {
            Files.createDirectories(uploadDir);
            String storedName = docId + "_" + UUID.randomUUID().toString().substring(0, 8) + "_" + originalFilename;
            Path target = uploadDir.resolve(storedName);
            file.transferTo(target);
            return target;
        } catch (IOException e) {
            throw new BizException("文件保存失败：" + e.getMessage());
        }
    }

    /**
     * 根据文件类型解析文档内容
     */
    private List<Document> parseDocuments(Path savedPath, String fileType, KnowledgeDocument document) throws IOException {
        FileSystemResource resource = new FileSystemResource(savedPath.toFile());
        Map<String, Object> baseMetadata = baseMetadata(document);

        if ("txt".equals(fileType)) {
            String content = Files.readString(savedPath, StandardCharsets.UTF_8);
            return List.of(new Document(content, baseMetadata));
        }

        if ("md".equals(fileType) || "markdown".equals(fileType)) {
            MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                    .withHorizontalRuleCreateDocument(true)
                    .withIncludeCodeBlock(true)
                    .withIncludeBlockquote(true)
                    .withAdditionalMetadata("title", document.getTitle())
                    .build();
            MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
            List<Document> docs = reader.read();
            docs.forEach(doc -> doc.getMetadata().putAll(baseMetadata));
            return docs;
        }

        TikaDocumentReader reader = new TikaDocumentReader(resource);
        List<Document> docs = reader.read();
        docs.forEach(doc -> doc.getMetadata().putAll(baseMetadata));
        return docs;
    }

    /**
     * 使用 TokenTextSplitter 对文档进行分块
     */
    private List<Document> splitDocuments(List<Document> documents) {
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(800)
                .withMinChunkSizeChars(200)
                .withMinChunkLengthToEmbed(10)
                .withMaxNumChunks(5000)
                .withKeepSeparator(true)
                .build();
        List<Document> chunks = splitter.apply(documents);
        if (chunks.isEmpty()) {
            throw new BizException("文档内容为空，无法分块");
        }
        return chunks;
    }

    /**
     * 为每个分块附加 docId 等元数据
     */
    private void attachDocMetadata(List<Document> chunks, KnowledgeDocument document) {
        for (Document chunk : chunks) {
            chunk.getMetadata().put("docId", document.getId().toString());
            chunk.getMetadata().put("title", document.getTitle());
            chunk.getMetadata().put("fileName", document.getFileName());
            chunk.getMetadata().put("fileType", document.getFileType());
        }
        // 预热 embedding 模型，确保向量维度与配置一致
        embeddingModel.embed("warmup");
    }

    /**
     * 构建文档基础元数据
     */
    private Map<String, Object> baseMetadata(KnowledgeDocument document) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("docId", document.getId().toString());
        metadata.put("title", document.getTitle());
        metadata.put("fileName", document.getFileName());
        metadata.put("fileType", document.getFileType());
        return metadata;
    }

    /**
     * 从文件名提取扩展名
     */
    private String extractFileType(String filename) {
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
