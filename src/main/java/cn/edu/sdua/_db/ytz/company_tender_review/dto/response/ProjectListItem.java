package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

import java.math.BigDecimal;

public class ProjectListItem {
    private Long id;
    private String projectNo;
    private String projectName;
    private Integer projectType;
    private String projectTypeName;
    private Integer status;
    private String statusName;
    private Long buildOrgId;
    private String buildOrgName;
    private String contractorName;
    private String supervisorName;
    private BigDecimal totalInvestment;
    private BigDecimal contractAmount;
    private String plannedStart;
    private String plannedEnd;
    private String location;
    private Integer pendingIssues;
    private String createdAt;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProjectNo() { return projectNo; }
    public void setProjectNo(String projectNo) { this.projectNo = projectNo; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public Integer getProjectType() { return projectType; }
    public void setProjectType(Integer projectType) { this.projectType = projectType; }
    public String getProjectTypeName() { return projectTypeName; }
    public void setProjectTypeName(String projectTypeName) { this.projectTypeName = projectTypeName; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
    public Long getBuildOrgId() { return buildOrgId; }
    public void setBuildOrgId(Long buildOrgId) { this.buildOrgId = buildOrgId; }
    public String getBuildOrgName() { return buildOrgName; }
    public void setBuildOrgName(String buildOrgName) { this.buildOrgName = buildOrgName; }
    public String getContractorName() { return contractorName; }
    public void setContractorName(String contractorName) { this.contractorName = contractorName; }
    public String getSupervisorName() { return supervisorName; }
    public void setSupervisorName(String supervisorName) { this.supervisorName = supervisorName; }
    public BigDecimal getTotalInvestment() { return totalInvestment; }
    public void setTotalInvestment(BigDecimal totalInvestment) { this.totalInvestment = totalInvestment; }
    public BigDecimal getContractAmount() { return contractAmount; }
    public void setContractAmount(BigDecimal contractAmount) { this.contractAmount = contractAmount; }
    public String getPlannedStart() { return plannedStart; }
    public void setPlannedStart(String plannedStart) { this.plannedStart = plannedStart; }
    public String getPlannedEnd() { return plannedEnd; }
    public void setPlannedEnd(String plannedEnd) { this.plannedEnd = plannedEnd; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getPendingIssues() { return pendingIssues; }
    public void setPendingIssues(Integer pendingIssues) { this.pendingIssues = pendingIssues; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
