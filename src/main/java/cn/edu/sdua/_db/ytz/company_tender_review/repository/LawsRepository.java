package cn.edu.sdua._db.ytz.company_tender_review.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ClauseQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.LawCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.LawQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.LawClause;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.LawDetailResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.LawListItem;

@Repository
public class LawsRepository {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public LawsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public Long count(LawQueryRequest request) {
        StringBuilder sql = new StringBuilder("""
            select 
                count(*) 
                from law_regulation
            where 1=1
        """);
        List<Object> args = new ArrayList<>();
        appendFilters(sql, args, request);
        Long c = jdbcTemplate.queryForObject(sql.toString(), Long.class, args.toArray());
        return c == null ? 0 : c;
    }
    public List<LawListItem> list(LawQueryRequest request) {
        StringBuilder sql = new StringBuilder("""
                select 
                    id, title, short_name, law_no,
                    category, issuer, issue_date, effective_date,
                    expire_date, status, keywords, summary
                    from law_regulation
                where 1 = 1
            """);
        List<Object> args = new ArrayList<>();
        appendFilters(sql, args, request);
        sql.append(" order by id limit ? offset ?");
        int page = request.getPage() == null ? 1 : request.getPage();
        int size = request.getSize() == null ? 20 : request.getSize();
        args.add(size);
        args.add(Math.max(0, (page - 1) * size));
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            LawListItem d = new LawListItem();
            d.setId(rs.getLong("id"));
            d.setTitle(rs.getString("title"));
            d.setShortName(rs.getString("short_name"));
            d.setLawNo(rs.getString("law_no"));
            d.setCategory((Integer) rs.getObject("category"));
            d.setCategoryName(categoryName(d.getCategory()));
            d.setIssuer(rs.getString("issuer"));
            d.setIssueDate(rs.getString("issue_date"));
            d.setEffectiveDate(rs.getString("effective_date"));
            d.setExpireDate(rs.getString("expire_date"));
            d.setStatus((Integer) rs.getObject("status"));
            String keywordsJson = rs.getString("keywords");
            d.setKeywords(parseJsonList(keywordsJson));
            d.setSummary(rs.getString("summary"));
            return d;
        }, args.toArray());
    }
    public Long insert(LawCreateRequest request) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                insert into law_regulation
                (title, short_name, law_no, category, issuer, issue_date,
                effective_date, expire_date, full_text, summary, keywords,
                status, created_at, updated_at)
                values
                (?, ?, ?, ?, ?, ?, ?, null, ?, ?, ?, 1, now(), now());
            """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, request.getTitle());
            ps.setString(2, request.getShortName());
            ps.setString(3, request.getLawNo());
            ps.setInt(4, request.getCategory());
            ps.setString(5, request.getIssuer());
            ps.setDate(6,toSqlDate(request.getIssueDate()));
            ps.setDate(7, toSqlDate(request.getEffectiveDate()));
            ps.setString(8, request.getFullText());
            ps.setString(9, request.getSummary());
            try {
                String keywordsJson = objectMapper.writeValueAsString(request.getKeywords());
                ps.setObject(10, keywordsJson);
            } catch (JsonProcessingException  e) {
                throw new DataIntegrityViolationException("keywords 转 JSON 失败", e);
            }
            return ps;
        },keyHolder);
        Number key = Objects.requireNonNull(keyHolder.getKey(), "generated key required");
        return  key.longValue();
    }

    public LawDetailResponse findDetail(Long id) {
        List<LawDetailResponse> rows = jdbcTemplate.query("""
            select 
                id, title, short_name, law_no, category,
                issuer, issue_date, effective_date, expire_date, status,
                full_text, summary, keywords, created_at, updated_at
            from law_regulation
            where id = ?
            """, (rs, rowNum) -> {
                LawDetailResponse d = new LawDetailResponse();
                d.setId(rs.getLong("id"));
                d.setTitle(rs.getString("title"));
                d.setShortNmae(rs.getString("short_name"));
                d.setLawNo(rs.getString("law_no"));
                d.setCategory((Integer) rs.getObject("category"));
                d.setCategoryName(categoryName(d.getCategory()));
                d.setIssuer(rs.getString("issuer"));
                Date issueDate = rs.getDate("issue_date");
                d.setIssueDate(issueDate == null ? null :issueDate.toLocalDate().format(DATE_FMT));
                Date effectiveDate = rs.getDate("effective_date");
                d.setEffectiveDate(effectiveDate == null ? null : effectiveDate.toLocalDate().format(DATE_FMT));
                Date expireDate = rs.getDate("expire_date");
                d.setExpireDate(expireDate == null ? null : expireDate.toLocalDate().format(DATE_FMT));
                d.setStatus(rs.getInt("status"));
                d.setFullText(rs.getString("full_text"));
                d.setSummary(rs.getString("summary"));
                String jsonStr = rs.getString("keywords");
                d.setKeywords(parseJsonList(jsonStr));
                d.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime().format(DT_FMT));
                d.setUpdateAt(rs.getTimestamp("updated_at").toLocalDateTime().format(DT_FMT));
                return d;
        }, id);
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("laws detail not found");
        }
        return rows.get(0);
    }
    public List<LawClause> query(Long lawId, ClauseQueryRequest request) {
        StringBuilder sql = new StringBuilder("""
            select 
                id, law_id, clause_no, title, content, keywords
            from law_clause
            where 1 = 1
            """);
        List<Object> args = new ArrayList<>();
        if (request.getClauseNo() != null) {
            sql.append(" and clause_no like ?");
            args.add(request.getClauseNo() + "%");
        }
        if (request.getKeyword() != null) {
            sql.append(" and json_search(keywords, 'one', ?) is not null");
            args.add(request.getKeyword());
        }
        sql.append(" and law_id = ?");
        args.add(lawId);
        sql.append(" order by id asc");
        List<LawClause> rows = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
                LawClause d = new LawClause();
                d.setId(rs.getLong("id"));
                d.setLawId(rs.getLong("law_id"));
                d.setClauseNo(rs.getString("clause_no"));
                d.setTitle(rs.getString("title"));
                d.setContent(rs.getString("content"));
                d.setKeywords(parseJsonList(rs.getString("keywords")));
                return d;
            }, args.toArray());
        return rows;
    }
    private List<String> parseJsonList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
    private void appendFilters(StringBuilder sql, List<Object> args, LawQueryRequest request) {
        if(request.getCategory() != null) {
            sql.append(" and category = ?");
            args.add(request.getCategory());
        }
        if(request.getStatus() != null) {
            sql.append(" and status = ?");
            args.add(request.getStatus());
        }
        if(request.getKeyword() != null) {
            sql.append(" and json_search(keywords, 'one', ?) is not null");
            args.add(request.getKeyword());
        }
    }
    private static Date toSqlDate(LocalDate date) {
        if (date == null) {
            return Date.valueOf(LocalDate.now());
        }
        return Date.valueOf(date);
    }

    private static String categoryName(Integer v) {
        return switch (v == null ? 0 : v) {
            case 1 -> "法律";
            case 2 -> "行政法规";
            case 3 -> "部门规章";
            case 4 -> "地方性法规";
            case 5 -> "标准规范";
            default -> "未知";
        };
    }
}
