package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class DocumentQueryRequest {
    @NotNull(message = "项目ID不能为空")
    @Positive(message = "项目ID必须为正数")
    private Long projectId;

    @Min(value = 1, message = "文档类型最小为1")
    @Max(value = 99, message = "文档类型最大为99")
    private Integer docType;

    @Min(value = 0, message = "解析状态最小为0")
    @Max(value = 5, message = "解析状态最大为5")
    private Integer parseStatus;

    @Size(max = 100, message = "关键字长度不能超过100字符")
    private String keyword;

    @Min(value = 1, message = "页码最小为1")
    private Integer page = 1;

    @Max(value = 100, message = "每页最大为100条")
    private Integer size = 20;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getDocType() {
        return docType;
    }

    public void setDocType(Integer docType) {
        this.docType = docType;
    }

    public Integer getParseStatus() {
        return parseStatus;
    }

    public void setParseStatus(Integer parseStatus) {
        this.parseStatus = parseStatus;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}

