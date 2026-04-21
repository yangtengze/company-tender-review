package cn.edu.sdua._db.ytz.company_tender_review.repository;

import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ChunkQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.DocumentQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.DocumentUploadRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.DocumentChunkNode;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.DocumentDetailResponse;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.DocumentChunkCreateRequest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;


@Repository
public class DocumentRepository {
    private static final DateTimeFormatter ISO_LOCAL_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter ISO_LOCAL_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final RowMapper<DocumentDetailResponse> MAPPER = (rs, rowNum) -> {
        DocumentDetailResponse d = new DocumentDetailResponse();
        d.setId(rs.getLong("id"));
        d.setProjectId(rs.getLong("project_id"));
        d.setDocType(rs.getObject("doc_type", Integer.class));
        d.setDocTypeName(docTypeName(d.getDocType()));
        d.setDocName(rs.getString("doc_name"));
        d.setFilePath(rs.getString("file_path"));
        d.setFileSize(rs.getObject("file_size", Long.class));
        d.setFileExt(rs.getString("file_ext"));
        d.setMd5(rs.getString("md5"));
        d.setVersion(rs.getString("version"));
        d.setParseStatus(rs.getObject("parse_status", Integer.class));
        Date issueDate = rs.getDate("issue_date");
        d.setIssueDate(issueDate == null ? null : issueDate.toLocalDate().format(ISO_LOCAL_DATE));
        d.setIssuer(rs.getString("issuer"));
        Object uploaderId = rs.getObject("uploader_id");
        d.setUploaderId(uploaderId == null ? null : rs.getLong("uploader_id"));
        d.setUploaderName(rs.getString("uploader_name"));
        d.setCreatedAt(toIso(rs.getTimestamp("created_at")));
        return d;
    };

    public DocumentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long insertUpload(Long uploaderId, DocumentUploadRequest request, MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file required");
        }

        String originalNameValue = file.getOriginalFilename();
        if (originalNameValue == null) {
            originalNameValue = "upload";
        }
        String extValue = extName(originalNameValue);
        String versionValue = request.getVersion();
        if (versionValue == null || versionValue.isBlank()) {
            versionValue = "1.0";
        }

        byte[] bytes = file.getBytes();
        String md5Value = md5Hex(bytes);
        String storagePathValue = "uploads/" + UUID.randomUUID() + "/" + originalNameValue;

        final String originalName = originalNameValue;
        final String ext = extValue;
        final String version = versionValue;
        final String md5 = md5Value;
        final String storagePath = storagePathValue;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    insert into document
                    (project_id, doc_type, doc_name, file_path, file_size, file_ext, md5, version,
                     issue_date, issuer, parse_status, parse_text, uploader_id, remark, created_at, updated_at)
                    values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, null, ?, ?, now(), now())
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, request.getProjectId());
            ps.setObject(2, request.getDocType());
            ps.setString(3, originalName);
            ps.setString(4, storagePath);
            ps.setLong(5, file.getSize());
            ps.setString(6, ext);
            ps.setString(7, md5);
            ps.setString(8, version);

            LocalDate issueDate = request.getIssueDate();
            ps.setDate(9, issueDate == null ? null : Date.valueOf(issueDate));
            ps.setString(10, request.getIssuer());
            ps.setObject(11, uploaderId);
            ps.setString(12, request.getRemark());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("failed to get insert id");
        }
        return key.longValue();
    }

    public DocumentDetailResponse findById(Long id) {
        List<DocumentDetailResponse> rows = jdbcTemplate.query("""
                select d.*, u.real_name as uploader_name
                  from document d
                  left join sys_user u on u.id = d.uploader_id
                 where d.id = ?
                """, MAPPER, id);
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("document not found");
        }
        return rows.get(0);
    }

    public long count(DocumentQueryRequest request) {
        StringBuilder sql = new StringBuilder("""
                select count(*)
                  from document d
                 where d.project_id = ?
                """);
        List<Object> args = new ArrayList<>();
        args.add(request.getProjectId());

        appendFilters(sql, args, request);
        return jdbcTemplate.queryForObject(sql.toString(), Long.class, args.toArray());
    }

    public List<DocumentDetailResponse> list(DocumentQueryRequest request) {
        int offset = Math.max(0, (request.getPage() - 1) * request.getSize());
        StringBuilder sql = new StringBuilder("""
                select d.*, u.real_name as uploader_name
                  from document d
                  left join sys_user u on u.id = d.uploader_id
                 where d.project_id = ?
                """);
        List<Object> args = new ArrayList<>();
        args.add(request.getProjectId());

        appendFilters(sql, args, request);
        sql.append(" order by d.id desc limit ? offset ?");
        args.add(request.getSize());
        args.add(offset);

        return jdbcTemplate.query(sql.toString(), MAPPER, args.toArray());
    }

    public Integer findDocTypeById(Long docId) {
        List<Integer> rows = jdbcTemplate.query("select doc_type from document where id = ?", (rs, i) -> rs.getInt(1), docId);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public DocumentMeta findDocMetaById(Long docId) {
        // 仅用于扩展表写入前的校验：doc_type 与 doc.project_id
        List<DocumentMeta> rows = jdbcTemplate.query("""
                select doc_type, project_id
                  from document
                 where id = ?
                """, (rs, rowNum) -> new DocumentMeta(
                rs.getObject("doc_type", Integer.class),
                rs.getLong("project_id")
        ), docId);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public record DocumentMeta(Integer docType, Long projectId) {}

    public void updateParseStatus(Long docId, int parseStatus) {
        jdbcTemplate.update("update document set parse_status = ?, updated_at = now() where id = ?",
                parseStatus, docId);
    }

    public List<DocumentChunkNode> chunks(Long docId, ChunkQueryRequest request) {
        StringBuilder sql = new StringBuilder("""
                select
                    id, parent_id,
                    chunk_type, chunk_level, chunk_index,
                    content, token_count, metadata_json as metadata
                    from document_chunk
                where doc_id = ?
                """);
        if (request.getTree()) {
            sql.append(" order by parent_id, chunk_index");
        } else {
            sql.append(" order by chunk_index");
        }
        List<DocumentChunkNode> rows = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            DocumentChunkNode d = new DocumentChunkNode();
            d.setId(rs.getLong("id"));
            d.setDocId(docId);
            d.setParentId(rs.getLong("parent_id"));
            d.setChunkType(rs.getString("chunk_type"));
            d.setChunkLevel(rs.getInt("chunk_level"));
            d.setChunkIndex(rs.getInt("chunk_index"));
            d.setContent(rs.getString("content"));
            d.setTokenCount(rs.getInt("token_count"));
            String metadataJson = rs.getString("metadata");
            if (metadataJson != null && !metadataJson.isEmpty()) {
                try {
                    Map<String, Object> metadata = objectMapper.readValue(metadataJson, Map.class);
                    d.setMetadata(metadata);
                } catch (Exception e) {
                    e.printStackTrace();
                    d.setMetadata(Collections.emptyMap());
                }
            } else {
                d.setMetadata(Collections.emptyMap());
            }
            d.setChildren(new ArrayList<>());
            return d;
        }, docId);
        if (request.getTree()) {
            return buildTree(rows);
        } else {
            rows.forEach(node -> node.setChildren(null));
            return rows;
        }
    }

    public void batchInsertChunks(List<DocumentChunkCreateRequest> requests) {
        String sql = "INSERT INTO document_chunk " +
                "(doc_id, parent_id, chunk_type, chunk_level, chunk_index, content, token_count, vector_id, metadata_json) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws java.sql.SQLException {
                DocumentChunkCreateRequest req = requests.get(i);
                ps.setLong(1, req.getDocId());
                if (req.getParentId() != null) {
                    ps.setLong(2, req.getParentId());
                } else {
                    ps.setNull(2, java.sql.Types.BIGINT);
                }
                ps.setString(3, req.getChunkType());
                if (req.getChunkLevel() != null) {
                    ps.setInt(4, req.getChunkLevel());
                } else {
                    ps.setInt(4, 0);
                }
                ps.setInt(5, req.getChunkIndex());
                ps.setString(6, req.getContent());
                if (req.getTokenCount() != null) {
                    ps.setInt(7, req.getTokenCount());
                } else {
                    ps.setNull(7, java.sql.Types.INTEGER);
                }
                ps.setString(8, req.getVectorId());
                try {
                    if (req.getMetadata() != null) {
                        ps.setString(9, objectMapper.writeValueAsString(req.getMetadata()));
                    } else {
                        ps.setNull(9, java.sql.Types.VARCHAR);
                    }
                } catch (Exception e) {
                    try {
                        ps.setNull(9, java.sql.Types.VARCHAR);
                    } catch (java.sql.SQLException ignored) {}
                }
            }

            @Override
            public int getBatchSize() {
                return requests.size();
            }
        });
    }
    
    private List<DocumentChunkNode> buildTree(List<DocumentChunkNode> rows) {

        Map<Long, List<DocumentChunkNode>> childrenMap = rows.stream()
                .filter(n -> n.getParentId() != null && n.getParentId() != 0)
                .collect(Collectors.groupingBy(DocumentChunkNode::getId));
        
        List<DocumentChunkNode> roots = new ArrayList<>();
        
        for (DocumentChunkNode row : rows) {
            // 挂载子节点
            List<DocumentChunkNode> children = childrenMap.getOrDefault(row.getId(), Collections.emptyList());
            row.setChildren(children);
            
            // 收集根节点
            if (row.getParentId() == null || row.getParentId() == 0) {
                roots.add(row);
            }
        }
        
        return roots;
    }
    
    private static void appendFilters(StringBuilder sql, List<Object> args, DocumentQueryRequest request) {
        if (request.getDocType() != null) {
            sql.append(" and d.doc_type = ?");
            args.add(request.getDocType());
        }
        if (request.getParseStatus() != null) {
            sql.append(" and d.parse_status = ?");
            args.add(request.getParseStatus());
        }
        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            sql.append(" and (d.doc_name like ? or d.file_path like ?)");
            String like = "%" + request.getKeyword().trim() + "%";
            args.add(like);
            args.add(like);
        }
    }

    private static String toIso(java.sql.Timestamp ts) {
        if (ts == null) return null;
        return ts.toLocalDateTime().format(ISO_LOCAL_DATE_TIME);
    }

    private static String extName(String fileName) {
        int idx = fileName.lastIndexOf('.');
        if (idx < 0 || idx == fileName.length() - 1) return "";
        return fileName.substring(idx + 1).toLowerCase();
    }

    private static String md5Hex(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] digestBytes = digest.digest(bytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : digestBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception ex) {
            // MD5 always exists in JRE; this is just defensive.
            throw new IllegalStateException("md5 calc failed", ex);
        }
    }

    private static String docTypeName(Integer type) {
        return switch (type == null ? 0 : type) {
            case 1 -> "招标公告";
            case 2 -> "投标文件";
            case 3 -> "评标报告";
            case 4 -> "中标通知书";
            case 5 -> "施工合同";
            case 6 -> "立项审批文件";
            case 7 -> "开工报告";
            case 8 -> "施工日志";
            case 9 -> "变更申请";
            case 10 -> "变更方案";
            case 11 -> "施工图纸";
            case 12 -> "工程量清单";
            case 13 -> "质量验收规范";
            case 14 -> "监理报告";
            case 15 -> "竣工验收报告";
            case 99 -> "其他";
            default -> "未知";
        };
    }

}

