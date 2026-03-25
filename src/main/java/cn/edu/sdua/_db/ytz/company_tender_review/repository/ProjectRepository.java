package cn.edu.sdua._db.ytz.company_tender_review.repository;

import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ProjectCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ProjectQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ProjectDetailResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ProjectListItem;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ProjectStatsResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class ProjectRepository {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final JdbcTemplate jdbcTemplate;

    public ProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long count(ProjectQueryRequest q) {
        StringBuilder sql = new StringBuilder("select count(*) from project p where 1=1");
        List<Object> args = new ArrayList<>();
        appendFilters(sql, args, q);
        return jdbcTemplate.queryForObject(sql.toString(), Long.class, args.toArray());
    }

    public List<ProjectListItem> list(ProjectQueryRequest q) {
        int offset = Math.max(0, (q.getPage() - 1) * q.getSize());
        StringBuilder sql = new StringBuilder("""
                select p.id,p.project_no,p.project_name,p.project_type,p.status,p.build_org_id,
                       bo.name as build_org_name,co.name as contractor_name,so.name as supervisor_name,
                       p.total_investment,p.contract_amount,p.planned_start,p.planned_end,p.location,p.created_at,
                       (select count(*) from review_issue ri where ri.project_id = p.id and ri.status = 1) as pending_issues
                  from project p
                  left join sys_org bo on bo.id = p.build_org_id
                  left join sys_org co on co.id = p.contractor_id
                  left join sys_org so on so.id = p.supervisor_id
                 where 1=1
                """);
        List<Object> args = new ArrayList<>();
        appendFilters(sql, args, q);
        sql.append(" order by p.id desc limit ? offset ?");
        args.add(q.getSize());
        args.add(offset);
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            ProjectListItem i = new ProjectListItem();
            i.setId(rs.getLong("id"));
            i.setProjectNo(rs.getString("project_no"));
            i.setProjectName(rs.getString("project_name"));
            i.setProjectType(rs.getObject("project_type", Integer.class));
            i.setProjectTypeName(projectTypeName(i.getProjectType()));
            i.setStatus(rs.getObject("status", Integer.class));
            i.setStatusName(statusName(i.getStatus()));
            i.setBuildOrgId(rs.getObject("build_org_id", Long.class));
            i.setBuildOrgName(rs.getString("build_org_name"));
            i.setContractorName(rs.getString("contractor_name"));
            i.setSupervisorName(rs.getString("supervisor_name"));
            i.setTotalInvestment(rs.getBigDecimal("total_investment"));
            i.setContractAmount(rs.getBigDecimal("contract_amount"));
            Date ps = rs.getDate("planned_start");
            Date pe = rs.getDate("planned_end");
            i.setPlannedStart(ps == null ? null : ps.toLocalDate().format(DATE_FMT));
            i.setPlannedEnd(pe == null ? null : pe.toLocalDate().format(DATE_FMT));
            i.setLocation(rs.getString("location"));
            i.setPendingIssues(rs.getInt("pending_issues"));
            i.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime().format(DT_FMT));
            return i;
        }, args.toArray());
    }

    public long insert(ProjectCreateRequest req) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    insert into project(project_no,project_name,project_type,build_org_id,contractor_id,supervisor_id,total_investment,contract_amount,location,approval_no,approval_date,planned_start,planned_end,status,description,creator_id,created_at,updated_at)
                    values(?,?,?,?,?,?,?,?,?,?,?,?,?,1,?,?,now(),now())
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, req.getProjectNo());
            ps.setString(2, req.getProjectName());
            ps.setObject(3, req.getProjectType());
            ps.setObject(4, req.getBuildOrgId());
            ps.setObject(5, req.getContractorId());
            ps.setObject(6, req.getSupervisorId());
            ps.setObject(7, req.getTotalInvestment());
            ps.setObject(8, req.getContractAmount());
            ps.setString(9, req.getLocation());
            ps.setString(10, req.getApprovalNo());
            ps.setObject(11, req.getApprovalDate());
            ps.setObject(12, req.getPlannedStart());
            ps.setObject(13, req.getPlannedEnd());
            ps.setString(14, req.getDescription());
            ps.setLong(15, 1L);
            return ps;
        }, keyHolder);
        Number key = Objects.requireNonNull(keyHolder.getKey(), "generated key required");
        return key.longValue();
    }

    public ProjectDetailResponse findDetail(long id) {
        List<ProjectDetailResponse> rows = jdbcTemplate.query("""
                select id,project_no,project_name,project_type,status,build_org_id,contractor_id,supervisor_id,total_investment,contract_amount,location,approval_no,approval_date,planned_start,planned_end,actual_start,actual_end,description,created_at
                from project where id=?
                """, (rs, rowNum) -> {
            ProjectDetailResponse d = new ProjectDetailResponse();
            d.setId(rs.getLong("id"));
            d.setProjectNo(rs.getString("project_no"));
            d.setProjectName(rs.getString("project_name"));
            d.setProjectType(rs.getObject("project_type", Integer.class));
            d.setStatus(rs.getObject("status", Integer.class));
            d.setBuildOrgId(rs.getObject("build_org_id", Long.class));
            d.setContractorId(rs.getObject("contractor_id", Long.class));
            d.setSupervisorId(rs.getObject("supervisor_id", Long.class));
            d.setTotalInvestment(rs.getBigDecimal("total_investment"));
            d.setContractAmount(rs.getBigDecimal("contract_amount"));
            d.setLocation(rs.getString("location"));
            d.setApprovalNo(rs.getString("approval_no"));
            Date ad = rs.getDate("approval_date");
            Date ps = rs.getDate("planned_start");
            Date pe = rs.getDate("planned_end");
            Date as = rs.getDate("actual_start");
            Date ae = rs.getDate("actual_end");
            d.setApprovalDate(ad == null ? null : ad.toLocalDate().format(DATE_FMT));
            d.setPlannedStart(ps == null ? null : ps.toLocalDate().format(DATE_FMT));
            d.setPlannedEnd(pe == null ? null : pe.toLocalDate().format(DATE_FMT));
            d.setActualStart(as == null ? null : as.toLocalDate().format(DATE_FMT));
            d.setActualEnd(ae == null ? null : ae.toLocalDate().format(DATE_FMT));
            d.setDescription(rs.getString("description"));
            d.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime().format(DT_FMT));
            return d;
        }, id);
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("project not found");
        }
        return rows.get(0);
    }

    public void updateStatus(long id, int targetStatus, java.time.LocalDate actualStart, java.time.LocalDate actualEnd) {
        Integer current = jdbcTemplate.queryForObject("select status from project where id = ?", Integer.class, id);
        if (current == null) {
            throw new IllegalArgumentException("project not found");
        }
        if (targetStatus != current && targetStatus != current + 1) {
            throw new IllegalArgumentException("invalid status transition");
        }
        jdbcTemplate.update("update project set status=?, actual_start=coalesce(?,actual_start), actual_end=coalesce(?,actual_end), updated_at=now() where id=?",
                targetStatus, actualStart, actualEnd, id);
    }

    public ProjectStatsResponse stats(long projectId) {
        ProjectStatsResponse s = new ProjectStatsResponse();
        s.setTasksTotal(queryInt("select count(*) from review_task where project_id=?", projectId));
        s.setTasksDone(queryInt("select count(*) from review_task where project_id=? and status=3", projectId));
        s.setTasksRunning(queryInt("select count(*) from review_task where project_id=? and status=2", projectId));
        s.setIssuesTotal(queryInt("select count(*) from review_issue where project_id=?", projectId));
        s.setIssuesPending(queryInt("select count(*) from review_issue where project_id=? and status in (1,2)", projectId));
        s.setIssuesResolved(queryInt("select count(*) from review_issue where project_id=? and status=3", projectId));
        s.setChangesTotal(queryInt("select count(*) from change_request where project_id=?", projectId));
        s.setChangesPending(queryInt("select count(*) from change_request where project_id=? and status in (1,2)", projectId));
        s.setDocsTotal(queryInt("select count(*) from document where project_id=?", projectId));
        int compliant = queryInt("select count(*) from review_result where project_id=? and overall_verdict=1", projectId);
        int total = queryInt("select count(*) from review_result where project_id=?", projectId);
        s.setComplianceRate(total == 0 ? 0D : (double) compliant / total);
        return s;
    }

    private int queryInt(String sql, Object arg) {
        Integer v = jdbcTemplate.queryForObject(sql, Integer.class, arg);
        return v == null ? 0 : v;
    }

    private void appendFilters(StringBuilder sql, List<Object> args, ProjectQueryRequest q) {
        if (q.getStatus() != null) { sql.append(" and p.status=?"); args.add(q.getStatus()); }
        if (q.getProjectType() != null) { sql.append(" and p.project_type=?"); args.add(q.getProjectType()); }
        if (q.getBuildOrgId() != null) { sql.append(" and p.build_org_id=?"); args.add(q.getBuildOrgId()); }
        if (q.getKeyword() != null && !q.getKeyword().isBlank()) {
            sql.append(" and (p.project_name like ? or p.project_no like ?)");
            String like = "%" + q.getKeyword().trim() + "%";
            args.add(like); args.add(like);
        }
        if (q.getPlannedStartFrom() != null) { sql.append(" and p.planned_start>=?"); args.add(q.getPlannedStartFrom()); }
        if (q.getPlannedStartTo() != null) { sql.append(" and p.planned_start<=?"); args.add(q.getPlannedStartTo()); }
    }

    private static String projectTypeName(Integer t) {
        return switch (t == null ? 0 : t) {
            case 1 -> "房建";
            case 2 -> "市政";
            case 3 -> "水利";
            case 4 -> "交通";
            case 5 -> "其他";
            default -> "未知";
        };
    }

    private static String statusName(Integer s) {
        return switch (s == null ? 0 : s) {
            case 1 -> "立项";
            case 2 -> "招标中";
            case 3 -> "施工中";
            case 4 -> "竣工";
            case 5 -> "归档";
            default -> "未知";
        };
    }
}
