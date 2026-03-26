package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.validator.constraints.URL;


public class BidAnnouncementCreateRequest {
    @NotNull
    @Positive
    private Long docId;

    @NotNull
    @Positive
    private Long projectId;

    @Size(max = 128)
    private String bidNo;

    @Min(1)
    @Max(3)
    private Integer bidType;

    private LocalDateTime publishDate;
    private LocalDateTime deadlineDate;
    private LocalDateTime bidOpenDate;

    @Size(max = 128)
    private String platformName;

    @URL
    @Size(max = 512)
    private String platformUrl;

    private String qualificationReq;
    private String performanceReq;

    @DecimalMin("0")
    private BigDecimal estimatedPrice;

    public Long getDocId() { return docId; }
    public void setDocId(Long docId) { this.docId = docId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getBidNo() { return bidNo; }
    public void setBidNo(String bidNo) { this.bidNo = bidNo; }

    public Integer getBidType() { return bidType; }
    public void setBidType(Integer bidType) { this.bidType = bidType; }

    public LocalDateTime getPublishDate() { return publishDate; }
    public void setPublishDate(LocalDateTime publishDate) { this.publishDate = publishDate; }

    public LocalDateTime getDeadlineDate() { return deadlineDate; }
    public void setDeadlineDate(LocalDateTime deadlineDate) { this.deadlineDate = deadlineDate; }

    public LocalDateTime getBidOpenDate() { return bidOpenDate; }
    public void setBidOpenDate(LocalDateTime bidOpenDate) { this.bidOpenDate = bidOpenDate; }

    public String getPlatformName() { return platformName; }
    public void setPlatformName(String platformName) { this.platformName = platformName; }

    public String getPlatformUrl() { return platformUrl; }
    public void setPlatformUrl(String platformUrl) { this.platformUrl = platformUrl; }

    public String getQualificationReq() { return qualificationReq; }
    public void setQualificationReq(String qualificationReq) { this.qualificationReq = qualificationReq; }

    public String getPerformanceReq() { return performanceReq; }
    public void setPerformanceReq(String performanceReq) { this.performanceReq = performanceReq; }

    public BigDecimal getEstimatedPrice() { return estimatedPrice; }
    public void setEstimatedPrice(BigDecimal estimatedPrice) { this.estimatedPrice = estimatedPrice; }
}

