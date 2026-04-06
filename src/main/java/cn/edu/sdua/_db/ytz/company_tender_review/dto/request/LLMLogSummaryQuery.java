package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import org.springframework.format.annotation.DateTimeFormat;


public class LLMLogSummaryQuery {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private String dateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private String dateTo;
    public String getDateFrom() {
        return dateFrom;
    }
    public String getDateTo() {
        return dateTo;
    }
    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }
    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }
}
