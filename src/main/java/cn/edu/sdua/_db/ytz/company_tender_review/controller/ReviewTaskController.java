package cn.edu.sdua._db.ytz.company_tender_review.controller;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ReviewTaskCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ReviewTaskQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ReviewTaskDetailResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ReviewFullResultResponse;
import cn.edu.sdua._db.ytz.company_tender_review.repository.ReviewTaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@Tag(name = "ReviewTask")
@RestController
@RequestMapping("/api/review-tasks")
public class ReviewTaskController {

    private final ReviewTaskRepository repository;
    private static final String PYTHON_AI_URL = "http://localhost:8000/api/ai/review";
    private final RestTemplate restTemplate = new RestTemplate();

    public ReviewTaskController(ReviewTaskRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "创建并排队审查任务")
    @PostMapping
    public R<ReviewTaskDetailResponse> create(@Valid @RequestBody ReviewTaskCreateRequest request) {
        long id = repository.create(request);
        ReviewTaskDetailResponse taskDetail = repository.findTaskDetail(id);

        final long taskId = id;
        final List<Long> docIds = request.getDocIds();
        final int taskType = request.getTaskType();

        new Thread(() -> {
            try {
                Map<String, Object> pyReq = new HashMap<>();
                pyReq.put("taskId", taskId);
                pyReq.put("docIds", docIds);
                pyReq.put("taskType", taskType);

                @SuppressWarnings("unchecked")
                Map<String, Object> pyResp = restTemplate.postForObject(
                        PYTHON_AI_URL, pyReq, Map.class);

                if (pyResp == null || !Integer.valueOf(0).equals(pyResp.get("code"))) {
                    // Python 端直接拒绝了（比如参数校验失败），标记任务失败
                    repository.updateStatusAndTime(taskId, 4);
                }
                // Python 返回 code=0 表示已接受，后台执行中
                // 状态 2 已经在 Python 端的 run_review 开头设置

            } catch (Exception e) {
                // Python 服务挂了、网络不通、超时等
                e.printStackTrace();
                repository.updateStatusAndTime(taskId, 4);
            }
        }, "review-worker-" + id).start();

        return R.ok(taskDetail);
    }

    @Operation(summary = "更新任务执行状态")
    @PatchMapping("/{id}/status")
    public R<ReviewTaskDetailResponse> updateInternalStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        if (!List.of(1, 2, 3, 4, 5).contains(status)) {
            throw new IllegalArgumentException("invalid internal status");
        }
        repository.updateStatusAndTime(id, status);
        return R.ok(repository.findTaskDetail(id));
    }

    @Operation(summary = "查询审查任务列表")
    @GetMapping
    public R<List<ReviewTaskDetailResponse>> list(@Valid ReviewTaskQueryRequest request) {
        long total = repository.count(request);
        List<ReviewTaskDetailResponse> data = repository.list(request);
        return R.okPage(data, total, request.getPage(), request.getSize());
    }

    @Operation(summary = "获取任务的完整审查结果")
    @GetMapping("/{id}/result")
    public R<ReviewFullResultResponse> result(@PathVariable("id") Long id) {
        return R.ok(repository.findFullResult(id));
    }
}
