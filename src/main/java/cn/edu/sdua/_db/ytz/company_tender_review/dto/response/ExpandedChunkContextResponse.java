package cn.edu.sdua._db.ytz.company_tender_review.dto.response;

import java.util.List;

public class ExpandedChunkContextResponse {
    private Long docId;
    private String vectorId;
    private Long hitChunkId;
    private Integer hitChunkIndex;
    private Long titleChunkId;
    private String title;
    private Integer titleLevel;
    private Integer sectionStartIndex;
    private Integer sectionEndIndex;
    private String contextText;
    private List<DocumentChunkNode> chunks;

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public String getVectorId() {
        return vectorId;
    }

    public void setVectorId(String vectorId) {
        this.vectorId = vectorId;
    }

    public Long getHitChunkId() {
        return hitChunkId;
    }

    public void setHitChunkId(Long hitChunkId) {
        this.hitChunkId = hitChunkId;
    }

    public Integer getHitChunkIndex() {
        return hitChunkIndex;
    }

    public void setHitChunkIndex(Integer hitChunkIndex) {
        this.hitChunkIndex = hitChunkIndex;
    }

    public Long getTitleChunkId() {
        return titleChunkId;
    }

    public void setTitleChunkId(Long titleChunkId) {
        this.titleChunkId = titleChunkId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTitleLevel() {
        return titleLevel;
    }

    public void setTitleLevel(Integer titleLevel) {
        this.titleLevel = titleLevel;
    }

    public Integer getSectionStartIndex() {
        return sectionStartIndex;
    }

    public void setSectionStartIndex(Integer sectionStartIndex) {
        this.sectionStartIndex = sectionStartIndex;
    }

    public Integer getSectionEndIndex() {
        return sectionEndIndex;
    }

    public void setSectionEndIndex(Integer sectionEndIndex) {
        this.sectionEndIndex = sectionEndIndex;
    }

    public String getContextText() {
        return contextText;
    }

    public void setContextText(String contextText) {
        this.contextText = contextText;
    }

    public List<DocumentChunkNode> getChunks() {
        return chunks;
    }

    public void setChunks(List<DocumentChunkNode> chunks) {
        this.chunks = chunks;
    }
}
