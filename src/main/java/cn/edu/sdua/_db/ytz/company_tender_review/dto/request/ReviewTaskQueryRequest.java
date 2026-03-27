package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class ReviewTaskQueryRequest {
    private Long projectId;

    @Min(1)
    @Max(2)
    private Integer taskType;

    @Min(1)
    @Max(5)
    private Integer status;

    private Long assigneeId;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateTo;

    @Min(1)
    private Integer page = 1;

    @Max(100)
    private Integer size = 20;

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Integer getTaskType() { return taskType; }
    public void setTaskType(Integer taskType) { this.taskType = taskType; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Long getAssigneeId() { return assigneeId; }
    public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }
    public LocalDateTime getDateFrom() { return dateFrom; }
    public void setDateFrom(LocalDateTime dateFrom) { this.dateFrom = dateFrom; }
    public LocalDateTime getDateTo() { return dateTo; }
    public void setDateTo(LocalDateTime dateTo) { this.dateTo = dateTo; }
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
}

