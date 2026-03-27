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
    @NotNull
    @Positive
    private Long projectId;

    @NotBlank
    @Size(max = 64)
    private String changeNo;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer changeType;

    @Min(1)
    @Max(5)
    private Integer changeReason;

    private String reasonDesc;

    @NotBlank
    private String changeDesc;

    @DecimalMin("0")
    private BigDecimal originalAmount;

    @NotNull
    private BigDecimal changeAmount;

    private LocalDate applyDate;

    @Positive
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

