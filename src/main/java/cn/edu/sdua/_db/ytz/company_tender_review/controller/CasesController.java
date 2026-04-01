package cn.edu.sdua._db.ytz.company_tender_review.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.CaseCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.CaseQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.CaseDetailResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.CaseListItem;
import cn.edu.sdua._db.ytz.company_tender_review.repository.CasesRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Validated
@Tag(name = "Case")
@RestController
@RequestMapping("/api/cases")
public class CasesController {
    private CasesRepository casesRepository;


    public CasesController(CasesRepository casesRepository) {
        this.casesRepository = casesRepository;
    }

    @Operation(summary = "全文检索历史案例")
    @GetMapping
    public R<List<CaseListItem>> list(@Valid CaseQueryRequest request) {
        try {
            Long total = casesRepository.count(request);
            List<CaseListItem> data = casesRepository.list(request);
            return R.okPage(data, total, request.getPage(), request.getSize());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    @Operation(summary = "录入历史案例")
    @PostMapping
    public R<CaseDetailResponse> insert(@Valid @RequestBody CaseCreateRequest request) {
        try {
            Long id = casesRepository.insert(request);
            return R.ok(casesRepository.findDetail(id));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
