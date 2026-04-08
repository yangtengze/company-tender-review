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

    @NotNull(message = "项目ID不能为空")
    @Positive(message = "项目ID必须为正数")
    private Long projectId;


    @NotNull(message = "任务类型不能为空")
    @Min(value = 1, message = "任务类型最小为1")
    @Max(value = 2, message = "任务类型最大为2")
    private Integer taskType;


    @NotBlank(message = "任务名称不能为空")
    @Size(max = 256, message = "任务名称长度不能超过256字符")
    private String taskName;


    @Positive(message = "变更ID必须为正数")
    private Long changeId;


    @NotEmpty(message = "文档ID列表不能为空")
    @Size(max = 20, message = "文档ID列表最多20个")
    @jakarta.validation.Valid
    private List<Long> docIds;

    private Map<Long, String> docRoles;


    @Min(value = 1, message = "优先级最小为1")
    @Max(value = 3, message = "优先级最大为3")
    private Integer priority;


    @Positive(message = "分配人ID必须为正数")
    private Long assigneeId;


    @Min(value = 1, message = "触发方式最小为1")
    @Max(value = 2, message = "触发方式最大为2")
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

