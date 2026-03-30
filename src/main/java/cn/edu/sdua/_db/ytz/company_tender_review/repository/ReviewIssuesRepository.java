package cn.edu.sdua._db.ytz.company_tender_review.repository;

import java.util.ArrayList;
import java.util.List;



import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cn.edu.sdua._db.ytz.company_tender_review.dto.request.IssueHandleRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.IssueQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.IssueStatsQuery;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.IssueDetailResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.IssueStatsResponse;

@Repository
public class ReviewIssuesRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<IssueDetailResponse> MAPPER = (rs, rowNum) -> {
        IssueDetailResponse d = new IssueDetailResponse();
        d.setId(rs.getLong("id"));
        d.setItemResultId(rs.getLong("item_result_id"));
        d.setResultId(rs.getLong("result_id"));
        d.setTaskId(rs.getLong("task_id"));
        d.setTaskName(rs.getString("task_name"));
        d.setProjectId(rs.getLong("project_id"));
        d.setProjectName(rs.getString("project_name"));
        d.setCheckDimension((Integer) rs.getObject("check_dimension"));
        d.setDimensionName(rs.getString("dimension_name"));
        d.setIssueType((Integer) rs.getObject("issue_type"));
        d.setTypeName(typeName(d.getIssueType()));
        d.setSeverity((Integer) rs.getObject("severity"));
        d.setSeverityName(severityName(d.getSeverity()));
        d.setTitle(rs.getString("title"));
        d.setDescription(rs.getString("description"));
        d.setLocation(rs.getString("location"));
        d.setSuggestion(rs.getString("suggestion"));
        d.setStatus((Integer) rs.getObject("status"));
        d.setStatusName(statusName(d.getStatus()));
        d.setHandleNote(rs.getString("handle_note"));
        d.setHandledBy(rs.getObject("handled_by") != null ? rs.getLong("handled_by") : null);
        d.setHandledByName(rs.getString("real_name"));
        d.setHandledAt(rs.getString("handled_at"));
        d.setCreatedAt(rs.getString("created_at"));
        d.setUpdatedAt(rs.getString("updated_at"));
        return d;
    };

    public ReviewIssuesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long count(IssueQueryRequest request) {
        StringBuilder sql = new StringBuilder("""
                select count(*) 
                from review_issue ri
                join review_item_result rir on ri.item_result_id = rir.id
                where 1=1
            """);
        List<Object> args = new ArrayList<>();
        appendFilters(sql, args, request);
        Long c = jdbcTemplate.queryForObject(sql.toString(), Long.class, args.toArray());
        return c == null ? 0 : c;
    }
    public List<IssueDetailResponse> list(IssueQueryRequest request) {
        StringBuilder sql = new StringBuilder("""
                select ri.id, ri.item_result_id, ri.result_id,
                    rt.id as task_id, rt.task_name, p.id as project_id, p.project_name, 
                    rir.check_dimension, rir.dimension_name,
                    ri.issue_type, ri.severity, ri.title, ri.description, 
                    ri.location, ri.suggestion, ri.status, ri.handle_note,
                    ri.handled_by, u.real_name, ri.handled_at, 
                    ri.created_at, ri.updated_at
                from review_issue ri
                join project p on ri.project_id = p.id
                join review_item_result rir on ri.item_result_id = rir.id
                join review_task rt on rir.task_id = rt.id
                left join sys_user u on ri.handled_by = u.id
                where 1=1
            """);
        List<Object> args = new ArrayList<>();
        appendFilters(sql, args, request);
        sql.append(" order by ri.id limit ? offset ?");
        int page = request.getPage() == null ? 1 : request.getPage();
        int size = request.getSize() == null ? 20 : request.getSize();
        args.add(size);
        args.add(Math.max(0, (page - 1) * size));
        
        return jdbcTemplate.query(sql.toString(), MAPPER, args.toArray());
    }
    public IssueDetailResponse update(Long id, IssueHandleRequest request, Long authId) {
        if (request == null) {
            throw new IllegalArgumentException("handle request required");
        }
        int updated = jdbcTemplate.update("""
            update review_issue
                set status = ?,
                    handle_note = ?,
                    handled_by = ?,
                    handled_at = now()
            where id = ?
        """,request.getStatus(),request.getHandleNote(),authId,id);
        if (updated <= 0) {
            throw new IllegalArgumentException("handle issue not found");
        }
        return findDetail(id);
    }
public IssueStatsResponse calculate(IssueStatsQuery request) {
    Long projectId = request.getProjectId();
    
    List<IssueStatsResponse> rows = jdbcTemplate.query("""
        select 
            count(*) as total,
            sum(case when status = 1 then 1 else 0 end) as pending,
            sum(case when status = 2 then 1 else 0 end) as handling,
            sum(case when status = 3 then 1 else 0 end) as resolved,
            sum(case when status = 4 then 1 else 0 end) as ignored
        from review_issue
        where project_id = ?
        """, (rs, rowNum) -> {
            IssueStatsResponse d = new IssueStatsResponse();
            d.setTotal(rs.getLong("total"));
            d.setPending(rs.getLong("pending"));
            d.setHandling(rs.getLong("handling"));
            d.setResolved(rs.getLong("resolved"));
            d.setIgnored(rs.getLong("ignored"));
            return d;
        }, projectId);
    
    if (rows.isEmpty()) {
        throw new IllegalArgumentException("project issues not found");
    }
    
    IssueStatsResponse data = rows.get(0);
    data.setBySeverity(jdbcTemplate.query("""
        select
            severity, 
            count(*) as total,
            sum(case when status = 3 then 1 else 0 end) as resolved
        from review_issue
        where project_id = ?
        group by severity
        order by severity
        """, (rs, rowNum) -> {
            IssueStatsResponse.SeverityCount d = new IssueStatsResponse.SeverityCount();
            d.setSeverity((Integer) rs.getObject("severity"));
            d.setSeverityName(severityName(d.getSeverity()));
            d.setTotal(rs.getLong("total"));
            d.setResolved(rs.getLong("resolved"));
            return d;
        }, projectId));
    
    data.setByType(jdbcTemplate.query("""
        select
            issue_type,
            count(*) as total
        from review_issue
        where project_id = ?
        group by issue_type
        order by issue_type
        """, (rs, rowNum) -> {
            IssueStatsResponse.TypeCount d = new IssueStatsResponse.TypeCount();
            d.setIssueType((Integer) rs.getObject("issue_type"));
            d.setTypeName(typeName(d.getIssueType()));
            d.setTotal(rs.getLong("total"));
            return d;
        }, projectId));
    
    return data;
}

    private IssueDetailResponse findDetail(Long id) {
        List<IssueDetailResponse> rows = jdbcTemplate.query("""
            select ri.id, ri.item_result_id, ri.result_id,
                    rt.id as task_id, rt.task_name, p.id as project_id, p.project_name, 
                    rir.check_dimension, rir.dimension_name,
                    ri.issue_type, ri.severity, ri.title, ri.description, 
                    ri.location, ri.suggestion, ri.status, ri.handle_note,
                    ri.handled_by, u.real_name, ri.handled_at, 
                    ri.created_at, ri.updated_at
                from review_issue ri
                join project p on ri.project_id = p.id
                join review_item_result rir on ri.item_result_id = rir.id
                join review_task rt on rir.task_id = rt.id
                left join sys_user u on ri.handled_by = u.id
                where ri.id = ?
            """, MAPPER, id);
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("handle issue not found");
        }
        return rows.get(0);
    }


    private void appendFilters(StringBuilder sql, List<Object> args, IssueQueryRequest request) {
        if(request.getProjectId() != null) {
            sql.append(" and ri.project_id = ?");
            args.add(request.getProjectId());
        }
        if(request.getResultId() != null) {
            sql.append(" and ri.result_id = ?");
            args.add(request.getResultId());
        }
        if(request.getSeverity() != null) {
            sql.append(" and ri.severity = ?");
            args.add(request.getSeverity());
        }
        if(request.getIssueType() != null) {
            sql.append(" and ri.issue_type = ?");
            args.add(request.getIssueType());
        }
        if(request.getStatus() != null) {
            sql.append(" and ri.status = ?");
            args.add(request.getStatus());
        }
    }
    private static String typeName(Integer v) {
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
    private static String statusName(Integer v) {
        return switch (v == null ? 0 : v) {
            case 1 -> "待整改";
            case 2 -> "整改中";
            case 3 -> "已整改";
            case 4 -> "已忽略";
            default -> "未知";
        };
    }
}   
