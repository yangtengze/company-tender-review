package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

public class ReviewTaskCreateRequest {
    @NotNull
    @Positive
    private Long projectId;

    @NotNull
    @Min(1)
    @Max(2)
    private Integer taskType;

    @NotBlank
    @Size(max = 256)
    private String taskName;

    @Positive
    private Long changeId;

    @NotEmpty
    @Size(max = 20)
    private List<Long> docIds;

    private Map<Long, String> docRoles;

    @Min(1)
    @Max(3)
    private Integer priority;

    @Positive
    private Long assigneeId;

    @Min(1)
    @Max(2)
    private Integer triggerMode;

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Integer getTaskType() { return taskType; }
    public void setTaskType(Integer taskType) { this.taskType = taskType; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public Long getChangeId() { return changeId; }
    public void setChangeId(Long changeId) { this.changeId = changeId; }
    public List<Long> getDocIds() { return docIds; }
    public void setDocIds(List<Long> docIds) { this.docIds = docIds; }
    public Map<Long, String> getDocRoles() { return docRoles; }
    public void setDocRoles(Map<Long, String> docRoles) { this.docRoles = docRoles; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public Long getAssigneeId() { return assigneeId; }
    public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }
    public Integer getTriggerMode() { return triggerMode; }
    public void setTriggerMode(Integer triggerMode) { this.triggerMode = triggerMode; }
}

