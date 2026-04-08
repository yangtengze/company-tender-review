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
    @NotBlank(message = "项目编号不能为空")
    @Size(max = 64, message = "项目编号长度不能超过64字符")
    private String projectNo;
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 256, message = "项目名称长度不能超过256字符")
    private String projectName;
    @NotNull(message = "项目类型不能为空") @Min(value = 1, message = "项目类型最小为1") @Max(value = 5, message = "项目类型最大为5")
    private Integer projectType;
    @NotNull(message = "建设单位ID不能为空") @Positive(message = "建设单位ID必须为正数")
    private Long buildOrgId;
    @Positive(message = "承包商ID必须为正数")
    private Long contractorId;
    @Positive(message = "监理单位ID必须为正数")
    private Long supervisorId;
    @DecimalMin(value = "0.01", message = "总投资不能小于0.01")
    private BigDecimal totalInvestment;
    @DecimalMin(value = "0.01", message = "合同金额不能小于0.01")
    private BigDecimal contractAmount;
    @Size(max = 256, message = "地址长度不能超过256字符")
    private String location;
    @Size(max = 128, message = "批准文号长度不能超过128字符")
    private String approvalNo;
    private LocalDate approvalDate;
    private LocalDate plannedStart;
    private LocalDate plannedEnd;
    @Size(max = 2000, message = "描述长度不能超过2000字符")
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
