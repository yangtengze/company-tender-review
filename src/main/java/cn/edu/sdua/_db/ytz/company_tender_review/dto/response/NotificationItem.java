package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

public class NotificationItem {
    private Long id;
    private Integer type;
    private String typeName;
    private String title;
    private String content;
    private String refType;
    private Long refId;
    private Integer isRead;
    private String createdAt;
    public String getContent() {
        return content;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public Long getId() {
        return id;
    }
    public Integer getIsRead() {
        return isRead;
    }
    public Long getRefId() {
        return refId;
    }
    public String getRefType() {
        return refType;
    }
    public String getTitle() {
        return title;
    }
    public Integer getType() {
        return type;
    }
    public String getTypeName() {
        return typeName;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }
    public void setRefId(Long refId) {
        this.refId = refId;
    }
    public void setRefType(String refType) {
        this.refType = refType;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setType(Integer type) {
        this.type = type;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
