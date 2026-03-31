package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class LawQueryRequest {
    @Min(1)
    @Max(5)
    private Integer category;
    @Min(0)
    @Max(1)
    private Integer status;
    @Size(max = 100)
    private String keyword;
    @Min(1)
    private Integer page;
    @Max(100)
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
