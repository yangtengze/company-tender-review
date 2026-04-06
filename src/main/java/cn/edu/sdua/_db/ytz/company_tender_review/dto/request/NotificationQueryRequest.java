package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class NotificationQueryRequest {
    @Min(0)
    @Max(1)
    private Integer isRead;
    @Min(1)
    @Max(4)
    private Integer type;
    @Min(1)
    private Integer page;
    @Max(100)
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
