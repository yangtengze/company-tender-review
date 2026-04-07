package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

public class DimensionStatItem {
    private Integer checkDimension;
    private String dimensionName;
    private Integer total;
    private Integer compliant;
    private Integer problematic;
    private Double complianceRate;
    private Double avgConfidence;
    public Double getAvgConfidence() {
        return avgConfidence;
    }
    public Integer getCheckDimension() {
        return checkDimension;
    }
    public Double getComplianceRate() {
        return complianceRate;
    }
    public Integer getCompliant() {
        return compliant;
    }
    public String getDimensionName() {
        return dimensionName;
    }
    public Integer getProblematic() {
        return problematic;
    }
    public Integer getTotal() {
        return total;
    }
    public void setAvgConfidence(Double avgConfidence) {
        this.avgConfidence = avgConfidence;
    }
    public void setCheckDimension(Integer checkDimension) {
        this.checkDimension = checkDimension;
    }
    public void setComplianceRate(Double complianceRate) {
        this.complianceRate = complianceRate;
    }
    public void setCompliant(Integer compliant) {
        this.compliant = compliant;
    }
    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }
    public void setProblematic(Integer problematic) {
        this.problematic = problematic;
    }
    public void setTotal(Integer total) {
        this.total = total;
    }
}
