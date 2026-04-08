package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class IssueQueryRequest {
    private Long projectId;
    private Long resultId;
    @Min(value = 1, message = "严重度最小为1")
    @Max(value = 4, message = "严重度最大为4")
    private Integer severity;
    @Min(value = 1, message = "问题类型最小为1")
    @Max(value = 6, message = "问题类型最大为6")
    private Integer issueType;
    @Min(value = 1, message = "状态最小为1")
    @Max(value = 4, message = "状态最大为4")
    private Integer status;
    @Min(value = 1, message = "页码最小为1")
    private Integer page;
    @Max(value = 100, message = "每页最大为100条")
    private Integer size;
    public Integer getIssueType() {
        return issueType;
    }
    public Integer getPage() {
        return page;
    }
    public Long getProjectId() {
        return projectId;
    }
    public Long getResultId() {
        return resultId;
    }
    public Integer getSeverity() {
        return severity;
    }
    public Integer getSize() {
        return size;
    }
    public Integer getStatus() {
        return status;
    }
    public void setIssueType(Integer issueType) {
        this.issueType = issueType;
    }
    public void setPage(Integer page) {
        this.page = page;
    }
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }
    public void setSeverity(Integer severity) {
        this.severity = severity;
    }
    public void setSize(Integer size) {
        this.size = size;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
}
