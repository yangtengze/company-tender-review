package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class LawQueryRequest {
    @Min(value = 1, message = "法规类别最小为1")
    @Max(value = 5, message = "法规类别最大为5")
    private Integer category;
    @Min(value = 0, message = "状态最小为0")
    @Max(value = 1, message = "状态最大为1")
    private Integer status;
    @Size(max = 100, message = "关键字长度不能超过100字符")
    private String keyword;
    @Min(value = 1, message = "页码最小为1")
    private Integer page;
    @Max(value = 100, message = "每页最大为100条")
    private Integer size;
    
    public Integer getCategory() {
        return category;
    }
    public String getKeyword() {
        return keyword;
    }
    public Integer getPage() {
        return page;
    }
    public Integer getSize() {
        return size;
    }
    public Integer getStatus() {
        return status;
    }
    public void setCategory(Integer category) {
        this.category = category;
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public void setPage(Integer page) {
        this.page = page;
    }
    public void setSize(Integer size) {
        this.size = size;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
}
