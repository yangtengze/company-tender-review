package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Size;

public class ClauseQueryRequest {
    @Size(max = 60)
    private String clauseNo;
    @Size(max = 100)
    private String keyword;
    public String getClauseNo() {
        return clauseNo;
    }
    public String getKeyword() {
        return keyword;
    }
    public void setClauseNo(String clauseNo) {
        this.clauseNo = clauseNo;
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
