package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

import java.util.List;
import java.util.Map;

public class DocumentChunkNode {
    private Long id;
    private Long docId;
    private Long parentId;
    private String chunkType;
    private Integer chunkLevel;
    private Integer chunkIndex;
    private String content;
    private Integer tokenCount;
    private Map<String, Object> metadata;
    private List<DocumentChunkNode> children;
    public List<DocumentChunkNode> getChildren() {
        return children;
    }
    public Integer getChunkIndex() {
        return chunkIndex;
    }
    public Integer getChunkLevel() {
        return chunkLevel;
    }
    public String getChunkType() {
        return chunkType;
    }
    public String getContent() {
        return content;
    }
    public Long getDocId() {
        return docId;
    }
    public Long getId() {
        return id;
    }
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    public Long getParentId() {
        return parentId;
    }
    public Integer getTokenCount() {
        return tokenCount;
    }
    public void setChildren(List<DocumentChunkNode> children) {
        this.children = children;
    }
    public void setChunkIndex(Integer chunkIndex) {
        this.chunkIndex = chunkIndex;
    }
    public void setChunkLevel(Integer chunkLevel) {
        this.chunkLevel = chunkLevel;
    }
    public void setChunkType(String chunkType) {
        this.chunkType = chunkType;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setDocId(Long docId) {
        this.docId = docId;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    public void setTokenCount(Integer tokenCount) {
        this.tokenCount = tokenCount;
    }
}
