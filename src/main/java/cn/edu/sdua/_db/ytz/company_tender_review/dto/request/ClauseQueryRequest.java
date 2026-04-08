package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Size;

public class ClauseQueryRequest {
    @Size(max = 60, message = "条款编号长度不能超过60字符")
    private String clauseNo;
    @Size(max = 100, message = "关键字长度不能超过100字符")
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
