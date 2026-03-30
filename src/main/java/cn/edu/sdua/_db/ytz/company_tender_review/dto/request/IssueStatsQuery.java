package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class IssueStatsQuery {
    @NotNull
    @Positive
    private Long projectId;
    public Long getProjectId() {
        return projectId;
    }
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
