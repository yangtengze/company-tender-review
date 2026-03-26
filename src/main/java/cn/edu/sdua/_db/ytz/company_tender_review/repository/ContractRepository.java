package cn.edu.sdua._db.ytz.company_tender_review.repository;

import cn.edu.sdua._db.ytz.company_tender_review.dto.request.ContractCreateRequest;
import cn.edu.sdua._db.ytz.company_tender_review.dto.response.ContractResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Repository
public class ContractRepository {
    private static final DateTimeFormatter ISO_LOCAL_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter ISO_LOCAL_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final JdbcTemplate jdbcTemplate;

    public ContractRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long insert(ContractCreateRequest req) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    insert into doc_contract
                    (doc_id, project_id,
                     contract_no, contract_amount, sign_date,
                     party_a, party_b,
                     start_date, end_date,
                     warranty_period, payment_terms, penalty_terms,
                     created_at, updated_at)
                    values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now())
                    """, Statement.RETURN_GENERATED_KEYS);

            ps.setLong(1, req.getDocId());
            ps.setLong(2, req.getProjectId());

            if (req.getContractNo() == null) ps.setNull(3, java.sql.Types.VARCHAR);
            else ps.setString(3, req.getContractNo());

            if (req.getContractAmount() == null) ps.setNull(4, java.sql.Types.DECIMAL);
            else ps.setBigDecimal(4, req.getContractAmount());

            setLocalDate(ps, 5, req.getSignDate());

            if (req.getPartyA() == null) ps.setNull(6, java.sql.Types.VARCHAR);
            else ps.setString(6, req.getPartyA());

            if (req.getPartyB() == null) ps.setNull(7, java.sql.Types.VARCHAR);
            else ps.setString(7, req.getPartyB());

            setLocalDate(ps, 8, req.getStartDate());
            setLocalDate(ps, 9, req.getEndDate());

            if (req.getWarrantyPeriod() == null) ps.setNull(10, java.sql.Types.INTEGER);
            else ps.setInt(10, req.getWarrantyPeriod());

            if (req.getPaymentTerms() == null) ps.setNull(11, java.sql.Types.LONGVARCHAR);
            else ps.setString(11, req.getPaymentTerms());

            if (req.getPenaltyTerms() == null) ps.setNull(12, java.sql.Types.LONGVARCHAR);
            else ps.setString(12, req.getPenaltyTerms());

            return ps;
        }, keyHolder);

        Number key = Objects.requireNonNull(keyHolder.getKey(), "generated key required");
        return key.longValue();
    }

    public ContractResponse findResponseById(long id) {
        List<ContractResponse> rows = jdbcTemplate.query("""
                select id, doc_id, project_id,
                       contract_no, contract_amount, sign_date,
                       party_a, party_b,
                       start_date, end_date,
                       warranty_period, payment_terms, penalty_terms,
                       updated_at
                  from doc_contract
                 where id = ?
                """, (rs, rowNum) -> {
            ContractResponse r = new ContractResponse();
            r.setId(rs.getLong("id"));
            r.setDocId(rs.getLong("doc_id"));
            r.setProjectId(rs.getLong("project_id"));
            r.setContractNo(rs.getString("contract_no"));
            r.setContractAmount(rs.getBigDecimal("contract_amount"));
            Date sd = rs.getDate("sign_date");
            r.setSignDate(sd == null ? null : sd.toLocalDate().format(ISO_LOCAL_DATE));
            r.setPartyA(rs.getString("party_a"));
            r.setPartyB(rs.getString("party_b"));
            Date st = rs.getDate("start_date");
            r.setStartDate(st == null ? null : st.toLocalDate().format(ISO_LOCAL_DATE));
            Date ed = rs.getDate("end_date");
            r.setEndDate(ed == null ? null : ed.toLocalDate().format(ISO_LOCAL_DATE));
            r.setWarrantyPeriod((Integer) rs.getObject("warranty_period"));
            r.setPaymentTerms(rs.getString("payment_terms"));
            r.setPenaltyTerms(rs.getString("penalty_terms"));
            LocalDateTime ut = rs.getTimestamp("updated_at") == null ? null : rs.getTimestamp("updated_at").toLocalDateTime();
            r.setUpdatedAt(ut == null ? null : ut.format(ISO_LOCAL_DATE_TIME));
            return r;
        }, id);

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("contract not found");
        }
        return rows.get(0);
    }

    private static void setLocalDate(PreparedStatement ps, int idx, LocalDate date) throws java.sql.SQLException {
        if (date == null) ps.setNull(idx, java.sql.Types.DATE);
        else ps.setDate(idx, Date.valueOf(date));
    }
}

