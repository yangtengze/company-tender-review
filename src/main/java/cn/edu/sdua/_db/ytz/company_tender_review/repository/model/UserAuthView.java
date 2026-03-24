package cn.edu.sdua._db.ytz.company_tender_review.repository.model;

import java.time.LocalDateTime;

public record UserAuthView(
        Long id,
        String username,
        String passwordHash,
        String realName,
        String phone,
        String email,
        Integer role,
        Integer status,
        String avatarUrl,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        Long orgId,
        String orgName,
        Integer orgType
) {
}
