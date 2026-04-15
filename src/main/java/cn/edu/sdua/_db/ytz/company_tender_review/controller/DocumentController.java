package cn.edu.sdua._db.ytz.company_tender_review.controller;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ChunkQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.DocumentQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.DocumentUploadRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.DocumentChunkNode;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.DocumentDetailResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ExtractResultResponse;
import cn.edu.sdua._db.ytz.company_tender_review.repository.DocumentRepository;
import cn.edu.sdua._db.ytz.company_tender_review.service.DocumentExtractService;
import cn.edu.sdua._db.ytz.company_tender_review.service.security.JwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@Tag(name = "Document")
@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentRepository documentRepository;
    private final DocumentExtractService documentExtractService;
    private final JwtTokenService jwtTokenService;

    public DocumentController(DocumentRepository documentRepository,
                               DocumentExtractService documentExtractService,
                               JwtTokenService jwtTokenService) {
        this.documentRepository = documentRepository;
        this.documentExtractService = documentExtractService;
        this.jwtTokenService = jwtTokenService;
    }

    @Operation(summary = "上传工程文件，解析异步进行")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<DocumentDetailResponse> upload(
            @RequestHeader("Authorization") String authorization,
            @RequestPart("file") MultipartFile file,
            @Valid @ModelAttribute DocumentUploadRequest request
    ) throws Exception {
        Long uploaderId = jwtTokenService.parseAccessUserId(authorization);
        long docId = documentRepository.insertUpload(uploaderId, request, file);
        return R.ok(documentRepository.findById(docId));
    }

    @Operation(summary = "查询项目文件列表")
    @GetMapping
    public R<java.util.List<DocumentDetailResponse>> list(@Valid @ModelAttribute DocumentQueryRequest request) {
        long total = documentRepository.count(request);
        java.util.List<DocumentDetailResponse> data = documentRepository.list(request);
        return R.okPage(data, total, request.getPage(), request.getSize());
    }

    @Operation(summary = "获取文档 AI 提取结果，命中缓存直接返回，否则异步触发")
    @GetMapping("/{id}/extract")
    public R<ExtractResultResponse> extract(@PathVariable("id") Long docId) {
        return R.ok(documentExtractService.extract(docId));
    }

    @Operation(summary = "获取文档的层次化切分结果")
    @GetMapping("/{id}/chunks")
    public R<List<DocumentChunkNode>> chunks(@PathVariable("id") Long docId,
                                            @Valid ChunkQueryRequest request) {
        return R.ok(documentRepository.chunks(docId, request));
    }
}

