package cn.edu.sdua._db.ytz.company_tender_review.repository;

import cn.edu.sdua._db.ytz.company_tender_review.repository.model.UserAuthView;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private static final String BASE_COLUMNS = """
            select u.id, u.username, u.password_hash, u.real_name, u.phone, u.email,
                   u.role, u.status, u.avatar_url, u.last_login_at, u.created_at,
                   o.id as org_id, o.name as org_name, o.type as org_type
              from sys_user u
              left join sys_org o on o.id = u.org_id
            """;

    private static final RowMapper<UserAuthView> MAPPER = (rs, rowNum) -> new UserAuthView(
            rs.getLong("id"),
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("real_name"),
            rs.getString("phone"),
            rs.getString("email"),
            rs.getInt("role"),
            rs.getInt("status"),
            rs.getString("avatar_url"),
            toLocalDateTime(rs.getTimestamp("last_login_at")),
            toLocalDateTime(rs.getTimestamp("created_at")),
            rs.getObject("org_id", Long.class),
            rs.getString("org_name"),
            rs.getObject("org_type", Integer.class)
    );

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<UserAuthView> findByUsername(String username) {
        List<UserAuthView> rows = jdbcTemplate.query(BASE_COLUMNS + " where u.username = ?", MAPPER, username);
        return rows.stream().findFirst();
    }

    public Optional<UserAuthView> findById(Long userId) {
        List<UserAuthView> rows = jdbcTemplate.query(BASE_COLUMNS + " where u.id = ?", MAPPER, userId);
        return rows.stream().findFirst();
    }

    public void updateLastLoginAt(Long userId, LocalDateTime now) {
        jdbcTemplate.update("update sys_user set last_login_at = ? where id = ?", Timestamp.valueOf(now), userId);
    }

    private static LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }
}
