package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

import java.math.BigDecimal;

public class BidAnnouncementResponse {
    private Long id;
    private Long docId;
    private Long projectId;
    private String bidNo;
    private Integer bidType;
    private String bidTypeName;
    private String publishDate;
    private String deadlineDate;
    private String bidOpenDate;
    private Integer publicNoticeDays;
    private String platformName;
    private String platformUrl;
    private Integer isPublicPlatform;
    private String qualificationReq;
    private String performanceReq;
    private BigDecimal estimatedPrice;
    private String updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDocId() { return docId; }
    public void setDocId(Long docId) { this.docId = docId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getBidNo() { return bidNo; }
    public void setBidNo(String bidNo) { this.bidNo = bidNo; }

    public Integer getBidType() { return bidType; }
    public void setBidType(Integer bidType) { this.bidType = bidType; }

    public String getBidTypeName() { return bidTypeName; }
    public void setBidTypeName(String bidTypeName) { this.bidTypeName = bidTypeName; }

    public String getPublishDate() { return publishDate; }
    public void setPublishDate(String publishDate) { this.publishDate = publishDate; }

    public String getDeadlineDate() { return deadlineDate; }
    public void setDeadlineDate(String deadlineDate) { this.deadlineDate = deadlineDate; }

    public String getBidOpenDate() { return bidOpenDate; }
    public void setBidOpenDate(String bidOpenDate) { this.bidOpenDate = bidOpenDate; }

    public Integer getPublicNoticeDays() { return publicNoticeDays; }
    public void setPublicNoticeDays(Integer publicNoticeDays) { this.publicNoticeDays = publicNoticeDays; }

    public String getPlatformName() { return platformName; }
    public void setPlatformName(String platformName) { this.platformName = platformName; }

    public String getPlatformUrl() { return platformUrl; }
    public void setPlatformUrl(String platformUrl) { this.platformUrl = platformUrl; }

    public Integer getIsPublicPlatform() { return isPublicPlatform; }
    public void setIsPublicPlatform(Integer isPublicPlatform) { this.isPublicPlatform = isPublicPlatform; }

    public String getQualificationReq() { return qualificationReq; }
    public void setQualificationReq(String qualificationReq) { this.qualificationReq = qualificationReq; }

    public String getPerformanceReq() { return performanceReq; }
    public void setPerformanceReq(String performanceReq) { this.performanceReq = performanceReq; }

    public BigDecimal getEstimatedPrice() { return estimatedPrice; }
    public void setEstimatedPrice(BigDecimal estimatedPrice) { this.estimatedPrice = estimatedPrice; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}

