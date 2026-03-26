package cn.edu.sdua._db.ytz.company_tender_review.repository;

import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ExtractResultResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Repository
public class DocumentExtractCacheRepository {
    private static final DateTimeFormatter ISO_LOCAL_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public DocumentExtractCacheRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public Optional<ExtractResultResponse> findByDocId(Long docId) {
        return jdbcTemplate.query("""
                select doc_id, extract_type, model_name, result_json, created_at, updated_at
                  from doc_extract_cache
                 where doc_id = ?
                """, (rs, rowNum) -> toResponse(rs), docId)
                .stream()
                .findFirst();
    }

    private ExtractResultResponse toResponse(java.sql.ResultSet rs) throws java.sql.SQLException {
        ExtractResultResponse r = new ExtractResultResponse();
        r.setDocId(rs.getLong("doc_id"));
        r.setExtractType(rs.getString("extract_type"));
        r.setModelName(rs.getString("model_name"));
        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        r.setCreatedAt(created == null ? null : created.toLocalDateTime().format(ISO_LOCAL_DATE_TIME));
        r.setUpdatedAt(updated == null ? null : updated.toLocalDateTime().format(ISO_LOCAL_DATE_TIME));
        String jsonString = rs.getString("result_json");
        if (jsonString == null) {
            r.setResultJson(null);
        } else {
            try {
                r.setResultJson(objectMapper.readValue(jsonString, Object.class));
            } catch (Exception ex) {
                r.setResultJson(jsonString);
            }
        }
        return r;
    }

    public ExtractResultResponse upsert(Long docId, String extractType, String modelName, Object resultJson) {
        try {
            String jsonString = objectMapper.writeValueAsString(resultJson);
            jdbcTemplate.update("""
                    insert into doc_extract_cache (doc_id, extract_type, result_json, model_name)
                    values (?, ?, ?, ?)
                    on duplicate key update
                        extract_type = values(extract_type),
                        result_json = values(result_json),
                        model_name = values(model_name)
                    """, docId, extractType, jsonString, modelName);
        } catch (Exception ex) {
            throw new IllegalStateException("insert extract cache failed", ex);
        }
        return findByDocId(docId).orElseThrow(() -> new IllegalStateException("cache insert then read failed"));
    }
}

