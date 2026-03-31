package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

import java.util.List;

public class LawListItem {
    private Long id;
    private String title;
    private String shortName;
    private String lawNo;
    private Integer category;
    private String categoryName;
    private String issuer;
    private String issueDate;
    private String effectiveDate;
    private String expireDate;
    private Integer status;
    private List<String> keywords;
    private String summary;

    public Integer getCategory() {
        return category;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public String getEffectiveDate() {
        return effectiveDate;
    }
    public String getExpireDate() {
        return expireDate;
    }
    public Long getId() {
        return id;
    }
    public String getIssueDate() {
        return issueDate;
    }
    public String getIssuer() {
        return issuer;
    }
    public List<String> getKeywords() {
        return keywords;
    }
    public String getLawNo() {
        return lawNo;
    }
    public String getShortName() {
        return shortName;
    }
    public Integer getStatus() {
        return status;
    }
    public String getSummary() {
        return summary;
    }
    public String getTitle() {
        return title;
    }
    public void setCategory(Integer category) {
        this.category = category;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
    public void setLawNo(String lawNo) {
        this.lawNo = lawNo;
    }
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
