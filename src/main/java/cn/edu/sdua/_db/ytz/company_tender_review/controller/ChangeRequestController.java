package cn.edu.sdua._db.ytz.company_tender_review.controller;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ChangeDocBindRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ChangeRequestCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ChangeRequestDetailResponse;
import cn.edu.sdua._db.ytz.company_tender_review.repository.ChangeRequestRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Tag(name = "ChangeRequest")
@RestController
@RequestMapping("/api/change-requests")
public class ChangeRequestController {
    private final ChangeRequestRepository changeRequestRepository;

    public ChangeRequestController(ChangeRequestRepository changeRequestRepository) {
        this.changeRequestRepository = changeRequestRepository;
    }

    @Operation(summary = "提交施工变更申请")
    @PostMapping
    public R<ChangeRequestDetailResponse> create(@Valid @RequestBody ChangeRequestCreateRequest request) {
        long id = changeRequestRepository.insertChangeRequest(request);
        return R.ok(changeRequestRepository.findDetailById(id));
    }

    @Operation(summary = "为变更申请绑定已上传文件")
    @PostMapping("/{id}/docs")
    public R<Void> bindDoc(@PathVariable("id") Long id,
                              @Valid @RequestBody ChangeDocBindRequest request) {
        try {
            changeRequestRepository.bindDoc(id, request);
            return R.ok(null);
        } catch (IllegalArgumentException ex) {
            if ("conflict".equals(ex.getMessage())) {
                return R.fail(409, "conflict");
            }
            throw ex;
        }
    }
}

