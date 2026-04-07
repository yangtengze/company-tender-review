package cn.edu.sdua._db.ytz.company_tender_review.repository;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.edu.sdua._db.ytz.company_tender_review.dto.request.DashboardOverviewQuery;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.DimensionStatsQuery;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.IssueTrendQuery;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.DashboardOverviewResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.DimensionStatItem;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.TrendDataPoint;

@Repository
public class DashboardRepository {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private final JdbcTemplate jdbcTemplate;

    public DashboardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DashboardOverviewResponse overview(DashboardOverviewQuery request) {
        Long orgId = request.getOrgId();
        String activeProjectsSql = """
            SELECT COUNT(*) FROM project
            WHERE status < 5
            """ + (orgId != null ? " AND (build_org_id = ? OR contractor_id = ? OR supervisor_id = ?)" : "");
        String pendingTasksSql = """
            SELECT COUNT(*) FROM review_task
            WHERE status IN (1, 2)
            """ + (orgId != null ? " AND project_id IN (SELECT id FROM project WHERE build_org_id = ?)" : "");
        String completedTasksSql = """
            SELECT COUNT(*) FROM review_task
            WHERE status = 3
            """ + (orgId != null ? " AND project_id IN (SELECT id FROM project WHERE build_org_id = ?)" : "");
        String highRiskIssuesSql = """
            SELECT COUNT(*) FROM review_issue
            WHERE severity >= 3 AND status = 1
            """ + (orgId != null ? " AND project_id IN (SELECT id FROM project WHERE build_org_id = ?)" : "");
        String pendingChangesSql = """
            SELECT COUNT(*) FROM change_request
            WHERE status = 1
            """ + (orgId != null ? " AND project_id IN (SELECT id FROM project WHERE build_org_id = ?)" : "");
        String tokensThisMonthSql = """
            SELECT COALESCE(SUM(total_tokens), 0) FROM llm_call_log
            WHERE created_at >= DATE_FORMAT(CURDATE(), '%Y-%m-01')
            """;
        String complianceRateSql = """
            SELECT 
                COALESCE(SUM(CASE WHEN overall_verdict = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 100.0)
            FROM review_result
            WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
            """ + (orgId != null ? " AND project_id IN (SELECT id FROM project WHERE build_org_id = ?)" : "");
        String avgReviewTimeSql = """
            SELECT COALESCE(AVG(duration_ms) / 1000.0, 0) FROM review_task
            WHERE status = 3 AND duration_ms IS NOT NULL
            """ + (orgId != null ? " AND project_id IN (SELECT id FROM project WHERE build_org_id = ?)" : "");
        DashboardOverviewResponse resp = new DashboardOverviewResponse();
        
        List<Object> orgArgs = orgId != null ? List.of(orgId, orgId, orgId) : List.of();
        List<Object> singleOrgArg = orgId != null ? List.of(orgId) : List.of();
        
        resp.setActiveProjects(queryForInt(activeProjectsSql, orgArgs));
        resp.setPendingTasks(queryForInt(pendingTasksSql, singleOrgArg));
        resp.setCompletedTasks(queryForInt(completedTasksSql, singleOrgArg));
        resp.setHighRiskIssues(queryForInt(highRiskIssuesSql, singleOrgArg));
        resp.setPendingChanges(queryForInt(pendingChangesSql, singleOrgArg));
        resp.setTokensThisMonth(queryForLong(tokensThisMonthSql, List.of()));
        resp.setComplianceRate(queryForDouble(complianceRateSql, singleOrgArg));
        resp.setAvgReviewTime(queryForDouble(avgReviewTimeSql, singleOrgArg));
        
        return resp;
    }

    public List<DimensionStatItem> stats(DimensionStatsQuery request) {
        StringBuilder sql = new StringBuilder("""
                select
                    rir.check_dimension, rir.dimension_name,
                    count(*) as total,
                    coalesce(sum(case when rir.verdict = 1 then 1 else 0 end), 0) as compliant,
                    coalesce(sum(case when rir.verdict != 1 then 1 else 0 end), 0) as problematic,
                    coalesce(avg(rir.confidence),0) as avg_confidence
                    from review_item_result rir
                    join review_result rr on rir.result_id = rr.id
                    join review_task rt on rir.task_id = rt.id
                where 1=1
                """);
        List<Object> args = new ArrayList<>();
        appendFilter(sql,args,request);
        sql.append(" group by rir.check_dimension, rir.dimension_name");
        sql.append(" order by rir.check_dimension");
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            DimensionStatItem d = new DimensionStatItem();
            d.setCheckDimension(rs.getInt("check_dimension"));
            d.setDimensionName(rs.getString("dimension_name"));
            d.setTotal(rs.getInt("total"));
            d.setCompliant(rs.getInt("compliant"));
            d.setProblematic(rs.getInt("problematic"));
            d.setComplianceRate(d.getTotal() > 0 ? (double)(d.getCompliant() / d.getTotal()) : 0.0);
            d.setAvgConfidence(rs.getDouble("avg_confidence"));
            return d;
        },args.toArray());
    }

    public List<TrendDataPoint> trend(IssueTrendQuery request) {
        StringBuilder sql = new StringBuilder("""
                select 
                    date(created_at) as date,
                    count(*) as total,
                    coalesce(sum(case when status = 3 then 1 else 0 end), 0) as resolved,
                    coalesce(sum(case when status = 1 then 1 else 0 end), 0) as pending
                    from review_issue
                where created_at >= date_sub(curdate(), interval ? day)
                """);
        List<Object> args = new ArrayList<>();
        args.add(request.getDays() != null ? request.getDays() : 30);
        appendFilter(sql, args, request);
        sql.append(" group by date(created_at)");
        sql.append(" order by date asc");
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            TrendDataPoint d = new TrendDataPoint();
            d.setDate(rs.getDate("date").toLocalDate().format(DATE_FMT));
            d.setTotal(rs.getInt("total"));
            d.setResolved(rs.getInt("resolved"));
            d.setPending(rs.getInt("pending"));
            return d;
        }, args.toArray());
    }

    private void appendFilter(StringBuilder sql, List<Object> args, DimensionStatsQuery request) {
        if(request.getProjectId() != null) {
            sql.append(" and rr.project_id = ?");
            args.add(request.getProjectId());
        }
        if(request.getTaskType() != null) {
            sql.append(" and rt.task_type = ?");
            args.add(request.getTaskType());
        }
        if(request.getDateFrom() != null) {
            sql.append(" and rir.created_at >= ?");
            args.add(request.getDateFrom());
        }
        if(request.getDateTo() != null) {
            sql.append(" and rir.created_at <= ?");
            args.add(request.getDateTo());
        }
    }

    private void appendFilter(StringBuilder sql, List<Object> args, IssueTrendQuery request) {
        if(request.getProjectId() != null) {
            sql.append(" and project_id = ?");
            args.add(request.getProjectId());
        }
    }

    private Integer queryForInt(String sql, List<Object> args) {
        return jdbcTemplate.queryForObject(sql, Integer.class, args.toArray());
    }

    private Long queryForLong(String sql, List<Object> args) {
        Long result = jdbcTemplate.queryForObject(sql, Long.class, args.toArray());
        return result != null ? result : 0L;
    }

    private Double queryForDouble(String sql, List<Object> args) {
        Double result = jdbcTemplate.queryForObject(sql, Double.class, args.toArray());
        return result != null ? result : 0.0;
    }



}
