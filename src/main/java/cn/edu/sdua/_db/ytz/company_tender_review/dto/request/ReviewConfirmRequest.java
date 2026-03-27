package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewConfirmRequest {
    @NotNull
    @Min(2)
    @Max(3)
    private Integer reviewStatus;

    @Size(max = 2000)
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

