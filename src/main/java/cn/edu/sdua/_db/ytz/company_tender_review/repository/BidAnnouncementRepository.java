package cn.edu.sdua._db.ytz.company_tender_review.repository;

import cn.edu.sdua._db.ytz.company_tender_review.dto.request.BidAnnouncementCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.BidAnnouncementResponse;
import cn.edu.sdua._db.ytz.company_tender_review.repository.model.BidAnnouncementRow;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Repository
public class BidAnnouncementRepository {
    private static final DateTimeFormatter ISO_DT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final JdbcTemplate jdbcTemplate;

    public BidAnnouncementRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long insert(BidAnnouncementCreateRequest req) {
        Integer publicNoticeDays = computePublicNoticeDays(req.getPublishDate(), req.getDeadlineDate());
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    insert into doc_bid_announcement
                    (doc_id, project_id, bid_no, bid_type, publish_date, deadline_date, bid_open_date,
                     public_notice_days, platform_name, platform_url, is_public_platform,
                     qualification_req, performance_req, estimated_price, created_at, updated_at)
                    values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, null, ?, ?, ?, now(), now())
                    """, Statement.RETURN_GENERATED_KEYS);

            ps.setLong(1, req.getDocId());
            ps.setLong(2, req.getProjectId());
            ps.setString(3, req.getBidNo());
            if (req.getBidType() == null) ps.setNull(4, java.sql.Types.TINYINT);
            else ps.setInt(4, req.getBidType());

            setLocalDateTime(ps, 5, req.getPublishDate());
            setLocalDateTime(ps, 6, req.getDeadlineDate());
            setLocalDateTime(ps, 7, req.getBidOpenDate());

            if (publicNoticeDays == null) ps.setNull(8, java.sql.Types.INTEGER);
            else ps.setInt(8, publicNoticeDays);

            ps.setString(9, req.getPlatformName());
            ps.setString(10, req.getPlatformUrl());

            ps.setString(11, req.getQualificationReq());
            ps.setString(12, req.getPerformanceReq());

            BigDecimal price = req.getEstimatedPrice();
            if (price == null) ps.setNull(13, java.sql.Types.DECIMAL);
            else ps.setBigDecimal(13, price);
            // created_at/updated_at: now() are hard-coded in SQL
            return ps;
        }, keyHolder);

        Number key = Objects.requireNonNull(keyHolder.getKey(), "generated key required");
        return key.longValue();
    }

    public BidAnnouncementResponse findResponseById(long id) {
        List<BidAnnouncementRow> rows = jdbcTemplate.query("""
                select id, doc_id, project_id, bid_no, bid_type,
                       publish_date, deadline_date, bid_open_date, public_notice_days,
                       platform_name, platform_url, is_public_platform,
                       qualification_req, performance_req, estimated_price,
                       updated_at
                  from doc_bid_announcement
                 where id = ?
                """, (rs, rowNum) -> new BidAnnouncementRow(
                        rs.getLong("id"),
                        rs.getLong("doc_id"),
                        rs.getLong("project_id"),
                        rs.getString("bid_no"),
                        rs.getObject("bid_type", Integer.class),
                        rs.getTimestamp("publish_date") == null ? null : rs.getTimestamp("publish_date").toLocalDateTime(),
                        rs.getTimestamp("deadline_date") == null ? null : rs.getTimestamp("deadline_date").toLocalDateTime(),
                        rs.getTimestamp("bid_open_date") == null ? null : rs.getTimestamp("bid_open_date").toLocalDateTime(),
                        rs.getObject("public_notice_days", Integer.class),
                        rs.getString("platform_name"),
                        rs.getString("platform_url"),
                        rs.getObject("is_public_platform", Integer.class),
                        rs.getString("qualification_req"),
                        rs.getString("performance_req"),
                        rs.getBigDecimal("estimated_price"),
                        rs.getTimestamp("updated_at") == null ? null : rs.getTimestamp("updated_at").toLocalDateTime()
                ), id);
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("bid announcement not found");
        }
        BidAnnouncementRow row = rows.get(0);
        return toResponse(row);
    }

    public BidAnnouncementResponse findResponseByDocId(Long docId) {
        List<BidAnnouncementRow> rows = jdbcTemplate.query("""
                select id, doc_id, project_id, bid_no, bid_type,
                       publish_date, deadline_date, bid_open_date, public_notice_days,
                       platform_name, platform_url, is_public_platform,
                       qualification_req, performance_req, estimated_price,
                       updated_at
                  from doc_bid_announcement
                 where doc_id = ?
                """, (rs, rowNum) -> new BidAnnouncementRow(
                        rs.getLong("id"),
                        rs.getLong("doc_id"),
                        rs.getLong("project_id"),
                        rs.getString("bid_no"),
                        rs.getObject("bid_type", Integer.class),
                        rs.getTimestamp("publish_date") == null ? null : rs.getTimestamp("publish_date").toLocalDateTime(),
                        rs.getTimestamp("deadline_date") == null ? null : rs.getTimestamp("deadline_date").toLocalDateTime(),
                        rs.getTimestamp("bid_open_date") == null ? null : rs.getTimestamp("bid_open_date").toLocalDateTime(),
                        rs.getObject("public_notice_days", Integer.class),
                        rs.getString("platform_name"),
                        rs.getString("platform_url"),
                        rs.getObject("is_public_platform", Integer.class),
                        rs.getString("qualification_req"),
                        rs.getString("performance_req"),
                        rs.getBigDecimal("estimated_price"),
                        rs.getTimestamp("updated_at") == null ? null : rs.getTimestamp("updated_at").toLocalDateTime()
                ), docId);
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("bid announcement not found");
        }
        BidAnnouncementRow row = rows.get(0);
        return toResponse(row);
    }

    private BidAnnouncementResponse toResponse(BidAnnouncementRow row) {
        BidAnnouncementResponse resp = new BidAnnouncementResponse();
        resp.setId(row.id());
        resp.setDocId(row.docId());
        resp.setProjectId(row.projectId());
        resp.setBidNo(row.bidNo());
        resp.setBidType(row.bidType());
        resp.setBidTypeName(bidTypeName(row.bidType()));
        resp.setPublishDate(format(row.publishDate()));
        resp.setDeadlineDate(format(row.deadlineDate()));
        resp.setBidOpenDate(format(row.bidOpenDate()));
        resp.setPublicNoticeDays(row.publicNoticeDays());
        resp.setPlatformName(row.platformName());
        resp.setPlatformUrl(row.platformUrl());
        resp.setIsPublicPlatform(row.isPublicPlatform());
        resp.setQualificationReq(row.qualificationReq());
        resp.setPerformanceReq(row.performanceReq());
        resp.setEstimatedPrice(row.estimatedPrice());
        resp.setUpdatedAt(format(row.updatedAt()));
        return resp;
    }

    private static Integer computePublicNoticeDays(LocalDateTime publish, LocalDateTime deadline) {
        if (publish == null || deadline == null) return null;
        LocalDate p = publish.toLocalDate();
        LocalDate d = deadline.toLocalDate();
        return (int) ChronoUnit.DAYS.between(p, d);
    }

    private static void setLocalDateTime(PreparedStatement ps, int idx, LocalDateTime value) throws java.sql.SQLException {
        if (value == null) ps.setNull(idx, java.sql.Types.TIMESTAMP);
        else ps.setTimestamp(idx, java.sql.Timestamp.valueOf(value));
    }

    private static String format(LocalDateTime dt) {
        return dt == null ? null : dt.format(ISO_DT);
    }

    private static String bidTypeName(Integer bidType) {
        return switch (bidType == null ? 0 : bidType) {
            case 1 -> "公开";
            case 2 -> "邀请";
            case 3 -> "竞争性谈判";
            default -> "未知";
        };
    }
}

