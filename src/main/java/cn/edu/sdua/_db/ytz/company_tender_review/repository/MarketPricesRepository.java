package cn.edu.sdua._db.ytz.company_tender_review.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cn.edu.sdua._db.ytz.company_tender_review.dto.request.MarketPriceCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.PriceCompareRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.request.PriceQueryRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.BatchImportResult;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.MarketPriceItem;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.PriceTrendItem;

@Repository
public class MarketPricesRepository {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final JdbcTemplate jdbcTemplate;

    public MarketPricesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public Long count(PriceQueryRequest request) {
        StringBuilder sql = new StringBuilder("""
                select 
                    count(*)
                    from market_price
                where 1=1
                """);
        List<Object> args = new ArrayList<>();
        appendFilter(sql,args,request);
        return jdbcTemplate.queryForObject(sql.toString(), Long.class, args.toArray());
    }

    public List<MarketPriceItem> list(PriceQueryRequest request) {
        StringBuilder sql = new StringBuilder("""
                select 
                    id, item_code, item_name, unit, category,
                    price, price_date, region, source
                    from market_price
                where 1=1
                """);
        List<Object> args = new ArrayList<>();
        appendFilter(sql, args, request);
        int page = request.getPage() == null ? 1 : request.getPage();
        int size = request.getSize() == null ? 20 : request.getSize();
        sql.append(" order by id asc limit ? offset ?");
        args.add(size);
        args.add(Math.max(0, (page - 1) * size));
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            MarketPriceItem d = new MarketPriceItem();
            d.setId(rs.getLong("id"));
            d.setItemCode(rs.getString("item_code"));
            d.setItemName(rs.getString("item_name"));
            d.setUnit(rs.getString("unit"));
            d.setCategory(rs.getInt("category"));
            d.setCategoryName(categoryName(d.getCategory()));
            d.setPrice(rs.getBigDecimal("price"));
            LocalDate priceDate = rs.getDate("price_date") == null ? LocalDate.now() : rs.getDate("price_date").toLocalDate();
            d.setPriceDate(priceDate.format(DATE_FMT));
            d.setRegion(rs.getString("region"));
            d.setSource(rs.getString("source"));
            return d;
        },args.toArray());
    }

    public BatchImportResult insert(List<MarketPriceCreateRequest> requests) {
        if (requests.size() > 1000) {
            throw new IllegalArgumentException("批量导入最多 1000 条，当前 " + requests.size() + " 条");
        }
        int total = requests.size();
        int success = 0;
        int updated = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();
        
        String sql = """
            INSERT INTO market_price
                (item_code, item_name, unit, category, price,
                price_date, region, source, created_at, updated_at)
            VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
            ON DUPLICATE KEY UPDATE
                item_name = VALUES(item_name),
                unit = VALUES(unit),
                category = VALUES(category),
                price = VALUES(price),
                region = VALUES(region),
                source = VALUES(source),
                updated_at = NOW()
            """;

        int batchSize = 500;
        for (int i = 0; i < requests.size(); i += batchSize) {
            List<MarketPriceCreateRequest> batch = requests.subList(i, Math.min(i + batchSize, requests.size()));
            int batchStartRow = i + 1;
            
            try {
                int[] affectedArray = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int idx) throws SQLException {
                        MarketPriceCreateRequest r = batch.get(idx);
                        ps.setString(1, r.getItemCode());
                        ps.setString(2, r.getItemName());
                        ps.setString(3, r.getUnit());
                        ps.setInt(4, r.getCategory());
                        ps.setBigDecimal(5, r.getPrice());
                        ps.setDate(6, Date.valueOf(r.getPriceDate()));
                        ps.setString(7, r.getRegion() == null ? "" : r.getRegion());
                        ps.setString(8, r.getSource());
                    }
                    
                    @Override
                    public int getBatchSize() {
                        return batch.size();
                    }
                });
                
                for (int affected : affectedArray) {
                    if (affected == 1) success++;
                    else if (affected == 2) updated++;
                }
                
            } catch (DataAccessException e) {
                for (int j = 0; j < batch.size(); j++) {
                    MarketPriceCreateRequest r = batch.get(j);
                    int rowNum = batchStartRow + j;
                    
                    try {
                        int affected = jdbcTemplate.update(sql,
                            r.getItemCode(),
                            r.getItemName(),
                            r.getUnit(),
                            r.getCategory(),
                            r.getPrice(),
                            Date.valueOf(r.getPriceDate()),
                            r.getRegion() == null ? "" : r.getRegion(),
                            r.getSource()
                        );
                        
                        if (affected == 1) success++;
                        else if (affected == 2) updated++;
                        
                    } catch (DataAccessException ex) {
                        failed++;
                        errors.add("第" + rowNum + "行: " + ex.getMostSpecificCause().getMessage());
                    }
                }
            }
        }
        
        BatchImportResult result = new BatchImportResult();
        result.setTotal(total);
        result.setSuccess(success);
        result.setUpdated(updated);
        result.setFailed(failed);
        result.setErrors(errors);
        return result;
    }
    public List<PriceTrendItem> compare(PriceCompareRequest request) {
        StringBuilder sql = new StringBuilder("""
            select 
                price_date, price, region, source
                from market_price
            where 1=1
            """);
        List<Object> args = new ArrayList<>();
        appendFilter(sql, args, request);
        sql.append(" order by created_at desc");
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            PriceTrendItem d = new PriceTrendItem();
            d.setPriceDate(rs.getDate("price_date").toLocalDate().format(DATE_FMT));
            d.setPrice(rs.getBigDecimal("price"));
            d.setRegion(rs.getString("region"));
            d.setSource(rs.getString("source"));
            return d;
        },args.toArray());
    }

    private void appendFilter(StringBuilder sql, List<Object> args, PriceQueryRequest request) {
        if(request.getItemCode() != null) {
            sql.append(" and item_code like ?");
            args.add(request.getItemCode() + "%");
        }
        if(request.getKeyword() != null) {
            sql.append(" and item_name like ?");
            args.add("%" + request.getKeyword() + "%");
        }
        if(request.getCategory() != null) {
            sql.append(" and category = ?");
            args.add(request.getCategory());
        }
        if(request.getRegion() != null) {
            sql.append(" and region = ?");
            args.add(request.getRegion());
        }
        if (request.getPriceDateFrom() != null) {
            sql.append(" and price_date_from >= ?");
            args.add(request.getPriceDateFrom());
        }
        if (request.getPriceDateTo() != null) {
            sql.append(" and price_date_to <= ?");
            args.add(request.getPriceDateTo());
        }
    }
    private void appendFilter(StringBuilder sql, List<Object> args, PriceCompareRequest request) {
        if(request.getItemCode() != null) {
            sql.append(" and item_code = ?");
            args.add(request.getItemCode());
        }
        if(request.getRegion() != null) {
            sql.append(" and region like ?");
            args.add("%" + request.getRegion() + "%");
        }
        if(request.getMonths() != null) {
            sql.append(" and price_date >= date_sub(curdate(), interval ? month)");
            args.add(request.getMonths());
        }
    }
    private String categoryName(Integer v) {
        return switch(v == null ? 0 : v) {
            case 1 -> "人工";
            case 2 -> "材料";
            case 3 -> "机械";
            case 4 -> "综合单价";
            default -> "未知";
        };
    }
}
