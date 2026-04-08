package cn.edu.sdua._db.ytz.company_tender_review.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.LLMLogQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.LLMLogSummaryQuery;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.LLMLogItem;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.LLMLogSummary;
import cn.edu.sdua._db.ytz.company_tender_review.repository.LLMLogsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Validated
@Tag(name = "LLMLog")
@RestController
@RequestMapping("/api/llm-logs")
public class LLMLogsController {
    private final LLMLogsRepository llmLogsRepository;

    public LLMLogsController(LLMLogsRepository llmLogsRepository) {
        this.llmLogsRepository = llmLogsRepository;
    }

    @Operation(summary = "查询大模型调用日志")
    @GetMapping
    public R<List<LLMLogItem>> list(@Valid LLMLogQueryRequest request) {
        Long total = llmLogsRepository.count(request);
        List<LLMLogItem> data = llmLogsRepository.list(request);
        return R.okPage(data, total, request.getPage(), request.getSize());
    }
    @Operation(summary = "按模型统计 Token 消耗/延迟/失败率")
    @GetMapping("/summary")
    public R<List<LLMLogSummary>> summary(@Valid LLMLogSummaryQuery request) {
        return R.ok(llmLogsRepository.summary(request));
    }
}
