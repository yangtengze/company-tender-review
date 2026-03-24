package cn.edu.sdua._db.ytz.company_tender_review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class OrgQueryRequest {
    @Min(1)
    @Max(4)
    private Integer type;

    @Min(0)
    @Max(1)
    private Integer status = 1;

    private Boolean tree = false;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getTree() {
        return tree;
    }

    public void setTree(Boolean tree) {
        this.tree = tree;
    }
}
