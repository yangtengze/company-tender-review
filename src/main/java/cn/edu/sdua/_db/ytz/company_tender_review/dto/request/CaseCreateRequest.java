package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CaseCreateRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 256, message = "标题长度不能超过256字符")
    private String title;

    @NotNull(message = "案例类型不能为空")
    @Min(value = 1, message = "案例类型最小为1")
    @Max(value = 4, message = "案例类型最大为4")
    private Integer caseType;

    @Size(max = 128, message = "来源长度不能超过128字符")
    private String source;
    private LocalDate caseDate;

    @Min(value = 1, message = "项目类型最小为1")
    @Max(value = 5, message = "项目类型最大为5")
    private Integer projectType;

    @Min(value = 1, message = "问题类型最小为1")
    @Max(value = 6, message = "问题类型最大为6")
    private Integer issueType;

    @NotBlank(message = "案例描述不能为空")
    private String description;
    private String keyFindings;
    private String outcome;
    private String lesson;

    @jakarta.validation.Valid
    private List<String> keywords;
    @jakarta.validation.Valid
    private List<Long> refLawIds;
    public LocalDate getCaseDate() {
        return caseDate;
    }
    public Integer getCaseType() {
        return caseType;
    }
    public String getDescription() {
        return description;
    }
    public Integer getIssueType() {
        return issueType;
    }
    public String getKeyFindings() {
        return keyFindings;
    }
    public List<String> getKeywords() {
        return keywords;
    }
    public String getLesson() {
        return lesson;
    }
    public String getOutcome() {
        return outcome;
    }
    public Integer getProjectType() {
        return projectType;
    }
    public List<Long> getRefLawIds() {
        return refLawIds;
    }
    public String getSource() {
        return source;
    }
    public String getTitle() {
        return title;
    }
    public void setCaseDate(LocalDate caseDate) {
        this.caseDate = caseDate;
    }
    public void setCaseType(Integer caseType) {
        this.caseType = caseType;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setIssueType(Integer issueType) {
        this.issueType = issueType;
    }
    public void setKeyFindings(String keyFindings) {
        this.keyFindings = keyFindings;
    }
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
    public void setLesson(String lesson) {
        this.lesson = lesson;
    }
    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
    public void setProjectType(Integer projectType) {
        this.projectType = projectType;
    }
    public void setRefLawIds(List<Long> refLawIds) {
        this.refLawIds = refLawIds;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
