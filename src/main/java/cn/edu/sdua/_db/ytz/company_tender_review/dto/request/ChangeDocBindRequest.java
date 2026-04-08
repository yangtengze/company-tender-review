package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ChangeDocBindRequest {
    @NotNull(message = "文档ID不能为空")
    @Positive(message = "文档ID必须为正数")
    private Long docId;

    @NotNull(message = "文档角色不能为空")
    @Min(value = 1, message = "文档角色最小为1")
    @Max(value = 4, message = "文档角色最大为4")
    private Integer docRole;

    public Long getDocId() { return docId; }
    public void setDocId(Long docId) { this.docId = docId; }

    public Integer getDocRole() { return docRole; }
    public void setDocRole(Integer docRole) { this.docRole = docRole; }
}

