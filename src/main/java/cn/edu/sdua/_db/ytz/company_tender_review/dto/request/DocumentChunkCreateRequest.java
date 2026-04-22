package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Schema(description = "文档切块创建请求")
public class DocumentChunkCreateRequest {

    @Schema(description = "客户端临时块ID（用于批量入库后回填 parentId）")
    private Long clientChunkId;

    @Schema(description = "客户端临时父块ID（用于批量入库后回填 parentId）")
    private Long clientParentChunkId;

    @Schema(description = "文档ID")
    @NotNull
    private Long docId;

    @Schema(description = "父块ID")
    private Long parentId;

    @Schema(description = "块类型")
    @NotBlank
    private String chunkType;

    @Schema(description = "块层级")
    private Integer chunkLevel;

    @Schema(description = "块索引")
    @NotNull
    private Integer chunkIndex;

    @Schema(description = "块内容")
    @NotBlank
    private String content;

    @Schema(description = "Token数")
    private Integer tokenCount;

    @Schema(description = "向量库中的ID")
    private String vectorId;

    @Schema(description = "元数据")
    private Map<String, Object> metadata;

    public Long getClientChunkId() { return clientChunkId; }
    public void setClientChunkId(Long clientChunkId) { this.clientChunkId = clientChunkId; }

    public Long getClientParentChunkId() { return clientParentChunkId; }
    public void setClientParentChunkId(Long clientParentChunkId) { this.clientParentChunkId = clientParentChunkId; }

    public Long getDocId() { return docId; }
    public void setDocId(Long docId) { this.docId = docId; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public String getChunkType() { return chunkType; }
    public void setChunkType(String chunkType) { this.chunkType = chunkType; }

    public Integer getChunkLevel() { return chunkLevel; }
    public void setChunkLevel(Integer chunkLevel) { this.chunkLevel = chunkLevel; }

    public Integer getChunkIndex() { return chunkIndex; }
    public void setChunkIndex(Integer chunkIndex) { this.chunkIndex = chunkIndex; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getTokenCount() { return tokenCount; }
    public void setTokenCount(Integer tokenCount) { this.tokenCount = tokenCount; }

    public String getVectorId() { return vectorId; }
    public void setVectorId(String vectorId) { this.vectorId = vectorId; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
