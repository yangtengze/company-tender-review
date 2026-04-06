package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

import java.math.BigDecimal;

public class MarketPriceItem {
    private Long id;
    private String itemCode;
    private String itemName;
    private String unit;
    private Integer category;
    private String categoryName;
    private BigDecimal price;
    private String priceDate;
    private String region;
    private String source;
    public Integer getCategory() {
        return category;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public Long getId() {
        return id;
    }
    public String getItemCode() {
        return itemCode;
    }
    public String getItemName() {
        return itemName;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public String getPriceDate() {
        return priceDate;
    }
    public String getRegion() {
        return region;
    }
    public String getSource() {
        return source;
    }
    public String getUnit() {
        return unit;
    }
    public void setCategory(Integer category) {
        this.category = category;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public void setPriceDate(String priceDate) {
        this.priceDate = priceDate;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
}
