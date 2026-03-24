package cn.edu.sdua._db.ytz.company_tender_review.repository;

import cn.edu.sdua._db.ytz.company_tender_review.repository.model.UserAuthView;
import cn.edu.sdua._db.ytz.company_tender_review.repository.model.UserListRow;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public long countUsers(Long orgId, Integer role, Integer status, String keyword) {
        StringBuilder sql = new StringBuilder("select count(*) from sys_user u where 1=1");
        List<Object> args = new ArrayList<>();
        appendFilters(sql, args, orgId, role, status, keyword);
        return jdbcTemplate.queryForObject(sql.toString(), Long.class, args.toArray());
    }

    public List<UserListRow> listUsers(Long orgId, Integer role, Integer status, String keyword, int page, int size) {
        int offset = Math.max(0, (page - 1) * size);
        StringBuilder sql = new StringBuilder("""
                select u.id, u.username, u.real_name, u.phone, u.email, u.role, u.status,
                       u.org_id, o.name as org_name, u.last_login_at, u.created_at
                  from sys_user u
                  left join sys_org o on o.id = u.org_id
                 where 1=1
                """);
        List<Object> args = new ArrayList<>();
        appendFilters(sql, args, orgId, role, status, keyword);
        sql.append(" order by u.id desc limit ? offset ?");
        args.add(size);
        args.add(offset);
        return jdbcTemplate.query(sql.toString(), UserListRow.MAPPER, args.toArray());
    }

    public long insertUser(String username,
                           String passwordHash,
                           String realName,
                           String phone,
                           String email,
                           Long orgId,
                           Integer role) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    insert into sys_user
                    (username, password_hash, real_name, phone, email, org_id, role, status, avatar_url, last_login_at, created_at, updated_at)
                    values (?, ?, ?, ?, ?, ?, ?, 1, null, null, now(), now())
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, realName);
            ps.setString(4, phone);
            ps.setString(5, email);
            ps.setObject(6, orgId);
            ps.setObject(7, role);
            return ps;
        }, keyHolder);
        Number key = Objects.requireNonNull(keyHolder.getKey(), "generated key required");
        return key.longValue();
    }

    public void updateUser(Long userId, String realName, String phone, String email, Integer role, Integer status, String avatarUrl) {
        StringBuilder sql = new StringBuilder("update sys_user set updated_at = now()");
        List<Object> args = new ArrayList<>();
        if (realName != null) {
            sql.append(", real_name = ?");
            args.add(realName);
        }
        if (phone != null) {
            sql.append(", phone = ?");
            args.add(phone);
        }
        if (email != null) {
            sql.append(", email = ?");
            args.add(email);
        }
        if (role != null) {
            sql.append(", role = ?");
            args.add(role);
        }
        if (status != null) {
            sql.append(", status = ?");
            args.add(status);
        }
        if (avatarUrl != null) {
            sql.append(", avatar_url = ?");
            args.add(avatarUrl);
        }
        sql.append(" where id = ?");
        args.add(userId);
        int updated = jdbcTemplate.update(sql.toString(), args.toArray());
        if (updated == 0) {
            throw new IllegalArgumentException("user not found");
        }
    }

    public Optional<String> findPasswordHashById(Long userId) {
        List<String> rows = jdbcTemplate.query("select password_hash from sys_user where id = ?",
                (rs, rowNum) -> rs.getString(1), userId);
        return rows.stream().findFirst();
    }

    public void updatePasswordHash(Long userId, String newPasswordHash) {
        int updated = jdbcTemplate.update("update sys_user set password_hash = ?, updated_at = now() where id = ?",
                newPasswordHash, userId);
        if (updated == 0) {
            throw new IllegalArgumentException("user not found");
        }
    }

    private static LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }

    private static void appendFilters(StringBuilder sql, List<Object> args, Long orgId, Integer role, Integer status, String keyword) {
        if (orgId != null) {
            sql.append(" and u.org_id = ?");
            args.add(orgId);
        }
        if (role != null) {
            sql.append(" and u.role = ?");
            args.add(role);
        }
        if (status != null) {
            sql.append(" and u.status = ?");
            args.add(status);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" and (u.username like ? or u.real_name like ?)");
            String like = "%" + keyword.trim() + "%";
            args.add(like);
            args.add(like);
        }
    }
}
