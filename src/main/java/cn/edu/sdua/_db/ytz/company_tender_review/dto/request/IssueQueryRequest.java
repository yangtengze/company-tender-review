package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class IssueQueryRequest {
    private Long projectId;
    private Long resultId;
    @Min(1)
    @Max(4)
    private Integer severity;
    @Min(1)
    @Max(6)
    private Integer issueType;
    @Min(1)
    @Max(4)
    private Integer status;
    @Min(1)
    private Integer page;
    @Max(100)
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
