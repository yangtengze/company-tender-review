package cn.edu.sdua._db.ytz.company_tender_review.repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.edu.sdua._db.ytz.company_tender_review.dto.request.LLMLogQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.LLMLogSummaryQuery;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.LLMLogItem;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.LLMLogSummary;

@Repository
public class LLMLogsRepository {
    private JdbcTemplate jdbcTemplate;
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public LLMLogsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long count(LLMLogQueryRequest request) {
        StringBuilder sql = new StringBuilder("""
                select 
                    count(*)
                    from llm_call_log
                where 1=1
                """);
        List<Object> args = new ArrayList<>();
        appendFilter(sql,args,request);
        return jdbcTemplate.queryForObject(sql.toString(), Long.class, args.toArray());
    }



    public List<LLMLogItem> list(LLMLogQueryRequest request) {
        StringBuilder sql = new StringBuilder("""
                select
                    id, task_id, call_type, model_name, model_version,
                    prompt_tokens, completion_tokens, total_tokens, latency_ms,
                    status, error_msg, created_at
                    from llm_call_log
                where 1=1
                """);
        List<Object> args = new ArrayList<>();
        appendFilter(sql, args, request);
        sql.append(" order by id asc limit ? offset ?");
        int size = request.getSize();
        int page = request.getPage();
        args.add(size);
        args.add(Math.max(0, (page - 1) * size));
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            LLMLogItem d = new LLMLogItem();
            d.setId(rs.getLong("id"));
            d.setTaskId(rs.getLong("task_id"));
            d.setCallType(rs.getString("call_type"));
            d.setModelName(rs.getString("model_name"));
            d.setModelVersion(rs.getString("model_version"));
            d.setPromptTokens(rs.getInt("prompt_tokens"));
            d.setCompletionTokens(rs.getInt("completion_tokens"));
            d.setTotalTokens(rs.getInt("total_tokens"));
            d.setLatencyMs(rs.getInt("latency_ms"));
            d.setStatus(rs.getInt("status"));
            d.setErrorMsg(rs.getString("error_msg"));
            d.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime().format(DT_FMT));
            return d;
        }, args.toArray());
    }

    public List<LLMLogSummary> summary(LLMLogSummaryQuery request) {
        String dateFrom = request.getDateFrom() == null ? LocalDateTime.now().minusDays(30).format(DT_FMT) : request.getDateFrom();
        String dateTo = request.getDateTo() == null ? LocalDateTime.now().format(DT_FMT) : request.getDateTo();

        StringBuilder sql = new StringBuilder("""
                select 
                    model_name,
                    count(*) as call_count,
                    sum(total_tokens) as tokens_total,
                    sum(prompt_tokens) as prompt_tokens,
                    sum(completion_tokens) as completion_tokens,
                    sum(case when status != 1 then 1 else 0 end) as failed_count,
                    avg(latency_ms) as avg_latency_ms,
                    sum(latency_ms) as sum_latency_ms
                from llm_call_log
                where created_at >= ? and created_at <= ?
                """);
        List<Object> args = new ArrayList<>();
        args.add(dateFrom);
        args.add(dateTo);
        sql.append(" group by model_name order by tokens_total desc");
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            LLMLogSummary d = new LLMLogSummary();
            d.setModelName(rs.getString("model_name"));
            d.setCallCount(rs.getInt("call_count"));
            d.setTokensTotal(rs.getLong("tokens_total"));
            d.setPromptTokens(rs.getLong("prompt_tokens"));
            d.setCompletionTokens(rs.getLong("completion_tokens"));
            d.setFailedCount(rs.getInt("failed_count"));
            d.setFailRate(d.getCallCount() > 0 ? (double) (d.getFailedCount() / d.getCallCount() * 100) : 0.0);
            d.setAvgLatencyMs(rs.getDouble("avg_latency_ms"));
            return d;
        }, args.toArray());
    }
    
    private void appendFilter(StringBuilder sql, List<Object> args, LLMLogQueryRequest request) {
        if(request.getTaskId() != null) {
            sql.append(" and task_id = ?");
            args.add(request.getTaskId());
        }
        if(request.getModelName() != null) {
            sql.append(" and model_name = ?");
            args.add(request.getModelName());
        }
        if(request.getStatus() != null) {
            sql.append(" and status = ?");
            args.add(request.getStatus());
        }
        if(request.getDateFrom() != null && request.getDateTo() != null) {
            LocalDateTime from = LocalDateTime.parse(request.getDateFrom(), DT_FMT);
            LocalDateTime to = LocalDateTime.parse(request.getDateTo(), DT_FMT);
            Long latencyMs = ChronoUnit.MILLIS.between(from, to);
            sql.append(" and latency_ms = ?");
            args.add(latencyMs);
        }
    }
}
