package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

import java.util.List;

public class CaseDetailResponse {
    private Long id;
    private String caseNo;
    private String title;
    private Integer caseType;
    private String caseTypeName;
    private String source;
    private String caseDate;
    private Integer projectType;
    private Integer issueType;
    private String description;
    private String keyFindings;
    private String outcome;
    private String lesson;
    private List<String> keywords;
    private List<Long> refLawIds;
    private String createdAt;
    private String updatedAt;
    public String getCaseDate() {
        return caseDate;
    }
    public String getCaseNo() {
        return caseNo;
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
    public String getDescription() {
        return description;
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
    public String getLesson() {
        return lesson;
    }
    public String getOutcome() {
        return outcome;
    }
    public Integer getProjectType() {
        return projectType;
    }
    public String getTitle() {
        return title;
    }
    public List<Long> getRefLawIds() {
        return refLawIds;
    }
    public String getSource() {
        return source;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setCaseDate(String caseDate) {
        this.caseDate = caseDate;
    }
    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
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
    public void setDescription(String description) {
        this.description = description;
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
    public void setLesson(String lesson) {
        this.lesson = lesson;
    }
    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
    public void setProjectType(Integer projectType) {
        this.projectType = projectType;
    }
    public void setRefLawIds(List<Long> refLawIds) {
        this.refLawIds = refLawIds;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
