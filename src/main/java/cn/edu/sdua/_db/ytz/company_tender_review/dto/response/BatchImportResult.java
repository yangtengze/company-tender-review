package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

import java.util.List;

public class BatchImportResult {
    private Integer total;
    private Integer success;
    private Integer updated;
    private Integer failed;
    private List<String> errors;
    public List<String> getErrors() {
        return errors;
    }
    public Integer getFailed() {
        return failed;
    }
    public Integer getSuccess() {
        return success;
    }
    public Integer getTotal() {
        return total;
    }
    public Integer getUpdated() {
        return updated;
    }
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    public void setFailed(Integer failed) {
        this.failed = failed;
    }
    public void setSuccess(Integer success) {
        this.success = success;
    }
    public void setTotal(Integer total) {
        this.total = total;
    }
    public void setUpdated(Integer updated) {
        this.updated = updated;
    }
}
