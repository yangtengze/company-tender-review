package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProjectCreateRequest {
    @NotBlank
    @Size(max = 64)
    private String projectNo;
    @NotBlank
    @Size(max = 256)
    private String projectName;
    @NotNull @Min(1) @Max(5)
    private Integer projectType;
    @NotNull @Positive
    private Long buildOrgId;
    @Positive
    private Long contractorId;
    @Positive
    private Long supervisorId;
    @DecimalMin("0.01")
    private BigDecimal totalInvestment;
    @DecimalMin("0.01")
    private BigDecimal contractAmount;
    @Size(max = 256)
    private String location;
    @Size(max = 128)
    private String approvalNo;
    private LocalDate approvalDate;
    private LocalDate plannedStart;
    private LocalDate plannedEnd;
    @Size(max = 2000)
    private String description;
    public String getProjectNo() { return projectNo; }
    public void setProjectNo(String projectNo) { this.projectNo = projectNo; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public Integer getProjectType() { return projectType; }
    public void setProjectType(Integer projectType) { this.projectType = projectType; }
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
    public LocalDate getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDate approvalDate) { this.approvalDate = approvalDate; }
    public LocalDate getPlannedStart() { return plannedStart; }
    public void setPlannedStart(LocalDate plannedStart) { this.plannedStart = plannedStart; }
    public LocalDate getPlannedEnd() { return plannedEnd; }
    public void setPlannedEnd(LocalDate plannedEnd) { this.plannedEnd = plannedEnd; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
