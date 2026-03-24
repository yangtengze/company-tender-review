package cn.edu.sdua._db.ytz.company_tender_review.repository.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public record UserListRow(
        Long id,
        String username,
        String realName,
        String phone,
        String email,
        Integer role,
        Integer status,
        Long orgId,
        String orgName,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt
) {
    public static final RowMapper<UserListRow> MAPPER = (rs, rowNum) -> new UserListRow(
            rs.getLong("id"),
            rs.getString("username"),
            rs.getString("real_name"),
            rs.getString("phone"),
            rs.getString("email"),
            rs.getObject("role", Integer.class),
            rs.getObject("status", Integer.class),
            rs.getObject("org_id", Long.class),
            rs.getString("org_name"),
            toLocalDateTime(rs.getTimestamp("last_login_at")),
            toLocalDateTime(rs.getTimestamp("created_at"))
    );

    private static LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }
}

