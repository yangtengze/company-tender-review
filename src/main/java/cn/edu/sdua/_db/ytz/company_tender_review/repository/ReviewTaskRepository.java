package cn.edu.sdua._db.ytz.company_tender_review.repository;

import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ReviewTaskCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ReviewTaskQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ReviewFullResultResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ReviewTaskDetailResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.TaskDocItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class ReviewTaskRepository {
    private static final DateTimeFormatter ISO_DT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public ReviewTaskRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public long create(ReviewTaskCreateRequest req) {
        if (req.getTaskType() == 2 && req.getChangeId() == null) {
            throw new IllegalArgumentException("changeId required when taskType=2");
        }
        ensureDocsBelongProject(req.getDocIds(), req.getProjectId());
        String taskNo = nextTaskNo();
        int priority = req.getPriority() == null ? 2 : req.getPriority();
        int triggerMode = req.getTriggerMode() == null ? 1 : req.getTriggerMode();

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    insert into review_task
                    (task_no, project_id, task_type, change_id, task_name, status, priority,
                     assignee_id, trigger_mode, creator_id, created_at, updated_at)
                    values (?, ?, ?, ?, ?, 1, ?, ?, ?, null, now(), now())
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, taskNo);
            ps.setLong(2, req.getProjectId());
            ps.setInt(3, req.getTaskType());
            if (req.getChangeId() == null) ps.setNull(4, java.sql.Types.BIGINT);
            else ps.setLong(4, req.getChangeId());
            ps.setString(5, req.getTaskName());
            ps.setInt(6, priority);
            if (req.getAssigneeId() == null) ps.setNull(7, java.sql.Types.BIGINT);
            else ps.setLong(7, req.getAssigneeId());
            ps.setInt(8, triggerMode);
            return ps;
        }, keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        Map<Long, String> docRoles = req.getDocRoles() == null ? Collections.emptyMap() : req.getDocRoles();
        for (Long docId : req.getDocIds()) {
            jdbcTemplate.update("""
                    insert into review_task_doc (task_id, doc_id, doc_role)
                    values (?, ?, ?)
                    """, id, docId, docRoles.get(docId));
        }
        return id;
    }

    public ReviewTaskDetailResponse findTaskDetail(long taskId) {
        ReviewTaskQueryRequest q = new ReviewTaskQueryRequest();
        q.setPage(1);
        q.setSize(1);
        List<ReviewTaskDetailResponse> list = jdbcTemplate.query("""
                select t.id, t.task_no, t.project_id, p.project_name, t.task_type, t.change_id,
                       t.task_name, t.status, t.priority, t.assignee_id, u.real_name as assignee_name,
                       t.start_at, t.end_at, t.duration_ms, t.created_at
                  from review_task t
                  left join project p on p.id = t.project_id
                  left join sys_user u on u.id = t.assignee_id
                 where t.id = ?
                """, (rs, rowNum) -> {
            ReviewTaskDetailResponse d = new ReviewTaskDetailResponse();
            d.setId(rs.getLong("id"));
            d.setTaskNo(rs.getString("task_no"));
            d.setProjectId(rs.getLong("project_id"));
            d.setProjectName(rs.getString("project_name"));
            d.setTaskType((Integer) rs.getObject("task_type"));
            d.setTaskTypeName(taskTypeName(d.getTaskType()));
            d.setChangeId(rs.getObject("change_id", Long.class));
            d.setTaskName(rs.getString("task_name"));
            d.setStatus((Integer) rs.getObject("status"));
            d.setStatusName(taskStatusName(d.getStatus()));
            d.setPriority((Integer) rs.getObject("priority"));
            d.setAssigneeId(rs.getObject("assignee_id", Long.class));
            d.setAssigneeName(rs.getString("assignee_name"));
            d.setStartAt(toIso(rs.getTimestamp("start_at")));
            d.setEndAt(toIso(rs.getTimestamp("end_at")));
            d.setDurationMs((Integer) rs.getObject("duration_ms"));
            d.setCreatedAt(toIso(Objects.requireNonNull(rs.getTimestamp("created_at"))));
            return d;
        }, taskId);
        if (list.isEmpty()) {
            throw new IllegalArgumentException("review task not found");
        }
        ReviewTaskDetailResponse d = list.get(0);
        d.setDocs(findTaskDocs(taskId));
        return d;
    }

    public void updateStatusAndTime(Long id, Integer status) {
        StringBuilder sql = new StringBuilder("""
            update review_task set 
                status = ?, 
                start_at = IF(? = 2, NOW(), start_at), 
                end_at = IF(? = 3, NOW(), end_at) 
            where id = ?
        """);
        jdbcTemplate.update(sql.toString(), status, status, status, id);
    }

    public long count(ReviewTaskQueryRequest q) {
        StringBuilder sql = new StringBuilder("select count(*) from review_task t where 1=1");
        List<Object> args = new ArrayList<>();
        appendQueryFilters(sql, args, q);
        Long c = jdbcTemplate.queryForObject(sql.toString(), Long.class, args.toArray());
        return c == null ? 0 : c;
    }

    public List<ReviewTaskDetailResponse> list(ReviewTaskQueryRequest q) {
        StringBuilder sql = new StringBuilder("""
                select t.id, t.task_no, t.project_id, p.project_name, t.task_type, t.change_id,
                       t.task_name, t.status, t.priority, t.assignee_id, u.real_name as assignee_name,
                       t.start_at, t.end_at, t.duration_ms, t.created_at
                  from review_task t
                  left join project p on p.id = t.project_id
                  left join sys_user u on u.id = t.assignee_id
                 where 1=1
                """);
        List<Object> args = new ArrayList<>();
        appendQueryFilters(sql, args, q);
        sql.append(" order by t.id desc limit ? offset ?");
        int page = q.getPage() == null ? 1 : q.getPage();
        int size = q.getSize() == null ? 20 : q.getSize();
        args.add(size);
        args.add(Math.max(0, (page - 1) * size));

        List<ReviewTaskDetailResponse> rows = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            ReviewTaskDetailResponse d = new ReviewTaskDetailResponse();
            d.setId(rs.getLong("id"));
            d.setTaskNo(rs.getString("task_no"));
            d.setProjectId(rs.getLong("project_id"));
            d.setProjectName(rs.getString("project_name"));
            d.setTaskType((Integer) rs.getObject("task_type"));
            d.setTaskTypeName(taskTypeName(d.getTaskType()));
            d.setChangeId(rs.getObject("change_id", Long.class));
            d.setTaskName(rs.getString("task_name"));
            d.setStatus((Integer) rs.getObject("status"));
            d.setStatusName(taskStatusName(d.getStatus()));
            d.setPriority((Integer) rs.getObject("priority"));
            d.setAssigneeId(rs.getObject("assignee_id", Long.class));
            d.setAssigneeName(rs.getString("assignee_name"));
            d.setStartAt(toIso(rs.getTimestamp("start_at")));
            d.setEndAt(toIso(rs.getTimestamp("end_at")));
            d.setDurationMs((Integer) rs.getObject("duration_ms"));
            d.setCreatedAt(toIso(Objects.requireNonNull(rs.getTimestamp("created_at"))));
            return d;
        }, args.toArray());

        for (ReviewTaskDetailResponse d : rows) {
            d.setDocs(findTaskDocs(d.getId()));
        }
        return rows;
    }
    
    public ReviewFullResultResponse findFullResult(long taskId) {
        List<ReviewFullResultResponse> main = jdbcTemplate.query("""
                select t.id as task_id, t.task_name, t.project_id, p.project_name,
                       rr.overall_verdict, rr.risk_level, rr.summary, rr.suggestion, rr.issue_count,
                       rr.model_name, rr.tokens_used, rr.review_status, ru.real_name as reviewer_name,
                       rr.reviewer_note, rr.reviewed_at, rr.created_at
                  from review_task t
                  left join review_result rr on rr.task_id = t.id
                  left join project p on p.id = t.project_id
                  left join sys_user ru on ru.id = rr.reviewer_id
                 where t.id = ?
                """, (rs, rowNum) -> {
            ReviewFullResultResponse r = new ReviewFullResultResponse();
            r.setTaskId(rs.getLong("task_id"));
            r.setTaskName(rs.getString("task_name"));
            r.setProjectId(rs.getObject("project_id", Long.class));
            r.setProjectName(rs.getString("project_name"));
            r.setOverallVerdict((Integer) rs.getObject("overall_verdict"));
            r.setVerdictName(verdictName(r.getOverallVerdict()));
            r.setRiskLevel((Integer) rs.getObject("risk_level"));
            r.setRiskLevelName(riskLevelName(r.getRiskLevel()));
            r.setSummary(rs.getString("summary"));
            r.setSuggestion(rs.getString("suggestion"));
            r.setIssueCount((Integer) rs.getObject("issue_count"));
            r.setModelName(rs.getString("model_name"));
            r.setTokensUsed((Integer) rs.getObject("tokens_used"));
            r.setReviewStatus((Integer) rs.getObject("review_status"));
            r.setReviewStatusName(reviewStatusName(r.getReviewStatus()));
            r.setReviewerName(rs.getString("reviewer_name"));
            r.setReviewerNote(rs.getString("reviewer_note"));
            r.setReviewedAt(toIso(rs.getTimestamp("reviewed_at")));
            r.setCreatedAt(toIso(rs.getTimestamp("created_at")));
            return r;
        }, taskId);
        if (main.isEmpty()) {
            throw new IllegalArgumentException("review task not found");
        }
        ReviewFullResultResponse resp = main.get(0);

        resp.setItems(jdbcTemplate.query("""
                select id, check_dimension, dimension_name, verdict, confidence, detail, evidence, issue_desc,
                       suggestion, ref_law_ids, ref_case_ids, updated_at
                  from review_item_result
                 where task_id = ?
                 order by id asc
                """, (rs, rowNum) -> {
            ReviewFullResultResponse.ReviewItemResultDto item = new ReviewFullResultResponse.ReviewItemResultDto();
            item.setId(rs.getLong("id"));
            item.setCheckDimension((Integer) rs.getObject("check_dimension"));
            item.setDimensionName(rs.getString("dimension_name"));
            item.setVerdict((Integer) rs.getObject("verdict"));
            item.setVerdictName(verdictName(item.getVerdict()));
            item.setConfidence(rs.getBigDecimal("confidence"));
            item.setDetail(rs.getString("detail"));
            item.setEvidence(rs.getString("evidence"));
            item.setIssueDesc(rs.getString("issue_desc"));
            item.setSuggestion(rs.getString("suggestion"));
            item.setRefLaws(parseLawRefs(rs.getString("ref_law_ids")));
            item.setRefCases(parseCaseRefs(rs.getString("ref_case_ids")));
            item.setUpdatedAt(toIso(rs.getTimestamp("updated_at")));
            return item;
        }, taskId));

        // issues from task's result if exists; fallback by project+task relation
        Long resultId = jdbcTemplate.query("""
                select id from review_result where task_id = ?
                """, (rs, i) -> rs.getLong(1), taskId).stream().findFirst().orElse(null);
        if (resultId == null) {
            resp.setIssues(Collections.emptyList());
        } else {
            resp.setIssues(jdbcTemplate.query("""
                    select id, issue_type, severity, title, description, location, suggestion, status, updated_at
                      from review_issue
                     where result_id = ?
                     order by id asc
                    """, (rs, rowNum) -> {
                ReviewFullResultResponse.ReviewIssueItemDto issue = new ReviewFullResultResponse.ReviewIssueItemDto();
                issue.setId(rs.getLong("id"));
                issue.setIssueType((Integer) rs.getObject("issue_type"));
                issue.setTypeName(issueTypeName(issue.getIssueType()));
                issue.setSeverity((Integer) rs.getObject("severity"));
                issue.setSeverityName(severityName(issue.getSeverity()));
                issue.setTitle(rs.getString("title"));
                issue.setDescription(rs.getString("description"));
                issue.setLocation(rs.getString("location"));
                issue.setSuggestion(rs.getString("suggestion"));
                issue.setStatus((Integer) rs.getObject("status"));
                issue.setStatusName(issueStatusName(issue.getStatus()));
                issue.setUpdatedAt(toIso(rs.getTimestamp("updated_at")));
                return issue;
            }, resultId));
        }
        return resp;
    }

    private List<TaskDocItem> findTaskDocs(long taskId) {
        return jdbcTemplate.query("""
                select d.id as doc_id, d.doc_name, d.doc_type, rtd.doc_role, d.file_ext, d.file_size
                  from review_task_doc rtd
                  join document d on d.id = rtd.doc_id
                 where rtd.task_id = ?
                 order by d.id asc
                """, (rs, rowNum) -> {
            TaskDocItem item = new TaskDocItem();
            item.setDocId(rs.getLong("doc_id"));
            item.setDocName(rs.getString("doc_name"));
            item.setDocType((Integer) rs.getObject("doc_type"));
            item.setDocRole(rs.getString("doc_role"));
            item.setFileExt(rs.getString("file_ext"));
            item.setFileSize(rs.getObject("file_size", Long.class));
            return item;
        }, taskId);
    }

    private void appendQueryFilters(StringBuilder sql, List<Object> args, ReviewTaskQueryRequest q) {
        if (q.getProjectId() != null) {
            sql.append(" and t.project_id = ?");
            args.add(q.getProjectId());
        }
        if (q.getTaskType() != null) {
            sql.append(" and t.task_type = ?");
            args.add(q.getTaskType());
        }
        if (q.getStatus() != null) {
            sql.append(" and t.status = ?");
            args.add(q.getStatus());
        }
        if (q.getAssigneeId() != null) {
            sql.append(" and t.assignee_id = ?");
            args.add(q.getAssigneeId());
        }
        if (q.getDateFrom() != null) {
            sql.append(" and t.created_at >= ?");
            args.add(java.sql.Timestamp.valueOf(q.getDateFrom()));
        }
        if (q.getDateTo() != null) {
            sql.append(" and t.created_at <= ?");
            args.add(java.sql.Timestamp.valueOf(q.getDateTo()));
        }
    }

    private void ensureDocsBelongProject(List<Long> docIds, Long projectId) {
        if (docIds == null || docIds.isEmpty()) {
            throw new IllegalArgumentException("docIds required");
        }
        for (Long docId : docIds) {
            Long p = jdbcTemplate.queryForObject("select project_id from document where id = ?", Long.class, docId);
            if (!Objects.equals(p, projectId)) {
                throw new IllegalArgumentException("doc project mismatch");
            }
        }
    }

    private String nextTaskNo() {
        LocalDate today = LocalDate.now();
        String prefix = "RT-" + today.format(DateTimeFormatter.BASIC_ISO_DATE) + "-";
        Long count = jdbcTemplate.queryForObject("select count(*) from review_task where task_no like ?", Long.class, prefix + "%");
        long seq = (count == null ? 0 : count) + 1;
        return prefix + String.format("%04d", seq);
    }

    private List<ReviewFullResultResponse.LawRef> parseLawRefs(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            List<Long> ids = objectMapper.readValue(json, new TypeReference<List<Long>>() {});
            return ids.stream().map(id -> {
                ReviewFullResultResponse.LawRef lr = new ReviewFullResultResponse.LawRef();
                lr.setId(id);
                lr.setTitle(null);
                lr.setClauseNo(null);
                return lr;
            }).collect(Collectors.toList());
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    private List<ReviewFullResultResponse.CaseRef> parseCaseRefs(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            List<Long> ids = objectMapper.readValue(json, new TypeReference<List<Long>>() {});
            return ids.stream().map(id -> {
                ReviewFullResultResponse.CaseRef c = new ReviewFullResultResponse.CaseRef();
                c.setId(id);
                c.setTitle(null);
                return c;
            }).collect(Collectors.toList());
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    private static String toIso(java.sql.Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime().format(ISO_DT);
    }

    private static String taskTypeName(Integer v) {
        return switch (v == null ? 0 : v) {
            case 1 -> "招投标审查";
            case 2 -> "施工变更审查";
            default -> "未知";
        };
    }

    private static String taskStatusName(Integer v) {
        return switch (v == null ? 0 : v) {
            case 1 -> "待执行";
            case 2 -> "执行中";
            case 3 -> "已完成";
            case 4 -> "已失败";
            case 5 -> "已取消";
            default -> "未知";
        };
    }

    private static String verdictName(Integer v) {
        return switch (v == null ? 0 : v) {
            case 1 -> "合规";
            case 2 -> "存在问题";
            case 3 -> "严重违规";
            case 4 -> "无法判断";
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

    private static String issueTypeName(Integer v) {
        return switch (v == null ? 0 : v) {
            case 1 -> "程序违规";
            case 2 -> "内容不一致";
            case 3 -> "时序异常";
            case 4 -> "条款违规";
            case 5 -> "成本异常";
            case 6 -> "其他";
            default -> "未知";
        };
    }

    private static String severityName(Integer v) {
        return switch (v == null ? 0 : v) {
            case 1 -> "提示";
            case 2 -> "警告";
            case 3 -> "错误";
            case 4 -> "严重";
            default -> "未知";
        };
    }

    private static String issueStatusName(Integer v) {
        return switch (v == null ? 0 : v) {
            case 1 -> "待整改";
            case 2 -> "整改中";
            case 3 -> "已整改";
            case 4 -> "已忽略";
            default -> "未知";
        };
    }


}

