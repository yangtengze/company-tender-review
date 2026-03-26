package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

public class ExtractResultResponse {
    private Long docId;
    private String extractType;
    private String modelName;
    private Object resultJson;
    private String createdAt;
    private String updatedAt;

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public String getExtractType() {
        return extractType;
    }

    public void setExtractType(String extractType) {
        this.extractType = extractType;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Object getResultJson() {
        return resultJson;
    }

    public void setResultJson(Object resultJson) {
        this.resultJson = resultJson;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

