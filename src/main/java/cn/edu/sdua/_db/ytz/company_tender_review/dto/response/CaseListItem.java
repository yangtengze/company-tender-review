package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

import java.util.List;

public class CaseListItem {
    private Long id;
    private String title;
    private Integer caseType;
    private String caseTypeName;
    private String source;
    private String caseDate;
    private Integer projectType;
    private Integer issueType;
    private String keyFindings;
    private List<String> keywords;
    private String createdAt;
    public String getCaseDate() {
        return caseDate;
    }
    public Integer getCaseType() {
        return caseType;
    }
    public String getCaseTypeName() {
        return caseTypeName;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public Long getId() {
        return id;
    }
    public Integer getIssueType() {
        return issueType;
    }
    public String getKeyFindings() {
        return keyFindings;
    }
    public List<String> getKeywords() {
        return keywords;
    }
    public Integer getProjectType() {
        return projectType;
    }
    public String getSource() {
        return source;
    }
    public String getTitle() {
        return title;
    }
    public void setCaseDate(String caseDate) {
        this.caseDate = caseDate;
    }
    public void setCaseType(Integer caseType) {
        this.caseType = caseType;
    }
    public void setCaseTypeName(String caseTypeName) {
        this.caseTypeName = caseTypeName;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setIssueType(Integer issueType) {
        this.issueType = issueType;
    }
    public void setKeyFindings(String keyFindings) {
        this.keyFindings = keyFindings;
    }
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
    public void setProjectType(Integer projectType) {
        this.projectType = projectType;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
