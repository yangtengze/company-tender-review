package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class PriceQueryRequest {
    @Size(max = 64)
    private String itemCode;
    @Size(max = 100)
    private String keyword;
    @Min(1)
    @Max(4)
    private Integer category;
    @Size(max = 64)
    private String region;
    @DateTimeFormat
    private String priceDateFrom;
    @DateTimeFormat
    private String priceDateTo;
    @Min(1)
    private Integer page;
    @Max(100)
    private Integer size;
    public Integer getCategory() {
        return category;
    }
    public String getItemCode() {
        return itemCode;
    }
    public String getKeyword() {
        return keyword;
    }
    public Integer getPage() {
        return page;
    }
    public String getPriceDateFrom() {
        return priceDateFrom;
    }
    public String getPriceDateTo() {
        return priceDateTo;
    }
    public String getRegion() {
        return region;
    }
    public Integer getSize() {
        return size;
    }
    public void setCategory(Integer category) {
        this.category = category;
    }
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public void setPage(Integer page) {
        this.page = page;
    }
    public void setPriceDateFrom(String priceDateFrom) {
        this.priceDateFrom = priceDateFrom;
    }
    public void setPriceDateTo(String priceDateTo) {
        this.priceDateTo = priceDateTo;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public void setSize(Integer size) {
        this.size = size;
    }
}
