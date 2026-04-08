package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class ProjectQueryRequest {
    @Min(value = 1, message = "状态最小为1")
    @Max(value = 5, message = "状态最大为5")
    private Integer status;

    @Min(value = 1, message = "项目类型最小为1")
    @Max(value = 5, message = "项目类型最大为5")
    private Integer projectType;

    private Long buildOrgId;

    @Size(max = 100, message = "关键字长度不能超过100字符")
    private String keyword;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate plannedStartFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate plannedStartTo;

    @Min(value = 1, message = "页码最小为1")
    private Integer page = 1;

    @Max(value = 100, message = "每页最大为100条")
    private Integer size = 20;

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getProjectType() { return projectType; }
    public void setProjectType(Integer projectType) { this.projectType = projectType; }
    public Long getBuildOrgId() { return buildOrgId; }
    public void setBuildOrgId(Long buildOrgId) { this.buildOrgId = buildOrgId; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public LocalDate getPlannedStartFrom() { return plannedStartFrom; }
    public void setPlannedStartFrom(LocalDate plannedStartFrom) { this.plannedStartFrom = plannedStartFrom; }
    public LocalDate getPlannedStartTo() { return plannedStartTo; }
    public void setPlannedStartTo(LocalDate plannedStartTo) { this.plannedStartTo = plannedStartTo; }
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
}
