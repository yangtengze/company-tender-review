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
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.edu.sdua._db.ytz.company_tender_review.dto.request.CaseCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.CaseQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.CaseDetailResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.CaseListItem;

@Repository
public class CasesRepository {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private JdbcTemplate jdbcTemplate;
    private ObjectMapper objectMapper;
    public CasesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public Long count(CaseQueryRequest request) {
        StringBuilder sql = new StringBuilder("""
                select
                    count(*)
                    from case_library
                where 1=1
            """);
        List<Object> args = new ArrayList<>();
        appendFilter(sql,args,request);
        Long total = jdbcTemplate.queryForObject(sql.toString(), Long.class, args.toArray());
        return total == null ? 0 : total;
    }
    
    public List<CaseListItem> list(CaseQueryRequest request) {
        StringBuilder sql = new StringBuilder("""
                select
                    id, title, case_type, source, case_date,
                    project_type, issue_type, key_findings,
                    keywords, created_at
                    from case_library
                where 1=1
                """);
        List<Object> args = new ArrayList<>();
        appendFilter(sql, args, request);
        sql.append(" order by id asc limit ? offset ?");
        int page = request.getPage() == null ? 1 : request.getPage();
        int size = request.getSize() == null ? 20 : request.getSize();
        args.add(size);
        args.add(Math.max(0, (page - 1) * size));
        List<CaseListItem> rows = jdbcTemplate.query(sql.toString(), (rs,rowNum) -> {
            CaseListItem d = new CaseListItem();
            d.setId(rs.getLong("id"));
            d.setTitle(rs.getString("title"));
            d.setCaseType(rs.getInt("case_type"));
            d.setCaseTypeName(caseTypeName(d.getCaseType()));
            d.setSource(rs.getString("source"));
            LocalDate caseDate = rs.getDate("case_date") == null ? null : rs.getDate("case_date").toLocalDate();
            d.setCaseDate(caseDate.format(DATE_FMT));
            d.setProjectType(rs.getInt("project_type"));
            d.setIssueType(rs.getInt("issue_type"));
            d.setKeyFindings(rs.getString("key_findings"));
            d.setKeywords(parseJsonList(rs.getString("keywords"), String.class));
            d.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime().format(DT_FMT));
            return d;
        },args.toArray());
        return rows;
    }
    public Long insert(CaseCreateRequest request) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                insert into case_library
                    (title, case_type, source, case_date, project_type, issue_type, description,
                    key_findings, outcome, lesson, keywords, ref_law_ids, created_at, updated_at)
                values
                    (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now());
            """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, request.getTitle());
            ps.setInt(2, request.getCaseType());
            ps.setString(3, request.getSource());
            LocalDate caseDate = request.getCaseDate();
            ps.setDate(4, caseDate == null ? Date.valueOf(LocalDate.now()) : Date.valueOf(caseDate));
            ps.setInt(5, request.getProjectType());
            ps.setInt(6, request.getIssueType());
            ps.setString(7, request.getDescription());
            ps.setString(8, request.getKeyFindings());
            ps.setString(9, request.getOutcome());
            ps.setString(10, request.getLesson());
            try {
                String keywordsJson = objectMapper.writeValueAsString(request.getKeywords());
                ps.setObject(11, keywordsJson);
                String refLawIdsJson = objectMapper.writeValueAsString(request.getRefLawIds());
                ps.setObject(12, refLawIdsJson);
            } catch (JsonProcessingException  e) {
                throw new DataIntegrityViolationException("Object 转 JSON 失败", e);
            }
            return ps;
        },keyHolder);
        Number key = Objects.requireNonNull(keyHolder.getKey(), "generated key required");
        return key.longValue();
    }

    public CaseDetailResponse findDetail(Long id) {
        List<CaseDetailResponse> rows = jdbcTemplate.query("""
            select 
                id, case_no, title, case_type, source, case_date, project_type,
                issue_type, description, key_findings, outcome, lesson,
                keywords, ref_law_ids, created_at, updated_at
                from case_library
            where id = ?
            """, (rs, rowNum) -> {
                CaseDetailResponse d = new CaseDetailResponse();
                d.setId(rs.getLong("id"));
                d.setCaseNo(rs.getString("case_no"));
                d.setTitle(rs.getString("title"));
                d.setCaseType(rs.getInt("case_type"));
                d.setCaseTypeName(caseTypeName(d.getCaseType()));
                d.setSource(rs.getString("source"));
                d.setCaseDate(rs.getDate("case_date").toLocalDate().format(DATE_FMT));
                d.setProjectType(rs.getInt("project_type"));
                d.setIssueType(rs.getInt("issue_type"));
                d.setDescription(rs.getString("description"));
                d.setKeyFindings(rs.getString("key_findings"));
                d.setOutcome(rs.getString("outcome"));
                d.setLesson(rs.getString("lesson"));
                d.setKeywords(parseJsonList(rs.getString("keywords"), String.class));
                d.setRefLawIds(parseJsonList(rs.getString("ref_law_ids"), Long.class));
                d.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime().format(DT_FMT));
                d.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime().format(DT_FMT));
                return d;
            }, id);
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("cases detail not found");
        }
        return rows.get(0);
    }


    private void appendFilter(StringBuilder sql, List<Object> args, CaseQueryRequest request) {
        if (request.getCaseType() != null) {
            sql.append(" and case_type = ?");
            args.add(request.getCaseType());
        }
        if (request.getIssueType() != null) {
            sql.append(" and issue_type = ?");
            args.add(request.getIssueType());
        }
        if (request.getProjectType() != null) {
            sql.append(" and project_type = ?");
            args.add(request.getProjectType());
        }
        if (request.getKeyword() != null) {
            sql.append(" and json_search(keywords, 'one', ?) is not null");
            args.add(request.getKeyword());
        }
    }

    private <T> List<T> parseJsonList(String json, Class<T> elementType) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            JavaType listType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, elementType);
            return objectMapper.readValue(json, listType);
        } catch (Exception e) {
            return List.of();
        }
    }
    private static String caseTypeName(Integer v) {
        return switch (v == null ? 0 : v) {
            case 1 -> "招投标违规";
            case 2 -> "合同纠纷";
            case 3 -> "施工变更纠纷";
            case 4 -> "其他";
            default -> "未知";
        };
    }
}
