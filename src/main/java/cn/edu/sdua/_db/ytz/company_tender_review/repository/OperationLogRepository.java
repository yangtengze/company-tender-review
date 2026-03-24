package cn.edu.sdua._db.ytz.company_tender_review.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OperationLogRepository {
    private final JdbcTemplate jdbcTemplate;

    public OperationLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertLogoutLog(Long userId) {
        jdbcTemplate.update("""
                insert into sys_operation_log (user_id, module, action, object_type, object_id, detail, ip)
                values (?, ?, ?, ?, ?, ?, ?)
                """, userId, "Auth", "LOGOUT", "sys_user", userId, "user logout", null);
    }
}
