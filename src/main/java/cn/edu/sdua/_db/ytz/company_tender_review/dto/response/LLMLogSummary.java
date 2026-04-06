package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

public class LLMLogSummary {
    private String modelName;
    private Integer callCount;
    private Long tokensTotal;
    private Long promptTokens;
    private Long completionTokens;
    private Integer failedCount;
    private Double failRate;
    private Double avgLatencyMs;
    public Double getAvgLatencyMs() {
        return avgLatencyMs;
    }
    public Integer getCallCount() {
        return callCount;
    }
    public Long getCompletionTokens() {
        return completionTokens;
    }
    public Double getFailRate() {
        return failRate;
    }
    public Integer getFailedCount() {
        return failedCount;
    }
    public String getModelName() {
        return modelName;
    }
    public Long getPromptTokens() {
        return promptTokens;
    }
    public Long getTokensTotal() {
        return tokensTotal;
    }
    public void setAvgLatencyMs(Double avgLatencyMs) {
        this.avgLatencyMs = avgLatencyMs;
    }
    public void setCallCount(Integer callCount) {
        this.callCount = callCount;
    }
    public void setCompletionTokens(Long completionTokens) {
        this.completionTokens = completionTokens;
    }
    public void setFailRate(Double failRate) {
        this.failRate = failRate;
    }
    public void setFailedCount(Integer failedCount) {
        this.failedCount = failedCount;
    }
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    public void setPromptTokens(Long promptTokens) {
        this.promptTokens = promptTokens;
    }
    public void setTokensTotal(Long tokensTotal) {
        this.tokensTotal = tokensTotal;
    }
}
