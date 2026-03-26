package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContractCreateRequest {
    @NotNull
    @Positive
    private Long docId;

    @NotNull
    @Positive
    private Long projectId;

    @Size(max = 128)
    private String contractNo;

    @DecimalMin("0.01")
    private BigDecimal contractAmount;

    private LocalDate signDate;

    @Size(max = 128)
    private String partyA;

    @Size(max = 128)
    private String partyB;

    private LocalDate startDate;

    private LocalDate endDate;

    @Min(0)
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

