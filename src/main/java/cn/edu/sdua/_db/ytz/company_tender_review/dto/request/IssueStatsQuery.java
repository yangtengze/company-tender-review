package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class IssueStatsQuery {
    @NotNull(message = "项目ID不能为空")
    @Positive(message = "项目ID必须为正数")
    private Long projectId;
    public Long getProjectId() {
        return projectId;
    }
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
