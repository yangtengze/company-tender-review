package cn.edu.sdua._db.ytz.company_tender_review.repository;

import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ReviewResultQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ItemResultRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ReviewConfirmRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ReviewResultDetailResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ReviewResultListItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class ReviewResultRepository {
    private static final DateTimeFormatter ISO_DT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final JdbcTemplate jdbcTemplate;

    public ReviewResultRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long count(ReviewResultQueryRequest q) {
        StringBuilder sql = new StringBuilder("""
                select count(*)
                  from review_result rr
                  join review_task rt on rt.id = rr.task_id
                 where 1=1
                """);
        List<Object> args = new ArrayList<>();
        appendFilters(sql, args, q);
        Long c = jdbcTemplate.queryForObject(sql.toString(), Long.class, args.toArray());
        return c == null ? 0 : c;
    }

    public List<ReviewResultListItem> list(ReviewResultQueryRequest q) {
        StringBuilder sql = new StringBuilder("""
                select rr.id, rr.project_id, rr.task_id,
                       rr.overall_verdict, rr.risk_level, rr.review_status,
                       rr.reviewer_note, rr.reviewed_at, rr.created_at,
                       rr.reviewer_id, ru.real_name as reviewer_name,
                       rt.task_type
                  from review_result rr
                  join review_task rt on rt.id = rr.task_id
                  left join sys_user ru on ru.id = rr.reviewer_id
                 where 1=1
                """);
        List<Object> args = new ArrayList<>();
        appendFilters(sql, args, q);
        sql.append(" order by rr.id desc limit ? offset ?");

        int page = q.getPage() == null ? 1 : q.getPage();
        int size = q.getSize() == null ? 20 : q.getSize();
        args.add(size);
        args.add(Math.max(0, (page - 1) * size));

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            ReviewResultListItem d = new ReviewResultListItem();
            d.setId(rs.getLong("id"));
            d.setProjectId(rs.getLong("project_id"));
            d.setTaskId(rs.getLong("task_id"));
            d.setTaskType((Integer) rs.getObject("task_type"));

            d.setOverallVerdict((Integer) rs.getObject("overall_verdict"));
            d.setVerdictName(verdictName(d.getOverallVerdict()));

            d.setRiskLevel((Integer) rs.getObject("risk_level"));
            d.setRiskLevelName(riskLevelName(d.getRiskLevel()));

            d.setReviewStatus((Integer) rs.getObject("review_status"));
            d.setReviewStatusName(reviewStatusName(d.getReviewStatus()));

            d.setReviewerName(rs.getString("reviewer_name"));
            d.setReviewerNote(rs.getString("reviewer_note"));
            d.setCompletedAt(toIso(rs.getTimestamp("reviewed_at")));
            d.setCreatedAt(toIso(rs.getTimestamp("created_at")));

            d.setTaskTypeName(taskTypeName(d.getTaskType()));
            return d;
        }, args.toArray());
    }

    public ReviewResultDetailResponse findDetail(long id) {
        List<ReviewResultDetailResponse> rows = jdbcTemplate.query("""
                select rr.id, rr.project_id, rr.task_id,
                       rr.overall_verdict, rr.risk_level, rr.review_status,
                       rr.summary, rr.suggestion, rr.issue_count,
                       rr.model_name, rr.model_version, rr.tokens_used,
                       rr.reviewer_note, rr.reviewed_at, rr.created_at,
                       rr.reviewer_id, ru.real_name as reviewer_name,
                       rt.task_type
                  from review_result rr
                  join review_task rt on rt.id = rr.task_id
                  left join project p on p.id = rr.project_id
                  left join sys_user ru on ru.id = rr.reviewer_id
                 where rr.id = ?
                """, (rs, rowNum) -> {
            ReviewResultDetailResponse d = new ReviewResultDetailResponse();
            d.setId(rs.getLong("id"));
            d.setProjectId(rs.getLong("project_id"));
            d.setTaskId(rs.getLong("task_id"));

            d.setTaskType((Integer) rs.getObject("task_type"));
            d.setTaskTypeName(taskTypeName(d.getTaskType()));

            d.setOverallVerdict((Integer) rs.getObject("overall_verdict"));
            d.setVerdictName(verdictName(d.getOverallVerdict()));

            d.setRiskLevel((Integer) rs.getObject("risk_level"));
            d.setRiskLevelName(riskLevelName(d.getRiskLevel()));

            d.setReviewStatus((Integer) rs.getObject("review_status"));
            d.setReviewStatusName(reviewStatusName(d.getReviewStatus()));

            d.setReviewerName(rs.getString("reviewer_name"));
            d.setReviewerNote(rs.getString("reviewer_note"));
            d.setCompletedAt(toIso(rs.getTimestamp("reviewed_at")));
            d.setCreatedAt(toIso(rs.getTimestamp("created_at")));

            d.setSummary(rs.getString("summary"));
            d.setSuggestion(rs.getString("suggestion"));
            d.setIssueCount((Integer) rs.getObject("issue_count"));

            d.setModelName(rs.getString("model_name"));
            d.setModelVersion(rs.getString("model_version"));
            d.setTokensUsed((Integer) rs.getObject("tokens_used"));
            return d;
        }, id);

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("review result not found");
        }
        return rows.get(0);
    }

    public ReviewResultDetailResponse review(long id, ReviewConfirmRequest req, long reviewerId) {
        if (req == null) {
            throw new IllegalArgumentException("review request required");
        }

        int updated = jdbcTemplate.update("""
                update review_result
                   set review_status = ?,
                       reviewer_note = ?,
                       reviewer_id = ?,
                       reviewed_at = now()
                 where id = ?
                """, req.getReviewStatus(), req.getReviewerNote(), reviewerId, id);

        if (updated <= 0) {
            throw new IllegalArgumentException("review result not found");
        }
        return findDetail(id);
    }

    public Long insertItemResult(ItemResultRequest req) {
        Long resultId = getOrInitResultId(req.getTaskId());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    insert into review_item_result
                        (result_id, task_id, check_dimension, dimension_name, verdict, confidence, detail, evidence, issue_desc, suggestion, created_at, updated_at)
                    values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now())
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, resultId);
            ps.setLong(1, req.getTaskId());
            ps.setInt(2, req.getCheckDimension());
            ps.setString(3, req.getDimensionName());
            ps.setInt(4, req.getVerdict());
            ps.setBigDecimal(5, req.getConfidence());
            ps.setString(6, req.getDetail());
            ps.setString(7, req.getEvidence());
            ps.setString(8, req.getIssueDesc());
            ps.setString(9, req.getSuggestion());
            return ps;
        }, keyHolder);
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return id;
    }

    private Long getOrInitResultId(Long taskId) {
        Long existingId = jdbcTemplate.queryForObject(
            "select id from review_result where task_id = ?", 
            Long.class, taskId);
        
        if (existingId != null) {
            return existingId;
        }

        // 没有则初始化占位记录
        // 注意：review_result 表的 project_id 是 NOT NULL，需要从 review_task 关联查出来
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            String sql = """
                insert into review_result (task_id, project_id, overall_verdict, issue_count, created_at, updated_at)
                select ?, project_id, 1, 0, now(), now() 
                from review_task where id = ?
                """;
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, taskId);
            ps.setLong(2, taskId);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public byte[] export(long id, String format) {
        ReviewResultDetailResponse d = findDetail(id);
        String f = format == null || format.isBlank() ? "pdf" : format.trim().toLowerCase();
        String content = "ReviewResultExport id=" + d.getId() + ", format=" + f;
        return content.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private void appendFilters(StringBuilder sql, List<Object> args, ReviewResultQueryRequest q) {
        if (q.getProjectId() != null) {
            sql.append(" and rr.project_id = ?");
            args.add(q.getProjectId());
        }
        if (q.getOverallVerdict() != null) {
            sql.append(" and rr.overall_verdict = ?");
            args.add(q.getOverallVerdict());
        }
        if (q.getRiskLevel() != null) {
            sql.append(" and rr.risk_level = ?");
            args.add(q.getRiskLevel());
        }
        if (q.getReviewStatus() != null) {
            sql.append(" and rr.review_status = ?");
            args.add(q.getReviewStatus());
        }
        if (q.getTaskType() != null) {
            sql.append(" and rt.task_type = ?");
            args.add(q.getTaskType());
        }
        if (q.getDateFrom() != null) {
            sql.append(" and rr.reviewed_at >= ?");
            args.add(Timestamp.valueOf(q.getDateFrom()));
        }
        if (q.getDateTo() != null) {
            sql.append(" and rr.reviewed_at <= ?");
            args.add(Timestamp.valueOf(q.getDateTo()));
        }
    }

    private static String toIso(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime().format(ISO_DT);
    }

    private static String taskTypeName(Integer v) {
        return switch (v == null ? 0 : v) {
            case 1 -> "招投标审查";
            case 2 -> "施工变更审查";
            default -> "未知";
        };
    }

    private static String verdictName(Integer v) {
        return switch (v == null ? 0 : v) {
            case 1 -> "合规";
            case 2 -> "存在问题";
            case 3 -> "严重违规";
            default -> "未知";
        };
    }

    private static String riskLevelName(Integer v) {
        return switch (v == null ? 0 : v) {
            case 1 -> "低";
            case 2 -> "中";
            case 3 -> "高";
            case 4 -> "极高";
            default -> "未知";
        };
    }

    private static String reviewStatusName(Integer v) {
        return switch (v == null ? 0 : v) {
            case 1 -> "待复核";
            case 2 -> "已确认";
            case 3 -> "已驳回";
            default -> "未知";
        };
    }
}

