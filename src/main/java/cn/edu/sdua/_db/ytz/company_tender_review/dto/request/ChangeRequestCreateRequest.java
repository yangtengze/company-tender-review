package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ChangeRequestCreateRequest {
    @NotNull(message = "项目ID不能为空")
    @Positive(message = "项目ID必须为正数")
    private Long projectId;

    @NotBlank(message = "变更编号不能为空")
    @Size(max = 64, message = "变更编号长度不能超过64字符")
    private String changeNo;

    @NotNull(message = "变更类型不能为空")
    @Min(value = 1, message = "变更类型最小为1")
    @Max(value = 5, message = "变更类型最大为5")
    private Integer changeType;

    @Min(value = 1, message = "变更原因最小为1")
    @Max(value = 5, message = "变更原因最大为5")
    private Integer changeReason;

    private String reasonDesc;

    @NotBlank(message = "变更说明不能为空")
    private String changeDesc;

    @DecimalMin(value = "0", message = "原始金额不能为负数")
    private BigDecimal originalAmount;

    @NotNull(message = "变更金额不能为空")
    private BigDecimal changeAmount;

    private LocalDate applyDate;

    @Positive(message = "申请机构ID必须为正数")
    private Long applyOrgId;

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getChangeNo() { return changeNo; }
    public void setChangeNo(String changeNo) { this.changeNo = changeNo; }
    public Integer getChangeType() { return changeType; }
    public void setChangeType(Integer changeType) { this.changeType = changeType; }
    public Integer getChangeReason() { return changeReason; }
    public void setChangeReason(Integer changeReason) { this.changeReason = changeReason; }
    public String getReasonDesc() { return reasonDesc; }
    public void setReasonDesc(String reasonDesc) { this.reasonDesc = reasonDesc; }
    public String getChangeDesc() { return changeDesc; }
    public void setChangeDesc(String changeDesc) { this.changeDesc = changeDesc; }
    public BigDecimal getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(BigDecimal originalAmount) { this.originalAmount = originalAmount; }
    public BigDecimal getChangeAmount() { return changeAmount; }
    public void setChangeAmount(BigDecimal changeAmount) { this.changeAmount = changeAmount; }
    public LocalDate getApplyDate() { return applyDate; }
    public void setApplyDate(LocalDate applyDate) { this.applyDate = applyDate; }
    public Long getApplyOrgId() { return applyOrgId; }
    public void setApplyOrgId(Long applyOrgId) { this.applyOrgId = applyOrgId; }
}

