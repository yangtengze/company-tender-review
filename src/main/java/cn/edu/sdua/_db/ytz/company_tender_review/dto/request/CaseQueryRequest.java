package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class CaseQueryRequest {
    @Min(1)
    @Max(4)
    private Integer caseType;
    @Min(1)
    @Max(6)
    private Integer issueType;
    @Min(1)
    @Max(5)
    private Integer projectType;
    @Size(max = 100)
    private String keyword;
    @Min(1)
    private Integer page;
    @Max(100)
    private Integer size;
    
    public Integer getCaseType() {
        return caseType;
    }
    public Integer getIssueType() {
        return issueType;
    }
    public String getKeyword() {
        return keyword;
    }
    public Integer getPage() {
        return page;
    }
    public Integer getProjectType() {
        return projectType;
    }
    public Integer getSize() {
        return size;
    }
    public void setCaseType(Integer caseType) {
        this.caseType = caseType;
    }
    public void setIssueType(Integer issueType) {
        this.issueType = issueType;
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public void setPage(Integer page) {
        this.page = page;
    }
    public void setProjectType(Integer projectType) {
        this.projectType = projectType;
    }
    public void setSize(Integer size) {
        this.size = size;
    }
}
