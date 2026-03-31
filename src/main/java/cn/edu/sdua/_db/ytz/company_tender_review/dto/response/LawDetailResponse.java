package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

import java.util.List;

public class LawDetailResponse {
    private Long id;
    private String title;
    private String shortNmae;
    private String lawNo;
    private Integer category;
    private String categoryName;
    private String issuer;
    private String issueDate;
    private String effectiveDate;
    private String expireDate;
    private Integer status;
    private String fullText;
    private String summary;
    private List<String> keywords;
    private String createdAt;
    private String updateAt;
    public Integer getCategory() {
        return category;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public String getEffectiveDate() {
        return effectiveDate;
    }
    public String getExpireDate() {
        return expireDate;
    }
    public String getFullText() {
        return fullText;
    }
    public Long getId() {
        return id;
    }
    public String getLawNo() {
        return lawNo;
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
    public String getShortNmae() {
        return shortNmae;
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
    public String getUpdateAt() {
        return updateAt;
    }
    public void setCategory(Integer category) {
        this.category = category;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
    public void setFullText(String fullText) {
        this.fullText = fullText;
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
    public void setShortNmae(String shortNmae) {
        this.shortNmae = shortNmae;
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
    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }
}
