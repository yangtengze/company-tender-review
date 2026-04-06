package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class LLMLogQueryRequest {
    private Long taskId;
    @Size(max = 128)
    private String modelName;
    @Min(1)
    @Max(3)
    private Integer status;
    @DateTimeFormat
    private String dateFrom;
    @DateTimeFormat
    private String dateTo;
    @Min(1)
    private Integer page;
    @Max(100)
    private Integer size;
    public String getDateFrom() {
        return dateFrom;
    }
    public String getDateTo() {
        return dateTo;
    }
    public String getModelName() {
        return modelName;
    }
    public Integer getPage() {
        return page;
    }
    public Integer getSize() {
        return size;
    }
    public Integer getStatus() {
        return status;
    }
    public Long getTaskId() {
        return taskId;
    }
    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }
    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    public void setPage(Integer page) {
        this.page = page;
    }
    public void setSize(Integer size) {
        this.size = size;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
}
