package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class ChangeRequestDetailResponse {
    private Long id;
    private Long projectId;
    private String projectName;
    private String changeNo;
    private Integer changeType;
    private String changeTypeName;
    private Integer changeReason;
    private String reasonDesc;
    private String changeDesc;
    private BigDecimal originalAmount;
    private BigDecimal changeAmount;
    private BigDecimal changeRatio;
    private String applyDate;
    private Long applyOrgId;
    private String applyOrgName;
    private Integer status;
    private String statusName;
    private List<ChangeDocItem> docs;
    private String createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getChangeNo() { return changeNo; }
    public void setChangeNo(String changeNo) { this.changeNo = changeNo; }

    public Integer getChangeType() { return changeType; }
    public void setChangeType(Integer changeType) { this.changeType = changeType; }

    public String getChangeTypeName() { return changeTypeName; }
    public void setChangeTypeName(String changeTypeName) { this.changeTypeName = changeTypeName; }

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

    public BigDecimal getChangeRatio() { return changeRatio; }
    public void setChangeRatio(BigDecimal changeRatio) { this.changeRatio = changeRatio; }

    public String getApplyDate() { return applyDate; }
    public void setApplyDate(String applyDate) { this.applyDate = applyDate; }

    public Long getApplyOrgId() { return applyOrgId; }
    public void setApplyOrgId(Long applyOrgId) { this.applyOrgId = applyOrgId; }

    public String getApplyOrgName() { return applyOrgName; }
    public void setApplyOrgName(String applyOrgName) { this.applyOrgName = applyOrgName; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public List<ChangeDocItem> getDocs() { return docs; }
    public void setDocs(List<ChangeDocItem> docs) { this.docs = docs; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

