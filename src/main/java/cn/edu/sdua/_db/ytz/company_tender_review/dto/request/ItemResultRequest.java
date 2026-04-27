package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import java.math.BigDecimal;

public class ItemResultRequest {
    private Long taskId;
    private Integer checkDimension;
    private String dimensionName;
    private Integer verdict;
    private BigDecimal confidence;
    private String detail;
    private String evidence;
    private String issueDesc;
    private String suggestion;

    public Integer getCheckDimension() {
        return checkDimension;
    }
    public BigDecimal getConfidence() {
        return confidence;
    }
    public String getDetail() {
        return detail;
    }
    public String getDimensionName() {
        return dimensionName;
    }
    public String getEvidence() {
        return evidence;
    }
    public String getIssueDesc() {
        return issueDesc;
    }
    public String getSuggestion() {
        return suggestion;
    }
    public Long getTaskId() {
        return taskId;
    }
    public Integer getVerdict() {
        return verdict;
    }
    public void setCheckDimension(Integer checkDimension) {
        this.checkDimension = checkDimension;
    }
    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }
    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }
    public void setIssueDesc(String issueDesc) {
        this.issueDesc = issueDesc;
    }
    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    public void setVerdict(Integer verdict) {
        this.verdict = verdict;
    }
}
