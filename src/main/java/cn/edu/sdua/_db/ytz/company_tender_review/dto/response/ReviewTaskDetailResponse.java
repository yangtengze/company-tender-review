package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

import java.util.List;

public class ReviewTaskDetailResponse {
    private Long id;
    private String taskNo;
    private Long projectId;
    private String projectName;
    private Integer taskType;
    private String taskTypeName;
    private Long changeId;
    private String taskName;
    private Integer status;
    private String statusName;
    private Integer priority;
    private Long assigneeId;
    private String assigneeName;
    private String startAt;
    private String endAt;
    private Integer durationMs;
    private List<TaskDocItem> docs;
    private String createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTaskNo() { return taskNo; }
    public void setTaskNo(String taskNo) { this.taskNo = taskNo; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public Integer getTaskType() { return taskType; }
    public void setTaskType(Integer taskType) { this.taskType = taskType; }
    public String getTaskTypeName() { return taskTypeName; }
    public void setTaskTypeName(String taskTypeName) { this.taskTypeName = taskTypeName; }
    public Long getChangeId() { return changeId; }
    public void setChangeId(Long changeId) { this.changeId = changeId; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public Long getAssigneeId() { return assigneeId; }
    public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }
    public String getAssigneeName() { return assigneeName; }
    public void setAssigneeName(String assigneeName) { this.assigneeName = assigneeName; }
    public String getStartAt() { return startAt; }
    public void setStartAt(String startAt) { this.startAt = startAt; }
    public String getEndAt() { return endAt; }
    public void setEndAt(String endAt) { this.endAt = endAt; }
    public Integer getDurationMs() { return durationMs; }
    public void setDurationMs(Integer durationMs) { this.durationMs = durationMs; }
    public List<TaskDocItem> getDocs() { return docs; }
    public void setDocs(List<TaskDocItem> docs) { this.docs = docs; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

