package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CaseCreateRequest {
    @NotBlank
    @Size(max = 256)
    private String title;
    @NotNull
    @Min(1)
    @Max(4)
    private Integer caseType;
    @Size(max = 128)
    private String source;
    private LocalDate caseDate;
    @Min(1)
    @Max(5)
    private Integer projectType;
    @Min(1)
    @Max(6)
    private Integer issueType;
    @NotBlank
    private String description;
    private String keyFindings;
    private String outcome;
    private String lesson;
    private List<String> keywords;
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
