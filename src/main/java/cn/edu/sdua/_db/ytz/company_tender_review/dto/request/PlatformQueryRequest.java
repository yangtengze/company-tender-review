package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class PlatformQueryRequest {
    @Min(value = 1, message = "平台等级最小为1")
    @Max(value = 4, message = "平台等级最大为4")
    private Integer level;
    @Size(max = 64, message = "区域长度不能超过64字符")
    private String region;
    @Min(value = 0, message = "是否认证最小为0")
    @Max(value = 1, message = "是否认证最大为1")
    private Integer isApproved;
    public Integer getIsApproved() {
        return isApproved;
    }
    public Integer getLevel() {
        return level;
    }
    public String getRegion() {
        return region;
    }
    public void setIsApproved(Integer isApproved) {
        this.isApproved = isApproved;
    }
    public void setLevel(Integer level) {
        this.level = level;
    }
    public void setRegion(String region) {
        this.region = region;
    }
}
