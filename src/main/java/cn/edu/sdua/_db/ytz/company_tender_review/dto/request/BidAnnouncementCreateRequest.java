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

    @NotNull(message = "文档ID不能为空")
    @Positive(message = "文档ID必须为正数")
    private Long docId;


    @NotNull(message = "项目ID不能为空")
    @Positive(message = "项目ID必须为正数")
    private Long projectId;


    @Size(max = 128, message = "招标编号长度不能超过128字符")
    private String bidNo;


    @Min(value = 1, message = "招标类型最小为1")
    @Max(value = 3, message = "招标类型最大为3")
    private Integer bidType;

    private LocalDateTime publishDate;
    private LocalDateTime deadlineDate;
    private LocalDateTime bidOpenDate;


    @Size(max = 128, message = "平台名称长度不能超过128字符")
    private String platformName;


    @URL(message = "平台链接格式不正确")
    @Size(max = 512, message = "平台链接长度不能超过512字符")
    private String platformUrl;

    private String qualificationReq;
    private String performanceReq;


    @DecimalMin(value = "0", message = "预算金额不能为负数")
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

