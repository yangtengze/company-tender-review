package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

public class IssueDetailResponse {
    private Long id;
    private Long itemResultId;
    private Long resultId;
    private Long taskId;
    private String taskName;
    private Long projectId;
    private String projectName;
    private Integer checkDimension;
    private String dimensionName;
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
    private String handleNote;
    private Long handledBy;
    private String handledByName;
    private String handledAt;
    private String createdAt;
    private String updatedAt;
    public Integer getCheckDimension() {
        return checkDimension;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public String getDescription() {
        return description;
    }
    public String getDimensionName() {
        return dimensionName;
    }
    public String getHandleNote() {
        return handleNote;
    }
    public String getHandledAt() {
        return handledAt;
    }
    public Long getHandledBy() {
        return handledBy;
    }
    public String getHandledByName() {
        return handledByName;
    }
    public Long getId() {
        return id;
    }
    public Integer getIssueType() {
        return issueType;
    }
    public Long getItemResultId() {
        return itemResultId;
    }
    public String getLocation() {
        return location;
    }
    public Long getProjectId() {
        return projectId;
    }
    public String getProjectName() {
        return projectName;
    }
    public Long getResultId() {
        return resultId;
    }
    public Integer getSeverity() {
        return severity;
    }
    public String getSeverityName() {
        return severityName;
    }
    public Integer getStatus() {
        return status;
    }
    public String getStatusName() {
        return statusName;
    }
    public String getSuggestion() {
        return suggestion;
    }
    public Long getTaskId() {
        return taskId;
    }
    public String getTaskName() {
        return taskName;
    }
    public String getTitle() {
        return title;
    }
    public String getTypeName() {
        return typeName;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setCheckDimension(Integer checkDimension) {
        this.checkDimension = checkDimension;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }
    public void setHandleNote(String handleNote) {
        this.handleNote = handleNote;
    }
    public void setHandledAt(String handledAt) {
        this.handledAt = handledAt;
    }
    public void setHandledBy(Long handledBy) {
        this.handledBy = handledBy;
    }
    public void setHandledByName(String handledByName) {
        this.handledByName = handledByName;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setIssueType(Integer issueType) {
        this.issueType = issueType;
    }
    public void setItemResultId(Long itemResultId) {
        this.itemResultId = itemResultId;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }
    public void setSeverity(Integer severity) {
        this.severity = severity;
    }
    public void setSeverityName(String severityName) {
        this.severityName = severityName;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}