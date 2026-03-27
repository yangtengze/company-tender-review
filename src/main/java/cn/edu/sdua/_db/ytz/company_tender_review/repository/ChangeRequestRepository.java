package cn.edu.sdua._db.ytz.company_tender_review.repository;

import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ChangeDocBindRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ChangeRequestCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ChangeDocItem;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ChangeRequestDetailResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository
public class ChangeRequestRepository {
    private static final DateTimeFormatter ISO_LOCAL_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter ISO_LOCAL_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final JdbcTemplate jdbcTemplate;

    public ChangeRequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long insertChangeRequest(ChangeRequestCreateRequest req) {
        BigDecimal originalAmount = resolveOriginalAmount(req);
        if (originalAmount == null) {
            throw new IllegalArgumentException("originalAmount required");
        }

        BigDecimal changeRatio = computeChangeRatio(req.getChangeAmount(), originalAmount);

        LocalDate applyDate = req.getApplyDate() == null ? LocalDate.now() : req.getApplyDate();
        Long applyOrgId = req.getApplyOrgId();

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    insert into change_request
                    (project_id, change_no, change_type, change_reason, reason_desc, change_desc,
                     original_amount, change_amount, change_ratio,
                     apply_date, apply_org_id, status, creator_id, created_at, updated_at)
                    values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, null, now(), now())
                    """, Statement.RETURN_GENERATED_KEYS);

            ps.setObject(1, req.getProjectId());
            ps.setString(2, req.getChangeNo());
            ps.setObject(3, req.getChangeType());
            if (req.getChangeReason() == null) ps.setNull(4, java.sql.Types.TINYINT);
            else ps.setInt(4, req.getChangeReason());

            if (req.getReasonDesc() == null) ps.setNull(5, java.sql.Types.LONGVARCHAR);
            else ps.setString(5, req.getReasonDesc());

            ps.setString(6, req.getChangeDesc());
            ps.setBigDecimal(7, originalAmount);
            ps.setBigDecimal(8, req.getChangeAmount());
            ps.setBigDecimal(9, changeRatio);
            ps.setDate(10, java.sql.Date.valueOf(applyDate));

            if (applyOrgId == null) ps.setNull(11, java.sql.Types.BIGINT);
            else ps.setLong(11, applyOrgId);

            return ps;
        }, keyHolder);

        Number key = Objects.requireNonNull(keyHolder.getKey(), "generated key required");
        return key.longValue();
    }

    public ChangeRequestDetailResponse findDetailById(long id) {
        List<ChangeRequestDetailResponse> rows = jdbcTemplate.query("""
                select cr.id, cr.project_id, p.project_name,
                       cr.change_no, cr.change_type, cr.change_reason, cr.reason_desc, cr.change_desc,
                       cr.original_amount, cr.change_amount, cr.change_ratio,
                       cr.apply_date, cr.apply_org_id, ao.name as apply_org_name,
                       cr.status, cr.created_at
                  from change_request cr
                  join project p on p.id = cr.project_id
                  left join sys_org ao on ao.id = cr.apply_org_id
                 where cr.id = ?
                """, (rs, rowNum) -> toDetailRow(rs), id);

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("change request not found");
        }
        ChangeRequestDetailResponse resp = rows.get(0);
        resp.setDocs(findDocs(id));
        return resp;
    }

    public void bindDoc(long changeRequestId, ChangeDocBindRequest req) {
        boolean exists = jdbcTemplate.queryForObject("""
                select count(*) from change_request_doc where change_request_id = ? and doc_id = ?
                """, Integer.class, changeRequestId, req.getDocId()) > 0;
        if (exists) {
            throw new IllegalArgumentException("conflict");
        }

        // 校验 doc 在同一项目下
        Long crProjectId = jdbcTemplate.queryForObject("""
                select project_id from change_request where id = ?
                """, Long.class, changeRequestId);
        Long docProjectId = jdbcTemplate.queryForObject("""
                select project_id from document where id = ?
                """, Long.class, req.getDocId());
        if (!Objects.equals(crProjectId, docProjectId)) {
            throw new IllegalArgumentException("doc project mismatch");
        }

        jdbcTemplate.update("""
                insert into change_request_doc (change_request_id, doc_id, doc_role)
                values (?, ?, ?)
                """, changeRequestId, req.getDocId(), req.getDocRole());
    }

    private BigDecimal resolveOriginalAmount(ChangeRequestCreateRequest req) {
        if (req.getOriginalAmount() != null) {
            return req.getOriginalAmount();
        }
        return jdbcTemplate.queryForObject("""
                select contract_amount from project where id = ?
                """, BigDecimal.class, req.getProjectId());
    }

    private static BigDecimal computeChangeRatio(BigDecimal changeAmount, BigDecimal originalAmount) {
        if (originalAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        // (changeAmount / originalAmount) * 100
        return changeAmount.divide(originalAmount, 8, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(4, RoundingMode.HALF_UP);
    }

    private List<ChangeDocItem> findDocs(long changeRequestId) {
        List<ChangeDocItem> docs = jdbcTemplate.query("""
                select d.id as doc_id, d.doc_name, d.doc_type, crd.doc_role, d.file_ext, d.file_size
                  from change_request_doc crd
                  join document d on d.id = crd.doc_id
                 where crd.change_request_id = ?
                 order by d.id asc
                """, (rs, rowNum) -> {
            ChangeDocItem item = new ChangeDocItem();
            item.setDocId(rs.getLong("doc_id"));
            item.setDocName(rs.getString("doc_name"));
            item.setDocType((Integer) rs.getObject("doc_type"));
            item.setDocRole(rs.getInt("doc_role"));
            item.setDocRoleName(docRoleName(item.getDocRole()));
            item.setFileExt(rs.getString("file_ext"));
            item.setFileSize(rs.getObject("file_size", Long.class));
            return item;
        }, changeRequestId);
        return docs == null ? Collections.emptyList() : docs;
    }

    private static ChangeRequestDetailResponse toDetailRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        ChangeRequestDetailResponse resp = new ChangeRequestDetailResponse();
        resp.setId(rs.getLong("id"));
        resp.setProjectId(rs.getLong("project_id"));
        resp.setProjectName(rs.getString("project_name"));
        resp.setChangeNo(rs.getString("change_no"));
        resp.setChangeType((Integer) rs.getObject("change_type"));
        resp.setChangeTypeName(changeTypeName(resp.getChangeType()));
        resp.setChangeReason((Integer) rs.getObject("change_reason"));
        resp.setReasonDesc(rs.getString("reason_desc"));
        resp.setChangeDesc(rs.getString("change_desc"));
        resp.setOriginalAmount(rs.getBigDecimal("original_amount"));
        resp.setChangeAmount(rs.getBigDecimal("change_amount"));
        resp.setChangeRatio(rs.getBigDecimal("change_ratio"));
        LocalDate applyDate = rs.getDate("apply_date") == null ? null : rs.getDate("apply_date").toLocalDate();
        resp.setApplyDate(applyDate == null ? null : applyDate.format(ISO_LOCAL_DATE));
        Object applyOrgId = rs.getObject("apply_org_id");
        resp.setApplyOrgId(applyOrgId == null ? null : ((Number) applyOrgId).longValue());
        resp.setApplyOrgName(rs.getString("apply_org_name"));
        resp.setStatus(rs.getInt("status"));
        resp.setStatusName(statusName(resp.getStatus()));
        LocalDateTime createdAt = rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toLocalDateTime();
        resp.setCreatedAt(createdAt == null ? null : createdAt.format(ISO_LOCAL_DATE_TIME));
        return resp;
    }

    private static String changeTypeName(Integer changeType) {
        return switch (changeType == null ? 0 : changeType) {
            case 1 -> "设计";
            case 2 -> "工程量";
            case 3 -> "材料";
            case 4 -> "工期";
            case 5 -> "综合";
            default -> "未知";
        };
    }

    private static String docRoleName(Integer docRole) {
        return switch (docRole == null ? 0 : docRole) {
            case 1 -> "变更方案";
            case 2 -> "原设计图纸";
            case 3 -> "工程量清单";
            case 4 -> "佐证材料";
            default -> "未知";
        };
    }

    private static String statusName(Integer status) {
        return switch (status == null ? 0 : status) {
            case 1 -> "待审查";
            case 2 -> "审查中";
            case 3 -> "完成";
            case 4 -> "已撤回";
            default -> "未知";
        };
    }
}

