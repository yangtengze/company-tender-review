package cn.edu.sdua._db.ytz.company_tender_review.controller;

import cn.edu.sdua._db.ytz.company_tender_review.common.R;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.BidAnnouncementCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.BidAnnouncementResponse;
import cn.edu.sdua._db.ytz.company_tender_review.repository.BidAnnouncementRepository;
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
@Tag(name = "BidAnnouncement")
@RestController
@RequestMapping("/api/bid-announcements")
public class BidAnnouncementController {
    private final BidAnnouncementRepository bidAnnouncementRepository;
    private final DocumentRepository documentRepository;

    public BidAnnouncementController(BidAnnouncementRepository bidAnnouncementRepository, DocumentRepository documentRepository) {
        this.bidAnnouncementRepository = bidAnnouncementRepository;
        this.documentRepository = documentRepository;
    }

    @Operation(summary = "录入招标公告关键字段（AI 提取后自动调用或人工补录）")
    @PostMapping
    public R<BidAnnouncementResponse> create(@Valid @RequestBody BidAnnouncementCreateRequest request) {
        DocumentRepository.DocumentMeta meta = documentRepository.findDocMetaById(request.getDocId());
        if (meta == null) {
            throw new IllegalArgumentException("document not found");
        }
        if (meta.docType() == null || meta.docType() != 1) {
            throw new IllegalArgumentException("bid doc type invalid");
        }
        if (!meta.projectId().equals(request.getProjectId())) {
            throw new IllegalArgumentException("document projectId mismatch");
        }

        long id = bidAnnouncementRepository.insert(request);
        return R.ok(bidAnnouncementRepository.findResponseById(id));
    }

    @Operation(summary = "根据docId获取招标公告的详细信息")
    @GetMapping("/{docId}/detail")
    public R<BidAnnouncementResponse> findResponseByDocId(@PathVariable Long docId) {
        return R.ok(bidAnnouncementRepository.findResponseByDocId(docId));
    }
}

