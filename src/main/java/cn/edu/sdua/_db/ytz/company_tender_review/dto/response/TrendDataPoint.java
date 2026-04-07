package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

public class TrendDataPoint {
    private String date;
    private Integer total;
    private Integer resolved;
    private Integer pending;
    public String getDate() {
        return date;
    }
    public Integer getPending() {
        return pending;
    }
    public Integer getResolved() {
        return resolved;
    }
    public Integer getTotal() {
        return total;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setPending(Integer pending) {
        this.pending = pending;
    }
    public void setResolved(Integer resolved) {
        this.resolved = resolved;
    }
    public void setTotal(Integer total) {
        this.total = total;
    }
}
