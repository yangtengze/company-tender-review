package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

public class LLMLogItem {
    private Long id;
    private Long taskId;
    private String callType;
    private String modelName;
    private String modelVersion;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
    private Integer latencyMs;
    private Integer status;
    private String errorMsg;
    private String createdAt;
    public String getCallType() {
        return callType;
    }
    public Integer getCompletionTokens() {
        return completionTokens;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public String getErrorMsg() {
        return errorMsg;
    }
    public Long getId() {
        return id;
    }
    public Integer getLatencyMs() {
        return latencyMs;
    }
    public String getModelName() {
        return modelName;
    }
    public String getModelVersion() {
        return modelVersion;
    }
    public Integer getPromptTokens() {
        return promptTokens;
    }
    public Integer getStatus() {
        return status;
    }
    public Long getTaskId() {
        return taskId;
    }
    public Integer getTotalTokens() {
        return totalTokens;
    }
    public void setCallType(String callType) {
        this.callType = callType;
    }
    public void setCompletionTokens(Integer completionTokens) {
        this.completionTokens = completionTokens;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setLatencyMs(Integer latencyMs) {
        this.latencyMs = latencyMs;
    }
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }
    public void setPromptTokens(Integer promptTokens) {
        this.promptTokens = promptTokens;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    public void setTotalTokens(Integer totalTokens) {
        this.totalTokens = totalTokens;
    }
}
