package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class DimensionStatsQuery {
    private Long projectId;
    @Min(1)
    @Max(2)
    private Integer taskType;
    @DateTimeFormat
    private String dateFrom;
    @DateTimeFormat
    private String dateTo;
    public String getDateFrom() {
        return dateFrom;
    }
    public String getDateTo() {
        return dateTo;
    }
    public Long getProjectId() {
        return projectId;
    }
    public Integer getTaskType() {
        return taskType;
    }
    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }
    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }
}
