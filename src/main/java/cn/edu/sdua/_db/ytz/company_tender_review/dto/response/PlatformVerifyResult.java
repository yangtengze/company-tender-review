package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

public class PlatformVerifyResult {
    private Boolean isApproved;
    private Long platformId;
    private String platformName;
    private Integer level;
    private String levelName;
    private String region;
    private Double matchScore;
    public Boolean getIsApproved() {
        return isApproved;
    }
    public Integer getLevel() {
        return level;
    }
    public String getLevelName() {
        return levelName;
    }
    public Double getMatchScore() {
        return matchScore;
    }
    public Long getPlatformId() {
        return platformId;
    }
    public String getPlatformName() {
        return platformName;
    }
    public String getRegion() {
        return region;
    }
    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }
    public void setLevel(Integer level) {
        this.level = level;
    }
    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }
    public void setMatchScore(Double matchScore) {
        this.matchScore = matchScore;
    }
    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }
    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }
    public void setRegion(String region) {
        this.region = region;
    }
}
