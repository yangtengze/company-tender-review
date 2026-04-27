package cn.edu.sdua._db.ytz.company_tender_review.controller;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ContractCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ContractResponse;
import cn.edu.sdua._db.ytz.company_tender_review.repository.ContractRepository;
import cn.edu.sdua._db.ytz.company_tender_review.repository.DocumentRepository;
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

@Validated
@Tag(name = "Contract")
@RestController
@RequestMapping("/api/contracts")
public class ContractController {
    private final ContractRepository repository;
    private final DocumentRepository documentRepository;

    public ContractController(ContractRepository repository, DocumentRepository documentRepository) {
        this.repository = repository;
        this.documentRepository = documentRepository;
    }

    @Operation(summary = "录入施工合同关键字段")
    @PostMapping
    public R<ContractResponse> create(@Valid @RequestBody ContractCreateRequest request) {
        DocumentRepository.DocumentMeta meta = documentRepository.findDocMetaById(request.getDocId());
        if (meta == null) {
            throw new IllegalArgumentException("document not found");
        }
        if (meta.docType() == null || meta.docType() != 5) {
            throw new IllegalArgumentException("contract doc type invalid");
        }
        if (!meta.projectId().equals(request.getProjectId())) {
            throw new IllegalArgumentException("document projectId mismatch");
        }

        long id = repository.insert(request);
        return R.ok(repository.findResponseById(id));
    }

    @Operation(summary = "根据docId找合同信息")
    @GetMapping("/{docId}/detail")
    public R<ContractResponse> findResponseByDocId(@PathVariable Long docId) {
        return R.ok(repository.findResponseByDocId(docId));
    }
}

