package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContractCreateRequest {
    @NotNull(message = "文档ID不能为空")
    @Positive(message = "文档ID必须为正数")
    private Long docId;

    @NotNull(message = "项目ID不能为空")
    @Positive(message = "项目ID必须为正数")
    private Long projectId;

    @Size(max = 128, message = "合同编号长度不能超过128字符")
    private String contractNo;

    @DecimalMin(value = "0.01", message = "合同金额不能小于0.01")
    private BigDecimal contractAmount;

    private LocalDate signDate;

    @Size(max = 128, message = "甲方长度不能超过128字符")
    private String partyA;

    @Size(max = 128, message = "乙方长度不能超过128字符")
    private String partyB;

    private LocalDate startDate;

    private LocalDate endDate;

    @Min(value = 0, message = "质保期最小为0")
    private Integer warrantyPeriod;

    private String paymentTerms;

    private String penaltyTerms;

    public Long getDocId() { return docId; }
    public void setDocId(Long docId) { this.docId = docId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getContractNo() { return contractNo; }
    public void setContractNo(String contractNo) { this.contractNo = contractNo; }

    public BigDecimal getContractAmount() { return contractAmount; }
    public void setContractAmount(BigDecimal contractAmount) { this.contractAmount = contractAmount; }

    public LocalDate getSignDate() { return signDate; }
    public void setSignDate(LocalDate signDate) { this.signDate = signDate; }

    public String getPartyA() { return partyA; }
    public void setPartyA(String partyA) { this.partyA = partyA; }

    public String getPartyB() { return partyB; }
    public void setPartyB(String partyB) { this.partyB = partyB; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getWarrantyPeriod() { return warrantyPeriod; }
    public void setWarrantyPeriod(Integer warrantyPeriod) { this.warrantyPeriod = warrantyPeriod; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public String getPenaltyTerms() { return penaltyTerms; }
    public void setPenaltyTerms(String penaltyTerms) { this.penaltyTerms = penaltyTerms; }
}

