package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class IssueTrendQuery {
    private Long projectId;
    @Min(7)
    @Max(365)
    private Integer days;
    public void setDays(Integer days) {
        this.days = days;
    }
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    public Integer getDays() {
        return days;
    }
    public Long getProjectId() {
        return projectId;
    }
}
