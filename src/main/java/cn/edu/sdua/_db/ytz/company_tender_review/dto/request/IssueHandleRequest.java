package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class IssueHandleRequest {
    @NotNull(message = "状态不能为空")
    @Min(value = 1, message = "状态最小为1")
    @Max(value = 4, message = "状态最大为4")
    private Integer status;
    @Size(max=2000, message = "处理说明长度不能超过2000字符")
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
