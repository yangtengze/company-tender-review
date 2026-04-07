package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

public class DashboardOverviewResponse {
    private Integer activeProjects;
    private Integer pendingTasks;
    private Integer completedTasks;
    private Integer highRiskIssues;
    private Integer pendingChanges;
    private Long tokensThisMonth;
    private Double complianceRate;
    private Double avgReviewTime;
    public Integer getActiveProjects() {
        return activeProjects;
    }
    public Double getAvgReviewTime() {
        return avgReviewTime;
    }
    public Integer getCompletedTasks() {
        return completedTasks;
    }
    public Double getComplianceRate() {
        return complianceRate;
    }
    public Integer getHighRiskIssues() {
        return highRiskIssues;
    }
    public Integer getPendingChanges() {
        return pendingChanges;
    }
    public Integer getPendingTasks() {
        return pendingTasks;
    }
    public Long getTokensThisMonth() {
        return tokensThisMonth;
    }
    public void setActiveProjects(Integer activeProjects) {
        this.activeProjects = activeProjects;
    }
    public void setAvgReviewTime(Double avgReviewTime) {
        this.avgReviewTime = avgReviewTime;
    }
    public void setCompletedTasks(Integer completedTasks) {
        this.completedTasks = completedTasks;
    }
    public void setComplianceRate(Double complianceRate) {
        this.complianceRate = complianceRate;
    }
    public void setHighRiskIssues(Integer highRiskIssues) {
        this.highRiskIssues = highRiskIssues;
    }
    public void setPendingChanges(Integer pendingChanges) {
        this.pendingChanges = pendingChanges;
    }
    public void setPendingTasks(Integer pendingTasks) {
        this.pendingTasks = pendingTasks;
    }
    public void setTokensThisMonth(Long tokensThisMonth) {
        this.tokensThisMonth = tokensThisMonth;
    }
}
