package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class NotificationQueryRequest {
    @Min(value = 0, message = "已读状态最小为0")
    @Max(value = 1, message = "已读状态最大为1")
    private Integer isRead;
    @Min(value = 1, message = "通知类型最小为1")
    @Max(value = 4, message = "通知类型最大为4")
    private Integer type;
    @Min(value = 1, message = "页码最小为1")
    private Integer page;
    @Max(value = 100, message = "每页最大为100条")
    private Integer size;
    public Integer getIsRead() {
        return isRead;
    }
    public Integer getPage() {
        return page;
    }
    public Integer getSize() {
        return size;
    }
    public Integer getType() {
        return type;
    }
    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }
    public void setPage(Integer page) {
        this.page = page;
    }
    public void setSize(Integer size) {
        this.size = size;
    }
    public void setType(Integer type) {
        this.type = type;
    }
}
