package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

import java.util.List;

public class LawClause {
    private Long id;
    private Long lawId;
    private String clauseNo;
    private String title;
    private String content;
    private List<String> keywords;

    public String getClauseNo() {
        return clauseNo;
    }
    public String getContent() {
        return content;
    }
    public Long getId() {
        return id;
    }
    public List<String> getKeywords() {
        return keywords;
    }
    public Long getLawId() {
        return lawId;
    }
    public String getTitle() {
        return title;
    }
    public void setClauseNo(String clauseNo) {
        this.clauseNo = clauseNo;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
    public void setLawId(Long lawId) {
        this.lawId = lawId;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
