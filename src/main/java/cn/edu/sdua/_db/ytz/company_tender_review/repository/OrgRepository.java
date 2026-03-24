package cn.edu.sdua._db.ytz.company_tender_review.repository;

import cn.edu.sdua._db.ytz.company_tender_review.repository.model.OrgRow;
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
public class OrgRepository {
    private static final RowMapper<OrgRow> MAPPER = (rs, rowNum) -> new OrgRow(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("code"),
            rs.getObject("type", Integer.class),
            rs.getObject("parent_id", Long.class),
            rs.getString("address"),
            rs.getObject("status", Integer.class),
            rs.getTimestamp("created_at").toLocalDateTime()
    );

    private final JdbcTemplate jdbcTemplate;

    public OrgRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<OrgRow> findAll(Integer type, Integer status) {
        StringBuilder sql = new StringBuilder("""
                select id, name, code, type, parent_id, address, status, created_at
                  from sys_org
                 where 1=1
                """);
        List<Object> args = new ArrayList<>();
        if (type != null) {
            sql.append(" and type = ?");
            args.add(type);
        }
        if (status != null) {
            sql.append(" and status = ?");
            args.add(status);
        }
        sql.append(" order by id asc");
        return jdbcTemplate.query(sql.toString(), MAPPER, args.toArray());
    }

    public Optional<OrgRow> findById(Long id) {
        List<OrgRow> rows = jdbcTemplate.query("""
                select id, name, code, type, parent_id, address, status, created_at
                  from sys_org
                 where id = ?
                """, MAPPER, id);
        return rows.stream().findFirst();
    }

    public long insert(String name, String code, Integer type, Long parentId, String address) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    insert into sys_org (name, code, type, parent_id, address, status, created_at, updated_at)
                    values (?, ?, ?, ?, ?, 1, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            LocalDateTime now = LocalDateTime.now();
            ps.setString(1, name);
            ps.setString(2, code);
            ps.setObject(3, type);
            ps.setObject(4, parentId);
            ps.setString(5, address);
            ps.setTimestamp(6, Timestamp.valueOf(now));
            ps.setTimestamp(7, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);
        Number key = Objects.requireNonNull(keyHolder.getKey(), "generated key required");
        return key.longValue();
    }

    public void update(Long id, String name, String code, String address, Integer status) {
        StringBuilder sql = new StringBuilder("update sys_org set updated_at = ? ");
        List<Object> args = new ArrayList<>();
        args.add(Timestamp.valueOf(LocalDateTime.now()));

        if (name != null) {
            sql.append(", name = ? ");
            args.add(name);
        }
        if (code != null) {
            sql.append(", code = ? ");
            args.add(code);
        }
        if (address != null) {
            sql.append(", address = ? ");
            args.add(address);
        }
        if (status != null) {
            sql.append(", status = ? ");
            args.add(status);
        }
        sql.append(" where id = ?");
        args.add(id);

        int updated = jdbcTemplate.update(sql.toString(), args.toArray());
        if (updated == 0) {
            throw new IllegalArgumentException("org not found");
        }
    }
}
