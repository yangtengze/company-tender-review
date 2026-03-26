package cn.edu.sdua._db.ytz.company_tender_review.repository.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BidAnnouncementRow(
        Long id,
        Long docId,
        Long projectId,
        String bidNo,
        Integer bidType,
        LocalDateTime publishDate,
        LocalDateTime deadlineDate,
        LocalDateTime bidOpenDate,
        Integer publicNoticeDays,
        String platformName,
        String platformUrl,
        Integer isPublicPlatform,
        String qualificationReq,
        String performanceReq,
        BigDecimal estimatedPrice,
        LocalDateTime updatedAt
) {
}

