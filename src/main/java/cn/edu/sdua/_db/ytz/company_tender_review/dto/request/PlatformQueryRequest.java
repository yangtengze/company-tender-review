package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class PlatformQueryRequest {
    @Min(1)
    @Max(4)
    private Integer level;
    @Size(max = 64)
    private String region;
    @Min(0)
    @Max(1)
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
