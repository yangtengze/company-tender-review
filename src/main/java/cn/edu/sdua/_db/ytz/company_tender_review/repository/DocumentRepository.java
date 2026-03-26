package cn.edu.sdua._db.ytz.company_tender_review.repository;

import cn.edu.sdua._db.ytz.company_tender_review.dto.request.DocumentQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.DocumentUploadRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.DocumentDetailResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class DocumentRepository {
    private static final DateTimeFormatter ISO_LOCAL_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter ISO_LOCAL_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    private final JdbcTemplate jdbcTemplate;

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

    public void updateParseStatus(Long docId, int parseStatus) {
        jdbcTemplate.update("update document set parse_status = ?, updated_at = now() where id = ?",
                parseStatus, docId);
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

