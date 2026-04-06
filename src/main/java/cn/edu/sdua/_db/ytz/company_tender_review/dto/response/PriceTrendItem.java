package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

import java.math.BigDecimal;

public class PriceTrendItem {
    private String priceDate;
    private BigDecimal price;
    private String region;
    private String source;
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
}
