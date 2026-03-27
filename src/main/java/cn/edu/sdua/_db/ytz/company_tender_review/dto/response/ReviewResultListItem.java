package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

public class ReviewResultListItem {
    private Long id;
    private Long projectId;
    private Long taskId;

    private Integer taskType;
    private String taskTypeName;

    private Integer overallVerdict;
    private String verdictName;

    private Integer riskLevel;
    private String riskLevelName;

    private Integer reviewStatus;
    private String reviewStatusName;

    private String reviewerName;
    private String reviewerNote;

    private String completedAt;
    private String createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getTaskTypeName() {
        return taskTypeName;
    }

    public void setTaskTypeName(String taskTypeName) {
        this.taskTypeName = taskTypeName;
    }

    public Integer getOverallVerdict() {
        return overallVerdict;
    }

    public void setOverallVerdict(Integer overallVerdict) {
        this.overallVerdict = overallVerdict;
    }

    public String getVerdictName() {
        return verdictName;
    }

    public void setVerdictName(String verdictName) {
        this.verdictName = verdictName;
    }

    public Integer getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(Integer riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRiskLevelName() {
        return riskLevelName;
    }

    public void setRiskLevelName(String riskLevelName) {
        this.riskLevelName = riskLevelName;
    }

    public Integer getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(Integer reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewStatusName() {
        return reviewStatusName;
    }

    public void setReviewStatusName(String reviewStatusName) {
        this.reviewStatusName = reviewStatusName;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewerNote() {
        return reviewerNote;
    }

    public void setReviewerNote(String reviewerNote) {
        this.reviewerNote = reviewerNote;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

