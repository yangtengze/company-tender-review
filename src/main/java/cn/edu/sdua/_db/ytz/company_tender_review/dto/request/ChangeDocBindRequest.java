package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ChangeDocBindRequest {
    @NotNull
    @Positive
    private Long docId;

    @NotNull
    @Min(1)
    @Max(4)
    private Integer docRole;

    public Long getDocId() { return docId; }
    public void setDocId(Long docId) { this.docId = docId; }

    public Integer getDocRole() { return docRole; }
    public void setDocRole(Integer docRole) { this.docRole = docRole; }
}

