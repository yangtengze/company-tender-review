package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

import java.math.BigDecimal;

public class ProjectDetailResponse {
    private Long id;
    private String projectNo;
    private String projectName;
    private Integer projectType;
    private Integer status;
    private Long buildOrgId;
    private Long contractorId;
    private Long supervisorId;
    private BigDecimal totalInvestment;
    private BigDecimal contractAmount;
    private String location;
    private String approvalNo;
    private String approvalDate;
    private String plannedStart;
    private String plannedEnd;
    private String actualStart;
    private String actualEnd;
    private String description;
    private String createdAt;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProjectNo() { return projectNo; }
    public void setProjectNo(String projectNo) { this.projectNo = projectNo; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public Integer getProjectType() { return projectType; }
    public void setProjectType(Integer projectType) { this.projectType = projectType; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Long getBuildOrgId() { return buildOrgId; }
    public void setBuildOrgId(Long buildOrgId) { this.buildOrgId = buildOrgId; }
    public Long getContractorId() { return contractorId; }
    public void setContractorId(Long contractorId) { this.contractorId = contractorId; }
    public Long getSupervisorId() { return supervisorId; }
    public void setSupervisorId(Long supervisorId) { this.supervisorId = supervisorId; }
    public BigDecimal getTotalInvestment() { return totalInvestment; }
    public void setTotalInvestment(BigDecimal totalInvestment) { this.totalInvestment = totalInvestment; }
    public BigDecimal getContractAmount() { return contractAmount; }
    public void setContractAmount(BigDecimal contractAmount) { this.contractAmount = contractAmount; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getApprovalNo() { return approvalNo; }
    public void setApprovalNo(String approvalNo) { this.approvalNo = approvalNo; }
    public String getApprovalDate() { return approvalDate; }
    public void setApprovalDate(String approvalDate) { this.approvalDate = approvalDate; }
    public String getPlannedStart() { return plannedStart; }
    public void setPlannedStart(String plannedStart) { this.plannedStart = plannedStart; }
    public String getPlannedEnd() { return plannedEnd; }
    public void setPlannedEnd(String plannedEnd) { this.plannedEnd = plannedEnd; }
    public String getActualStart() { return actualStart; }
    public void setActualStart(String actualStart) { this.actualStart = actualStart; }
    public String getActualEnd() { return actualEnd; }
    public void setActualEnd(String actualEnd) { this.actualEnd = actualEnd; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
