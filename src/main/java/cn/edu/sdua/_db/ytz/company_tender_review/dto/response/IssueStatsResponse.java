package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

import java.util.List;

public class IssueStatsResponse {
    private Long total;
    private Long pending;
    private Long handling;
    private Long resolved;
    private Long ignored;
    private List<SeverityCount> bySeverity;
    private List<TypeCount> byType;

    public static class SeverityCount {
        private Integer severity;
        private String severityName;
        private Long total;
        private Long resolved;
        public Long getResolved() {
            return resolved;
        }
        public Integer getSeverity() {
            return severity;
        }
        public String getSeverityName() {
            return severityName;
        }
        public Long getTotal() {
            return total;
        }
        public void setResolved(Long resolved) {
            this.resolved = resolved;
        }
        public void setSeverity(Integer severity) {
            this.severity = severity;
        }
        public void setSeverityName(String severityName) {
            this.severityName = severityName;
        }
        public void setTotal(Long total) {
            this.total = total;
        }
    }

    public static class TypeCount {
        private Integer issueType;
        private String typeName;
        private Long total;
        public Integer getIssueType() {
            return issueType;
        }
        public Long getTotal() {
            return total;
        }
        public String getTypeName() {
            return typeName;
        }
        public void setIssueType(Integer issueType) {
            this.issueType = issueType;
        }
        public void setTotal(Long total) {
            this.total = total;
        }public void setTypeName(String typeName) {
            this.typeName = typeName;
        }
    }
    public List<SeverityCount> getBySeverity() {
        return bySeverity;
    }
    public List<TypeCount> getByType() {
        return byType;
    }
    public Long getHandling() {
        return handling;
    }
    public Long getIgnored() {
        return ignored;
    }
    public Long getPending() {
        return pending;
    }
    public Long getResolved() {
        return resolved;
    }
    public Long getTotal() {
        return total;
    }
    public void setBySeverity(List<SeverityCount> bySeverity) {
        this.bySeverity = bySeverity;
    }
    public void setByType(List<TypeCount> byType) {
        this.byType = byType;
    }
    public void setHandling(Long handling) {
        this.handling = handling;
    }
    public void setIgnored(Long ignored) {
        this.ignored = ignored;
    }
    public void setPending(Long pending) {
        this.pending = pending;
    }
    public void setResolved(Long resolved) {
        this.resolved = resolved;
    }
    public void setTotal(Long total) {
        this.total = total;
    }
}
