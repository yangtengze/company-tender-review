package cn.edu.sdua._db.ytz.company_tender_review.controller;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ReviewConfirmRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ReviewResultQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ReviewResultDetailResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ReviewResultListItem;
import cn.edu.sdua._db.ytz.company_tender_review.repository.ReviewResultRepository;
import cn.edu.sdua._db.ytz.company_tender_review.service.security.JwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Validated
@Tag(name = "ReviewResult")
@RestController
@RequestMapping("/api/review-results")
public class ReviewResultController {
    private final ReviewResultRepository reviewResultRepository;
    private final JwtTokenService jwtTokenService;

    public ReviewResultController(ReviewResultRepository reviewResultRepository, JwtTokenService jwtTokenService) {
        this.reviewResultRepository = reviewResultRepository;
        this.jwtTokenService = jwtTokenService;
    }

    @Operation(summary = "查询审查结果分页列表")
    @GetMapping
    public R<List<ReviewResultListItem>> list(@Valid ReviewResultQueryRequest request) {
        long total = reviewResultRepository.count(request);
        List<ReviewResultListItem> data = reviewResultRepository.list(request);
        return R.okPage(data, total, request.getPage(), request.getSize());
    }

    @Operation(summary = "审查员对 AI 结果进行确认或驳回")
    @PatchMapping("/{id}/review")
    public R<ReviewResultDetailResponse> review(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                   @PathVariable("id") Long id,
                                                   @Valid @RequestBody ReviewConfirmRequest request) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return R.fail(401, "Invalid authorization header(Bearer )");
        }
        Long reviewerId = jwtTokenService.parseAccessUserId(authorization);
        if (request.getReviewStatus() == null) {
            throw new IllegalArgumentException("reviewStatus required");
        }
        if (request.getReviewStatus() == 3
                && (request.getReviewerNote() == null || request.getReviewerNote().isBlank())) {
            throw new IllegalArgumentException("reviewerNote required");
        }
        ReviewResultDetailResponse updated = reviewResultRepository.review(id, request, reviewerId);
        return R.ok(updated);
    }

    @Operation(summary = "导出审查报告（PDF 或 Word）")
    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> export(@PathVariable("id") Long id,
                                            @RequestParam(required = false) @Pattern(regexp = "pdf|word")
                                            String format) {
        String f = format == null ? "pdf" : format.trim().toLowerCase();
        byte[] bytes = reviewResultRepository.export(id, f);

        MediaType mediaType = "word".equals(f)
                ? MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                : MediaType.APPLICATION_PDF;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentLength(bytes.length);
        return ResponseEntity.ok().headers(headers).body(bytes);
    }
}

