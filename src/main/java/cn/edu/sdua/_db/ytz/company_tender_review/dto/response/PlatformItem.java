package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

public class PlatformItem {
    private Long id;
    private String name;
    private String url;
    private Integer level;
    private String levelName;
    private String region;
    private Integer isApproved;
    private String remark;
    private String createdAt;
    private String updatedAt;
    public String getCreatedAt() {
        return createdAt;
    }
    public Long getId() {
        return id;
    }
    public Integer getIsApproved() {
        return isApproved;
    }
    public Integer getLevel() {
        return level;
    }
    public String getLevelName() {
        return levelName;
    }
    public String getName() {
        return name;
    }
    public String getRegion() {
        return region;
    }
    public String getRemark() {
        return remark;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }
    public String getUrl() {
        return url;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setIsApproved(Integer isApproved) {
        this.isApproved = isApproved;
    }
    public void setLevel(Integer level) {
        this.level = level;
    }
    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
