package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class IssueHandleRequest {
    @NotNull
    @Min(1)
    @Max(4)
    private Integer status;
    @Size(max=2000)
    private String handleNote;
    public String getHandleNote() {
        return handleNote;
    }
    public Integer getStatus() {
        return status;
    }
    public void setHandleNote(String handleNote) {
        this.handleNote = handleNote;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
}
