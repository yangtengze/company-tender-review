package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class ReviewFullResultResponse {
    private Long taskId;
    private String taskName;
    private Long projectId;
    private String projectName;
    private Integer overallVerdict;
    private String verdictName;
    private Integer riskLevel;
    private String riskLevelName;
    private String summary;
    private String suggestion;
    private Integer issueCount;
    private String modelName;
    private Integer tokensUsed;
    private Integer reviewStatus;
    private String reviewStatusName;
    private String reviewerName;
    private String reviewerNote;
    private String reviewedAt;
    private List<ReviewItemResultDto> items;
    private List<ReviewIssueItemDto> issues;
    private String createdAt;

    public static class ReviewItemResultDto {
        private Long id;
        private Integer checkDimension;
        private String dimensionName;
        private Integer verdict;
        private String verdictName;
        private BigDecimal confidence;
        private String detail;
        private String evidence;
        private String issueDesc;
        private String suggestion;
        private List<LawRef> refLaws;
        private List<CaseRef> refCases;
        private String updatedAt;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Integer getCheckDimension() { return checkDimension; }
        public void setCheckDimension(Integer checkDimension) { this.checkDimension = checkDimension; }
        public String getDimensionName() { return dimensionName; }
        public void setDimensionName(String dimensionName) { this.dimensionName = dimensionName; }
        public Integer getVerdict() { return verdict; }
        public void setVerdict(Integer verdict) { this.verdict = verdict; }
        public String getVerdictName() { return verdictName; }
        public void setVerdictName(String verdictName) { this.verdictName = verdictName; }
        public BigDecimal getConfidence() { return confidence; }
        public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }
        public String getDetail() { return detail; }
        public void setDetail(String detail) { this.detail = detail; }
        public String getEvidence() { return evidence; }
        public void setEvidence(String evidence) { this.evidence = evidence; }
        public String getIssueDesc() { return issueDesc; }
        public void setIssueDesc(String issueDesc) { this.issueDesc = issueDesc; }
        public String getSuggestion() { return suggestion; }
        public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
        public List<LawRef> getRefLaws() { return refLaws; }
        public void setRefLaws(List<LawRef> refLaws) { this.refLaws = refLaws; }
        public List<CaseRef> getRefCases() { return refCases; }
        public void setRefCases(List<CaseRef> refCases) { this.refCases = refCases; }
        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class ReviewIssueItemDto {
        private Long id;
        private Integer issueType;
        private String typeName;
        private Integer severity;
        private String severityName;
        private String title;
        private String description;
        private String location;
        private String suggestion;
        private Integer status;
        private String statusName;
        private String updatedAt;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Integer getIssueType() { return issueType; }
        public void setIssueType(Integer issueType) { this.issueType = issueType; }
        public String getTypeName() { return typeName; }
        public void setTypeName(String typeName) { this.typeName = typeName; }
        public Integer getSeverity() { return severity; }
        public void setSeverity(Integer severity) { this.severity = severity; }
        public String getSeverityName() { return severityName; }
        public void setSeverityName(String severityName) { this.severityName = severityName; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getSuggestion() { return suggestion; }
        public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public String getStatusName() { return statusName; }
        public void setStatusName(String statusName) { this.statusName = statusName; }
        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class LawRef {
        private Long id;
        private String title;
        private String clauseNo;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getClauseNo() { return clauseNo; }
        public void setClauseNo(String clauseNo) { this.clauseNo = clauseNo; }
    }

    public static class CaseRef {
        private Long id;
        private String title;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public Integer getOverallVerdict() { return overallVerdict; }
    public void setOverallVerdict(Integer overallVerdict) { this.overallVerdict = overallVerdict; }
    public String getVerdictName() { return verdictName; }
    public void setVerdictName(String verdictName) { this.verdictName = verdictName; }
    public Integer getRiskLevel() { return riskLevel; }
    public void setRiskLevel(Integer riskLevel) { this.riskLevel = riskLevel; }
    public String getRiskLevelName() { return riskLevelName; }
    public void setRiskLevelName(String riskLevelName) { this.riskLevelName = riskLevelName; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
    public Integer getIssueCount() { return issueCount; }
    public void setIssueCount(Integer issueCount) { this.issueCount = issueCount; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public Integer getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(Integer tokensUsed) { this.tokensUsed = tokensUsed; }
    public Integer getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(Integer reviewStatus) { this.reviewStatus = reviewStatus; }
    public String getReviewStatusName() { return reviewStatusName; }
    public void setReviewStatusName(String reviewStatusName) { this.reviewStatusName = reviewStatusName; }
    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }
    public String getReviewerNote() { return reviewerNote; }
    public void setReviewerNote(String reviewerNote) { this.reviewerNote = reviewerNote; }
    public String getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(String reviewedAt) { this.reviewedAt = reviewedAt; }
    public List<ReviewItemResultDto> getItems() { return items; }
    public void setItems(List<ReviewItemResultDto> items) { this.items = items; }
    public List<ReviewIssueItemDto> getIssues() { return issues; }
    public void setIssues(List<ReviewIssueItemDto> issues) { this.issues = issues; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

