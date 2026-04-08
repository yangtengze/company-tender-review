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
    @NotBlank(message = "物品编码不能为空")
    @Size(max = 64, message = "物品编码长度不能超过64字符")
    private String itemCode;
    @NotBlank(message = "物品名称不能为空")
    @Size(max = 256, message = "物品名称长度不能超过256字符")
    private String itemName;
    @Size(max = 32, message = "单位长度不能超过32字符")
    private String unit;
    @NotNull(message = "类别不能为空")
    @Min(value = 1, message = "类别最小为1")
    @Max(value = 4, message = "类别最大为4")
    private Integer category;
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0", message = "价格不能为负数")
    private BigDecimal price;
    @NotNull(message = "价格日期不能为空")
    private LocalDate priceDate;
    @Size(max = 64, message = "地区长度不能超过64字符")
    private String region;
    @Size(max = 128, message = "来源长度不能超过128字符")
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
