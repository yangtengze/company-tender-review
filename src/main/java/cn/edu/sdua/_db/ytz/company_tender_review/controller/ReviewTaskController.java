package cn.edu.sdua._db.ytz.company_tender_review.controller;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ReviewTaskCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ReviewTaskQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ReviewFullResultResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ReviewTaskDetailResponse;
import cn.edu.sdua._db.ytz.company_tender_review.repository.ReviewTaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@Tag(name = "ReviewTask")
@RestController
@RequestMapping("/api/review-tasks")
public class ReviewTaskController {
    private final ReviewTaskRepository repository;

    public ReviewTaskController(ReviewTaskRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "创建并排队审查任务")
    @PostMapping
    public R<ReviewTaskDetailResponse> create(@Valid @RequestBody ReviewTaskCreateRequest request) {
        long id = repository.create(request);
        return R.ok(repository.findTaskDetail(id));
    }

    @Operation(summary = "查询审查任务列表")
    @GetMapping
    public R<List<ReviewTaskDetailResponse>> list(@Valid ReviewTaskQueryRequest request) {
        long total = repository.count(request);
        List<ReviewTaskDetailResponse> data = repository.list(request);
        return R.okPage(data, total, request.getPage(), request.getSize());
    }

    @Operation(summary = "获取任务的完整审查结果（总结论 + 8 个子项 + 问题清单）")
    @GetMapping("/{id}/result")
    public R<ReviewFullResultResponse> result(@PathVariable("id") Long id) {
        return R.ok(repository.findFullResult(id));
    }
}

