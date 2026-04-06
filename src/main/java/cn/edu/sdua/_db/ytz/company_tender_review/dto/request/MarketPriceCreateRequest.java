package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MarketPriceCreateRequest {
    @NotBlank
    @Size(max = 64)
    private String itemCode;
    @NotBlank
    @Size(max = 256)
    private String itemName;
    @Size(max = 32)
    private String unit;
    @NotNull
    @Min(1)
    @Max(4)
    private Integer category;
    @NotNull
    @DecimalMin("0")
    private BigDecimal price;
    @NotNull
    private LocalDate priceDate;
    @Size(max = 64)
    private String region;
    @Size(max = 128)
    private String source;
    public Integer getCategory() {
        return category;
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
    public LocalDate getPriceDate() {
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
    public void setRegion(String region) {
        this.region = region;
    }
    public void setCategory(Integer category) {
        this.category = category;
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
    public void setPriceDate(LocalDate priceDate) {
        this.priceDate = priceDate;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
}
