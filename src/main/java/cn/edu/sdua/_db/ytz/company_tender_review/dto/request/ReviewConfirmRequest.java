package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewConfirmRequest {
    @NotNull(message = "评审状态不能为空")
    @Min(value = 2, message = "评审状态最小为2")
    @Max(value = 3, message = "评审状态最大为3")
    private Integer reviewStatus;

    @Size(max = 2000, message = "评审备注长度不能超过2000字符")
    private String reviewerNote;

    public Integer getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(Integer reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewerNote() {
        return reviewerNote;
    }

    public void setReviewerNote(String reviewerNote) {
        this.reviewerNote = reviewerNote;
    }
}

