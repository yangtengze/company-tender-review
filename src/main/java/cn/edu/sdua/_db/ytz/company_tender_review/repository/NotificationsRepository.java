package cn.edu.sdua._db.ytz.company_tender_review.repository;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.edu.sdua._db.ytz.company_tender_review.dto.request.NotificationQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.NotificationItem;

@Repository
public class NotificationsRepository {
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final JdbcTemplate jdbcTemplate;

    public NotificationsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long count(NotificationQueryRequest request) {
        StringBuilder sql = new StringBuilder("""
                select
                    count(*)
                    from sys_notification
                where 1=1
                """);
        List<Object> args = new ArrayList<>();
        appendFilter(sql,args,request);
        return jdbcTemplate.queryForObject(sql.toString(), Long.class, args.toArray());
    }

    public List<NotificationItem> list(NotificationQueryRequest request) {
        StringBuilder sql = new StringBuilder("""
                select
                    id, type, title, content,
                    ref_type, ref_id, is_read, created_at, updated_at
                    from sys_notification
                where 1=1
                """);
        List<Object> args = new ArrayList<>();
        appendFilter(sql, args, request);
        int page = request.getPage();
        int size = request.getSize();
        sql.append(" order by id asc limit ? offset ?");
        args.add(size);
        args.add(Math.max(0, (page - 1) * size));
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            NotificationItem d = new NotificationItem();
            d.setId(rs.getLong("id"));
            d.setType(rs.getInt("type"));
            d.setTypeName(typeName(d.getType()));
            d.setTitle(rs.getString("title"));
            d.setContent(rs.getString("content"));
            d.setRefType(rs.getString("ref_type"));
            d.setRefId(rs.getLong("ref_id"));
            d.setIsRead(rs.getInt("is_read"));
            d.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime().format(DT_FMT));
            return d;
        }, args.toArray());
    }

    public void read(Long id) {
        StringBuilder sql = new StringBuilder("""
                update sys_notification
                    set is_read = 1, updated_at = now()
                where id = ?
                """);
        jdbcTemplate.update(sql.toString(), id);
    }

    public void readAll(Long authId) {
        StringBuilder sql = new StringBuilder("""
                update sys_notification
                    set is_read = 1, updated_at = now()
                where is_read = 0 and user_id = ?
                """);
        jdbcTemplate.update(sql.toString(), authId);
    }

    private void appendFilter(StringBuilder sql, List<Object> args, NotificationQueryRequest request) {
        if(request.getIsRead() != null) {
            sql.append(" and is_read = ?");
            args.add(request.getIsRead());
        }
        if(request.getType() != null) {
            sql.append(" and type = ?");
            args.add(request.getType());
        }
    }

    private String typeName(Integer v) {
        return switch (v == null ? 0 : v) {
            case 1 -> "任务完成";
            case 2 -> "问题待处理";
            case 3 -> "复核提醒";
            case 4 -> "系统信息";
            default -> "未知";
        };
    }
}
