package cn.edu.sdua._db.ytz.company_tender_review.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.IssueHandleRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.IssueQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.IssueStatsQuery;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.IssueDetailResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.IssueStatsResponse;
import cn.edu.sdua._db.ytz.company_tender_review.repository.ReviewIssuesRepository;
import cn.edu.sdua._db.ytz.company_tender_review.service.security.JwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Validated
@Tag(name = "ReviewIssue")
@RestController
@RequestMapping("/api/review-issues")
public class ReviewIssuesController {
    private final ReviewIssuesRepository reviewIssuesRepository;
    private final JwtTokenService jwtTokenService;
    public ReviewIssuesController(ReviewIssuesRepository reviewIssuesRepository, JwtTokenService jwtTokenService) {
        this.reviewIssuesRepository = reviewIssuesRepository;
        this.jwtTokenService = jwtTokenService;
    }
    @Operation(summary = "查询问题清单")
    @GetMapping
    public R<List<IssueDetailResponse>> list(@Valid IssueQueryRequest request) {
        Long total = reviewIssuesRepository.count(request);
        List<IssueDetailResponse> data = reviewIssuesRepository.list(request);
        return R.okPage(data, total, request.getPage(), request.getSize());
    }
    @Operation(summary = "更新问题整改状态")
    @PatchMapping("/{id}/handle")
    public R<IssueDetailResponse> updateStatus(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                @PathVariable("id") Long id,
                                                @RequestBody @Valid IssueHandleRequest request) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return R.fail(401, "Invalid authorization header(Bearer )");
        }
        Long authId = jwtTokenService.parseAccessUserId(authorization);
        if (request.getStatus() == null) {
            throw new IllegalArgumentException("status required");
        }
        if (request.getStatus() == 3 
            && (request.getHandleNote() == null || request.getHandleNote().isBlank())) {
             throw new IllegalArgumentException("handleNote required for status 3");
        }
        IssueDetailResponse updated = reviewIssuesRepository.update(id, request, authId);
        return R.ok(updated);
    }
    @Operation(summary = "按项目统计各级别问题数量")
    @GetMapping("/statistic")
    public R<IssueStatsResponse> calculate(@Valid IssueStatsQuery request) {
        return R.ok(reviewIssuesRepository.calculate(request));
    }
}
