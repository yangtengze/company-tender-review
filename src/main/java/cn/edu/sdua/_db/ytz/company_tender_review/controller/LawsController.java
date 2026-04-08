package cn.edu.sdua._db.ytz.company_tender_review.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ClauseQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.LawCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.LawQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.LawClause;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.LawDetailResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.LawListItem;
import cn.edu.sdua._db.ytz.company_tender_review.repository.LawsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Validated
@Tag(name = "Law")
@RestController
@RequestMapping("/api/laws")
public class LawsController {
    private final LawsRepository lawsRepository;

    public LawsController(LawsRepository lawsRepository) {
        this.lawsRepository = lawsRepository;
    }
    @Operation(summary = "全文检索法规库")
    @GetMapping
    public R<List<LawListItem>> list(@Valid LawQueryRequest request) {
        Long total = lawsRepository.count(request);
        List<LawListItem> data = lawsRepository.list(request);
        return R.okPage(data, total, request.getPage(), request.getSize());
    }
    @Operation(summary = "录入新法规全文")
    @PostMapping
    public R<LawDetailResponse> insert(@Valid @RequestBody LawCreateRequest request) {
        Long id = lawsRepository.insert(request);
        return R.ok(lawsRepository.findDetail(id));
    }
    @Operation(summary = "获取法规细粒度条款")
    @GetMapping("/{lawId}/clauses")
    public R<List<LawClause>> query(@PathVariable("lawId") Long lawId,
                                    @Valid ClauseQueryRequest request) {
        List<LawClause> data = lawsRepository.query(lawId, request);
        return R.ok(data);
    }
}
