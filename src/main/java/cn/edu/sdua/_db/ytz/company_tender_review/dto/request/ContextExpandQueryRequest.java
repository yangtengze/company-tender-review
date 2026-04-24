package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "文档上下文扩展查询参数")
public class ContextExpandQueryRequest {

    @Schema(description = "向量库返回的 vectorId")
    @NotBlank
    @Size(max = 128)
    private String vectorId;

    public String getVectorId() {
        return vectorId;
    }

    public void setVectorId(String vectorId) {
        this.vectorId = vectorId;
    }
}
