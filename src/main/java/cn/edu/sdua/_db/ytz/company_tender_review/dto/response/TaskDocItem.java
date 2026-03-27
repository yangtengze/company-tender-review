package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

public class TaskDocItem {
    private Long docId;
    private String docName;
    private Integer docType;
    private String docRole;
    private String fileExt;
    private Long fileSize;

    public Long getDocId() { return docId; }
    public void setDocId(Long docId) { this.docId = docId; }
    public String getDocName() { return docName; }
    public void setDocName(String docName) { this.docName = docName; }
    public Integer getDocType() { return docType; }
    public void setDocType(Integer docType) { this.docType = docType; }
    public String getDocRole() { return docRole; }
    public void setDocRole(String docRole) { this.docRole = docRole; }
    public String getFileExt() { return fileExt; }
    public void setFileExt(String fileExt) { this.fileExt = fileExt; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
}

