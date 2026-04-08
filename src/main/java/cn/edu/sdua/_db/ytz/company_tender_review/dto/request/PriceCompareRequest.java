package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class PriceCompareRequest {
    @NotBlank(message = "物品编码不能为空")
    private String itemCode;
    @NotBlank(message = "地区不能为空")
    private String region;
    @Min(value = 1, message = "月份数最小为1")
    @Max(value = 60, message = "月份数最大为60")
    private Integer months = 12;
    public String getItemCode() {
        return itemCode;
    }
    public Integer getMonths() {
        return months;
    }
    public String getRegion() {
        return region;
    }
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    public void setMonths(Integer months) {
        this.months = months;
    }
    public void setRegion(String region) {
        this.region = region;
    }
}
