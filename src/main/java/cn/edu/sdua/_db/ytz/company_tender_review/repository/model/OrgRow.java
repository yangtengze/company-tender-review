package cn.edu.sdua._db.ytz.company_tender_review.repository.model;

import java.time.LocalDateTime;

public record OrgRow(
        Long id,
        String name,
        String code,
        Integer type,
        Long parentId,
        String address,
        Integer status,
        LocalDateTime createdAt
) {
}
