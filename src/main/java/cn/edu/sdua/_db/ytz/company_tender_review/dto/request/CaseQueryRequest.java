package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class CaseQueryRequest {
    @Min(value = 1, message = "案例类型最小为1")
    @Max(value = 4, message = "案例类型最大为4")
    private Integer caseType;
    @Min(value = 1, message = "问题类型最小为1")
    @Max(value = 6, message = "问题类型最大为6")
    private Integer issueType;
    @Min(value = 1, message = "项目类型最小为1")
    @Max(value = 5, message = "项目类型最大为5")
    private Integer projectType;
    @Size(max = 100, message = "关键字长度不能超过100字符")
    private String keyword;
    @Min(value = 1, message = "页码最小为1")
    private Integer page;
    @Max(value = 100, message = "每页最大为100条")
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
