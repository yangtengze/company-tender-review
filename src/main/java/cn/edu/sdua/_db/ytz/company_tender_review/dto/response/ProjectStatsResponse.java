package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

public class ProjectStatsResponse {
    private Integer tasksTotal;
    private Integer tasksDone;
    private Integer tasksRunning;
    private Integer issuesTotal;
    private Integer issuesPending;
    private Integer issuesResolved;
    private Integer changesTotal;
    private Integer changesPending;
    private Integer docsTotal;
    private Double complianceRate;
    public Integer getTasksTotal() { return tasksTotal; }
    public void setTasksTotal(Integer tasksTotal) { this.tasksTotal = tasksTotal; }
    public Integer getTasksDone() { return tasksDone; }
    public void setTasksDone(Integer tasksDone) { this.tasksDone = tasksDone; }
    public Integer getTasksRunning() { return tasksRunning; }
    public void setTasksRunning(Integer tasksRunning) { this.tasksRunning = tasksRunning; }
    public Integer getIssuesTotal() { return issuesTotal; }
    public void setIssuesTotal(Integer issuesTotal) { this.issuesTotal = issuesTotal; }
    public Integer getIssuesPending() { return issuesPending; }
    public void setIssuesPending(Integer issuesPending) { this.issuesPending = issuesPending; }
    public Integer getIssuesResolved() { return issuesResolved; }
    public void setIssuesResolved(Integer issuesResolved) { this.issuesResolved = issuesResolved; }
    public Integer getChangesTotal() { return changesTotal; }
    public void setChangesTotal(Integer changesTotal) { this.changesTotal = changesTotal; }
    public Integer getChangesPending() { return changesPending; }
    public void setChangesPending(Integer changesPending) { this.changesPending = changesPending; }
    public Integer getDocsTotal() { return docsTotal; }
    public void setDocsTotal(Integer docsTotal) { this.docsTotal = docsTotal; }
    public Double getComplianceRate() { return complianceRate; }
    public void setComplianceRate(Double complianceRate) { this.complianceRate = complianceRate; }
}
