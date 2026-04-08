package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LawCreateRequest {

    @NotBlank(message = "法规标题不能为空")
    @Size(max = 256, message = "法规标题长度不能超过256字符")
    private String title;


    @Size(max = 128, message = "简称长度不能超过128字符")
    private String shortName;


    @Size(max = 128, message = "法规编号长度不能超过128字符")
    private String lawNo;


    @NotNull(message = "法规类别不能为空")
    @Min(value = 1, message = "法规类别最小为1")
    @Max(value = 5, message = "法规类别最大为5")
    private Integer category;


    @Size(max = 128, message = "发布机构长度不能超过128字符")
    private String issuer;

    private LocalDate issueDate;
    private LocalDate effectiveDate;


    @NotBlank(message = "法规正文不能为空")
    private String fullText;


    @Size(max = 2000, message = "摘要长度不能超过2000字符")
    private String summary;


    @jakarta.validation.Valid
    private List<String> keywords;
    
    public Integer getCategory() {
        return category;
    }
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }
    public String getFullText() {
        return fullText;
    }
    public LocalDate getIssueDate() {
        return issueDate;
    }
    public String getIssuer() {
        return issuer;
    }
    public List<String> getKeywords() {
        return keywords;
    }
    public String getLawNo() {
        return lawNo;
    }
    public String getShortName() {
        return shortName;
    }
    public String getSummary() {
        return summary;
    }
    public String getTitle() {
        return title;
    }
    public void setCategory(Integer category) {
        this.category = category;
    }
    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    public void setFullText(String fullText) {
        this.fullText = fullText;
    }
    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
    public void setLawNo(String lawNo) {
        this.lawNo = lawNo;
    }
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
