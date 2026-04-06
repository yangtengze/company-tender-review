package cn.edu.sdua._db.ytz.company_tender_review.repository;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.edu.sdua._db.ytz.company_tender_review.dto.request.PlatformQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.PlatformVerifyRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.PlatformItem;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.PlatformVerifyResult;

@Repository
public class PlatformsRepository {
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final JdbcTemplate jdbcTemplate;

    public PlatformsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PlatformItem> list(PlatformQueryRequest request) {
        StringBuilder sql = new StringBuilder("""
                select
                    id, name, url, level, region, is_approved,
                    remark, created_at, updated_at
                    from public_platform
                where 1=1
                """);
        List<Object> args = new ArrayList<>();
        appendFilter(sql,args,request);
        sql.append(" order by id asc");
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            PlatformItem d = new PlatformItem();
            d.setId(rs.getLong("id"));
            d.setName(rs.getString("name"));
            d.setUrl(rs.getString("url"));
            d.setLevel(rs.getInt("level"));
            d.setLevelName(levelName(d.getLevel()));
            d.setRegion(rs.getString("region"));
            d.setIsApproved(rs.getInt("is_approved"));
            d.setRemark(rs.getString("remark"));
            d.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime().format(DT_FMT));
            d.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime().format(DT_FMT));
            return d;
        },args.toArray());
    }

    public PlatformVerifyResult verify(PlatformVerifyRequest request) {
        StringBuilder sql = new StringBuilder("""
                select 
                    is_approved, id as platform_id, name as platform_name,
                    level, region, url, name,
                    case 
                        when url = ? then 1.0
                        when name = ? then 1.0
                        when url like ? then 0.8
                        when name like ? then 0.6
                        else 0.5
                    end as match_score
                    from public_platform
                where 1=1
                """);
        List<Object> args = new ArrayList<>();
        args.add(request.getUrl());
        args.add(request.getName());
        args.add("%" + request.getUrl() + "%");
        args.add("%" + request.getName() + "%");
        appendFilter(sql, args, request);
        List<PlatformVerifyResult> rows = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            PlatformVerifyResult d = new PlatformVerifyResult();
            d.setIsApproved(rs.getInt("is_approved") == 1 ? true : false);
            d.setPlatformId(rs.getLong("platform_id"));
            d.setPlatformName(rs.getString("platform_name"));
            d.setLevel(rs.getInt("level"));
            d.setLevelName(levelName(d.getLevel()));
            d.setRegion(rs.getString("region"));
            d.setMatchScore(rs.getDouble("match_score"));
            return d;
        },args.toArray());
        if(rows.isEmpty()) {
            throw new IllegalArgumentException("verify result not found");
        }
        return rows.get(0);
    }

    private void appendFilter(StringBuilder sql, List<Object> args, PlatformVerifyRequest request) {
        if(request.getUrl() != null) {
            sql.append(" and url like ?");
            args.add("%" + request.getUrl() + "%");
        }
        if(request.getName() != null) {
            sql.append(" and name like ?");
            args.add("%" + request.getName() + "%");
        }
    }

    private void appendFilter(StringBuilder sql, List<Object> args, PlatformQueryRequest request) {
        if(request.getLevel() != null) {
            sql.append(" and level = ?");
            args.add(request.getLevel());
        }
        if(request.getRegion() != null) {
            sql.append(" and region like ?");
            args.add("%" + request.getRegion() + "%");
        }
        if(request.getIsApproved() != null) {
            sql.append(" and is_approved = ?");
            args.add(request.getIsApproved());
        }
    }

    private String levelName(Integer v) {
        return switch(v == null ? 0 : v) {
            case 1 -> "国家";
            case 2 -> "省";
            case 3 -> "市";
            case 4 -> "区县";
            default -> "未知";
        };
    }
}
